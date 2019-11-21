package gui.controllers;

import enums.ControlMode;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import remote.Car;
import remote.Server;
import remote.listeners.DataListener;

public class ManualControlModuleController {
    public final static int CLICK_SPEED_STEP = 10;
    public final static int CLICK_TURN_STEP = 10;

    private boolean WASDState;

    @FXML private Text manualControlModuleSpeed;

    @FXML private HBox fullAutoIndicator;
    @FXML private HBox halfAutoIndicator;
    @FXML private HBox manualIndicator;

    @FXML private Button WASDButton;

    public ManualControlModuleController() {
        WASDState = false;
    }

    public void initialize() {
        // Create and add remote data listeners
        DataListener<Integer> speedListener = (Integer speed) -> {
            manualControlModuleSpeed.setText(String.valueOf(speed));
        };

        DataListener<ControlMode> modeListener = (ControlMode mode) -> {
            fullAutoIndicator.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), false);
            halfAutoIndicator.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), false);
            manualIndicator.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), false);

            switch (mode) {
                case MANUAL:
                    manualIndicator.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), true);
                    break;
                case SEMI_AUTO:
                    halfAutoIndicator.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), true);
                    break;
                case FULL_AUTO:
                    fullAutoIndicator.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), true);
                    break;
            }
        };

        // Subscribe listeners
        Car.getInstance().speed.subscribe(speedListener);
        Car.getInstance().controlMode.subscribe(modeListener);
    }

    public void handleEmergencyStopButtonClick(MouseEvent mouseEvent) {
        Server.getInstance().getRequestBuilder().addEmergencyStopRequest();
    }

    public void handleUpArrowClick(MouseEvent mouseEvent) {
        // Ignore request if car is in autonomous mode
        if (ControlMode.FULL_AUTO == Car.getInstance().controlMode.get())
            return;

        // TODO Change to desired speed?
        int currentSpeed = Car.getInstance().speed.get();
        Server.getInstance().getRequestBuilder().addSetMaxSpeedRequest(currentSpeed + CLICK_SPEED_STEP);
    }

    public void handleLeftArrowClick(MouseEvent mouseEvent) {
        // Ignore request if car is in autonomous mode
        if (ControlMode.FULL_AUTO == Car.getInstance().controlMode.get())
            return;

        // TODO Uncomment when turn is implemented in Car-model
        /*int currentTurn = Car.getInstance().turn.get();

        Server.getInstance().getRequestBuilder().addSetMaxSpeedRequest(currentTurn + MOUSE_CLICK_SET_TURN);*/
    }

    public void handleRightArrowClick(MouseEvent mouseEvent) {
        // Ignore request if car is in autonomous mode
        if (ControlMode.FULL_AUTO == Car.getInstance().controlMode.get())
            return;

        // TODO Uncomment when turn is implemented in Car-model
        /*int currentTurn = Car.getInstance().turn.get();

        Server.getInstance().getRequestBuilder().addSetMaxSpeedRequest(currentTurn + MOUSE_CLICK_SET_TURN);*/
    }

    public void handleDownArrowClick(MouseEvent mouseEvent) {
        // Ignore request if car is in autonomous mode
        if (ControlMode.FULL_AUTO == Car.getInstance().controlMode.get())
            return;

        // TODO Change to desired speed?
        int currentSpeed = Car.getInstance().speed.get();
        Server.getInstance().getRequestBuilder().addSetMaxSpeedRequest(currentSpeed - CLICK_SPEED_STEP);
    }

    public void handleWASDToggleClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() != MouseButton.PRIMARY)
            return;

        WASDState = !WASDState;
        WASDButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), WASDState);
    }

    public void handleKeyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getText()) {
            case "w":
                Server.getInstance().getRequestBuilder().addSetMaxSpeedRequest(CLICK_SPEED_STEP);
                break;
            case "a":
                Server.getInstance().getRequestBuilder().addTurnRequest(-CLICK_TURN_STEP);
                break;
            case "s":
                Server.getInstance().getRequestBuilder().addSetMaxSpeedRequest(-CLICK_SPEED_STEP);
                break;
            case "d":
                Server.getInstance().getRequestBuilder().addTurnRequest(CLICK_TURN_STEP);
                break;
            case "e":
                Server.getInstance().getRequestBuilder().addEmergencyStopRequest();
                break;
        }

        Server.getInstance().pull();
    }

    public void handleKeyReleased(KeyEvent keyEvent) {
        switch (keyEvent.getText()) {
            case "w":
            case "s":
                Server.getInstance().getRequestBuilder().addSetMaxSpeedRequest(0);
                break;
            case "a":
            case "d":
                Server.getInstance().getRequestBuilder().addTurnRequest(0);
                break;
        }

        Server.getInstance().pull();
    }

    public void handleFullModeButtonClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() != MouseButton.PRIMARY)
            return;
        Server.getInstance().getRequestBuilder().addSetModeRequest(ControlMode.FULL_AUTO);
    }

    public void handlHalfModeButtonClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() != MouseButton.PRIMARY)
            return;
        Server.getInstance().getRequestBuilder().addSetModeRequest(ControlMode.SEMI_AUTO);
    }

    public void handleManualModeButtonClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() != MouseButton.PRIMARY)
            return;
        Server.getInstance().getRequestBuilder().addSetModeRequest(ControlMode.MANUAL);
    }
}
