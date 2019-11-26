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

import java.util.concurrent.atomic.AtomicBoolean;

public class ManualControlModuleController {
    private final static int CLICK_SPEED_STEP = 10;
    private final static int CLICK_TURN_STEP = 20;

    private final static int KEY_SPEED_STEP = 10;
    private final static int KEY_TURN_STEP = 20;

    private static final int MAX_SPEED = 0x7F;
    private static final int MAX_TURN = 0x7F;

    private volatile boolean wDown;
    private volatile boolean aDown;
    private volatile boolean sDown;
    private volatile boolean dDown;
    private volatile boolean eDown;

    private volatile int speed;
    private volatile int turn;

    private volatile AtomicBoolean wasdState;

    @FXML private Text manualControlModuleSpeed;

    @FXML private HBox fullAutoIndicator;
    @FXML private HBox halfAutoIndicator;
    @FXML private HBox manualIndicator;

    @FXML private Button wasdButton;

    public ManualControlModuleController() {
        wasdState = new AtomicBoolean(false);

        speed = 0;
        turn = 0;

        wDown = false;
        aDown = false;
        sDown = false;
        dDown = false;
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
        Server.getInstance().pull();
    }

    synchronized public void handleUpArrowClick(MouseEvent mouseEvent) {
        // Ignore request if car is in autonomous mode
        if (ControlMode.FULL_AUTO == Car.getInstance().controlMode.get())
            return;

        speed += CLICK_SPEED_STEP;

        Server.getInstance().getRequestBuilder().addSetMaxSpeedRequest(speed);
        Server.getInstance().pull();
    }

    synchronized public void handleLeftArrowClick(MouseEvent mouseEvent) {
        // Ignore request if car is in autonomous mode
        if (ControlMode.FULL_AUTO == Car.getInstance().controlMode.get())
            return;

        turn -= CLICK_TURN_STEP;

        Server.getInstance().getRequestBuilder().addTurnRequest(turn);
        Server.getInstance().pull();
    }

    synchronized public void handleRightArrowClick(MouseEvent mouseEvent) {
        // Ignore request if car is in autonomous mode
        if (ControlMode.FULL_AUTO == Car.getInstance().controlMode.get())
            return;

        turn += CLICK_TURN_STEP;

        Server.getInstance().getRequestBuilder().addTurnRequest(turn);
        Server.getInstance().pull();
    }

    synchronized public void handleDownArrowClick(MouseEvent mouseEvent) {
        // Ignore request if car is in autonomous mode
        if (ControlMode.FULL_AUTO == Car.getInstance().controlMode.get())
            return;

        speed -= CLICK_SPEED_STEP;

        Server.getInstance().getRequestBuilder().addSetMaxSpeedRequest(speed);
        Server.getInstance().pull();
    }

    public void handleWASDToggleClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() != MouseButton.PRIMARY)
            return;

        wasdState.set(!wasdState.get());
        wasdButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), wasdState.get());

        if (wasdState.get()) {
            (new Thread(this::wasdControl)).start();
        }
    }

    public void handleKeyPressed(KeyEvent keyEvent) {
        keyEvent.consume();
        // Ignore key press if not in key input mode
        if (!wasdState.get())
            return;

        switch (keyEvent.getText()) {
            case "w":
                wDown = true;
                break;
            case "a":
                aDown = true;
                break;
            case "s":
                sDown = true;
                break;
            case "d":
                dDown = true;
                break;
            case "e":
                eDown = true;
                break;
        }
    }

    public void handleKeyReleased(KeyEvent keyEvent) {
        keyEvent.consume();
        // Ignore key press if not in key input mode
        if (!wasdState.get())
            return;

        switch (keyEvent.getText()) {
            case "w":
                wDown = false;
                break;
            case "s":
                sDown = false;
                break;
            case "a":
                aDown = false;
                break;
            case "d":
                dDown = false;
                break;
            case "e":
                eDown = false;
                break;
        }
    }

    public void handleFullModeButtonClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() != MouseButton.PRIMARY)
            return;
        Server.getInstance().getRequestBuilder().addSetModeRequest(ControlMode.FULL_AUTO);
        Server.getInstance().pull();
    }

    public void handlHalfModeButtonClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() != MouseButton.PRIMARY)
            return;
        Server.getInstance().getRequestBuilder().addSetModeRequest(ControlMode.SEMI_AUTO);
        Server.getInstance().pull();
    }

    public void handleManualModeButtonClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() != MouseButton.PRIMARY)
            return;
        Server.getInstance().getRequestBuilder().addSetModeRequest(ControlMode.MANUAL);
        Server.getInstance().pull();
    }

    synchronized private void wasdControl() {
        while(wasdState.get()) {
            if ((wDown && sDown) || (!wDown && !sDown)) {
                speed = 0;
            }
            else if (wDown) {
                speed += KEY_SPEED_STEP;
            }
            else if (sDown) {
                speed -= KEY_SPEED_STEP;
            }


            if ((aDown && dDown) || (!aDown && !dDown)) {
                turn = 0;
            }
            else if (aDown) {
                turn = -(MAX_TURN - 1);
            }
            else if (dDown) {
                turn = MAX_TURN - 1;
            }

            if (eDown) {
                turn = 0;
                speed = 0;
                Server.getInstance().getRequestBuilder().addEmergencyStopRequest();
            }

            if (speed > MAX_SPEED)
                speed = MAX_SPEED - 1;

            if(speed < -MAX_SPEED)
                speed = -(MAX_SPEED - 1);

            if (turn > MAX_TURN)
                turn = MAX_TURN - 1;

            if(turn < -MAX_TURN)
                turn = -(MAX_TURN - 1);

            System.out.println("");
            System.out.println("Speed: " + speed);
            System.out.println("Turn:  " + turn);

            Server.getInstance().getRequestBuilder().addSetMaxSpeedRequest(speed);
            Server.getInstance().getRequestBuilder().addTurnRequest(turn);
            Server.getInstance().pull();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
