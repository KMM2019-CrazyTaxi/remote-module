package gui.controllers;

import enums.PIDControllerType;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import remote.Car;
import remote.Server;
import remote.datatypes.PIDParams;
import remote.listeners.DataListener;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Control Parameter-feature Controller class. This is the main controller class for the Control Parameter-module.
 *
 * @author Henrik Nilsson
 */
public class ControlParameterFeatureController implements DataListener<PIDParams> {

    @FXML private GridPane controlGrid;

    @FXML private TextField controlParameterKP;
    @FXML private TextField controlParameterKI;
    @FXML private TextField controlParameterKD;
    @FXML private TextField controlParameterAlpha;
    @FXML private TextField controlParameterBeta;

    @FXML private TextField controlParameterAngle;
    @FXML private TextField controlParameterSpeed;
    @FXML private TextField controlParameterMin;
    @FXML private TextField controlParameterSlope;

    private AtomicBoolean editing;

    public ControlParameterFeatureController() {
        editing = new AtomicBoolean(false);
        Car.getInstance().addLateBind(this::subscribe);
    }

    /**
     * Initialize method called by JavaFX thread when JavaFX-objects are constructed and ready.
     * This sets up all input formatters for parameter inputs.
     */
    public void initialize() {
        // Add formatters and converters
        Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?");

        UnaryOperator<Change> filter = c -> {
            String text = c.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return c ;
            } else {
                return null ;
            }
        };

        StringConverter<Double> converter = new StringConverter<Double>() {
            @Override
            public Double fromString(String s) {
                if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                    return 0.0 ;
                } else {
                    return Double.valueOf(s);
                }
            }

            @Override
            public String toString(Double d) {
                return d.toString();
            }
        };

        TextFormatter<Double> kpFormatter = new TextFormatter<>(converter, 0.0, filter);
        TextFormatter<Double> kiFormatter = new TextFormatter<>(converter, 0.0, filter);
        TextFormatter<Double> kdFormatter = new TextFormatter<>(converter, 0.0, filter);
        TextFormatter<Double> alphaFormatter = new TextFormatter<>(converter, 0.0, filter);
        TextFormatter<Double> betaFormatter = new TextFormatter<>(converter, 0.0, filter);

        TextFormatter<Double> angleFormatter = new TextFormatter<>(converter, 0.0, filter);
        TextFormatter<Double> speedFormatter = new TextFormatter<>(converter, 0.0, filter);
        TextFormatter<Double> minFormatter = new TextFormatter<>(converter, 0.0, filter);
        TextFormatter<Double> slopeFormatter = new TextFormatter<>(converter, 0.0, filter);

        controlParameterKP.setTextFormatter(kpFormatter);
        controlParameterKI.setTextFormatter(kiFormatter);
        controlParameterKD.setTextFormatter(kdFormatter);
        controlParameterAlpha.setTextFormatter(alphaFormatter);
        controlParameterBeta.setTextFormatter(betaFormatter);

        controlParameterAngle.setTextFormatter(angleFormatter);
        controlParameterSpeed.setTextFormatter(speedFormatter);
        controlParameterMin.setTextFormatter(minFormatter);
        controlParameterSlope.setTextFormatter(slopeFormatter);
    }

    /**
     * Handle Update button click event. Toggle input lock and pull the server when updating.
     * @param mouseEvent Triggering event
     */
    public void handleUpdateClicked(MouseEvent mouseEvent) {
        if (editing.get()) {
            PIDParams newParams = getPIDParamsFromInput();
            PIDControllerType subSystem = getParamSubsystem();

            lockInputs();
            editing.set(false);

            Server.getInstance().getRequestBuilder().addSendControlParametersRequest(subSystem, newParams);
            Server.getInstance().releaseBuilder();
            Server.getInstance().pull();
        }
        else {
            unlockInputs();
            editing.set(true);
        }
    }

    /**
     * Get the PIDParams from the input fields.
     * @return New PID Controller params from the current text inputs
     */
    private PIDParams getPIDParamsFromInput() {
        double kp = (Double) controlParameterKP.getTextFormatter().getValue();
        double ki = (Double) controlParameterKI.getTextFormatter().getValue();
        double kd = (Double) controlParameterKD.getTextFormatter().getValue();
        double alpha = (Double) controlParameterAlpha.getTextFormatter().getValue();
        double beta = (Double) controlParameterBeta.getTextFormatter().getValue();

        double angle = (Double) controlParameterAngle.getTextFormatter().getValue();
        double speed = (Double) controlParameterSpeed.getTextFormatter().getValue();
        double min = (Double) controlParameterMin.getTextFormatter().getValue();
        double slope = (Double) controlParameterSlope.getTextFormatter().getValue();

        return new PIDParams(kp, ki, kd, alpha, beta, angle, speed, min, slope);
    }

    /**
     * Get the subsystem represented by this feature by looking at the parent ID.
     * @return Which subsystem type this feature represents
     */
    private PIDControllerType getParamSubsystem() {
        String parentName = controlGrid.getParent().getParent().getId();

        PIDControllerType subSystem = null;

        if (parentName.startsWith("Turning"))
            subSystem = PIDControllerType.TURNING;
        else if (parentName.startsWith("Parking"))
            subSystem = PIDControllerType.PARKING;
        else if (parentName.startsWith("Stopping"))
            subSystem = PIDControllerType.STOPPING;
        else if (parentName.startsWith("LineAngle"))
            subSystem = PIDControllerType.LINE_ANGLE;
        else if (parentName.startsWith("LineSpeed"))
            subSystem = PIDControllerType.LINE_SPEED;

        return subSystem;
    }

    /**
     * Lock parameter inputs.
     */
    private void lockInputs() {
        controlParameterKP.setEditable(false);
        controlParameterKI.setEditable(false);
        controlParameterKD.setEditable(false);
        controlParameterAlpha.setEditable(false);
        controlParameterBeta.setEditable(false);

        controlParameterAngle.setEditable(false);
        controlParameterSpeed.setEditable(false);
        controlParameterMin.setEditable(false);
        controlParameterSlope.setEditable(false);
    }

    /**
     * Unlock parameter inputs.
     */
    private void unlockInputs() {
        controlParameterKP.setEditable(true);
        controlParameterKI.setEditable(true);
        controlParameterKD.setEditable(true);
        controlParameterAlpha.setEditable(true);
        controlParameterBeta.setEditable(true);

        controlParameterAngle.setEditable(true);
        controlParameterSpeed.setEditable(true);
        controlParameterMin.setEditable(true);
        controlParameterSlope.setEditable(true);
    }

    /**
     * Subscribe to changes of parameter data.
     */
    public void subscribe() {
        switch (getParamSubsystem()) {
            case TURNING:
                Car.getInstance().turningParams.subscribe(this);
                break;
            case PARKING:
                Car.getInstance().parkingParams.subscribe(this);
                break;
            case STOPPING:
                Car.getInstance().stoppingParams.subscribe(this);
                break;
            case LINE_ANGLE:
                Car.getInstance().lineAngleParams.subscribe(this);
                break;
            case LINE_SPEED:
                Car.getInstance().lineSpeedParams.subscribe(this);
                break;
        }
    }

    @Override
    public void update(PIDParams data) {
        controlParameterKP.setText(String.valueOf(data.kp));
        controlParameterKI.setText(String.valueOf(data.ki));
        controlParameterKD.setText(String.valueOf(data.kd));
        controlParameterAlpha.setText(String.valueOf(data.alpha));
        controlParameterBeta.setText(String.valueOf(data.beta));

        controlParameterAngle.setText(String.valueOf(data.angleThreshold));
        controlParameterSpeed.setText(String.valueOf(data.speedThreshold));
        controlParameterMin.setText(String.valueOf(data.minValue));
        controlParameterSlope.setText(String.valueOf(data.slope));
    }
}
