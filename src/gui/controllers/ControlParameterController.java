package gui.controllers;

import enums.PIDControlerType;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import remote.Server;
import remote.datatypes.PIDParams;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class ControlParameterController {

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

    public ControlParameterController() {
        editing = new AtomicBoolean(false);
    }

    public void initialize() {
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


    public void handleUpdateClicked(MouseEvent mouseEvent) {
        if (editing.get()) {
            PIDParams newParams = getPIDParamsFromInput();
            PIDControlerType subSystem = getParamSubsystem();

            lockInputs();
            editing.set(false);

            Server.getInstance().getRequestBuilder().addSendControlParametersRequest(subSystem, newParams);
            Server.getInstance().pull();
        }
        else {
            unlockInputs();
            editing.set(true);
        }
    }

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

    private PIDControlerType getParamSubsystem() {
        String parentName = controlGrid.getParent().getParent().getId();

        PIDControlerType subSystem = null;

        if (parentName.startsWith("Turning"))
            subSystem = PIDControlerType.TURNING;
        else if (parentName.startsWith("Parking"))
            subSystem = PIDControlerType.PARKING;
        else if (parentName.startsWith("Stopping"))
            subSystem = PIDControlerType.STOPPING;
        else if (parentName.startsWith("LineAngle"))
            subSystem = PIDControlerType.LINE_ANGLE;
        else if (parentName.startsWith("LineSpeed"))
            subSystem = PIDControlerType.LINE_SPEED;

        return subSystem;
    }

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
}
