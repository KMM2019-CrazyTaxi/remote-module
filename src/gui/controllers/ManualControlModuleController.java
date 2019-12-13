package gui.controllers;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import enums.ControlMode;
import remote.Car;
import remote.Server;
import remote.listeners.DataListener;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manual Control-module Controller class. This is the main controller class for the Manual Control-module.
 *
 * @author Henrik Nilsson
 */
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
    @FXML private HBox manualIndicator;

    @FXML private Button wasdButton;

    /**
     * ManualControlModuleController constructor.
     */
    public ManualControlModuleController() {
        wasdState = new AtomicBoolean(false);

        speed = 0;
        turn = 0;

        wDown = false;
        aDown = false;
        sDown = false;
        dDown = false;
    }

    /**
     * Initialize method called by JavaFX thread when JavaFX-objects are constructed and ready.
     * This method sets up all listeners.
     */
    public void initialize() {
        // Create and add remote data listeners
        DataListener<Integer> speedListener = (Integer speed) -> {
            manualControlModuleSpeed.setText(String.valueOf(speed));
        };

        DataListener<ControlMode> modeListener = (ControlMode mode) -> {
            wasdState.set(false);

            fullAutoIndicator.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), false);
            manualIndicator.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), false);

            switch (mode) {
                case MANUAL:
                    manualIndicator.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), true);
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

    /**
     * Handle click in the Emergiency Stop-button. Request emergiency stop and pull the server.
     * @param mouseEvent Triggering event
     */
    public void handleEmergencyStopButtonClick(MouseEvent mouseEvent) {
        Server.getInstance().getRequestBuilder().addEmergencyStopRequest();
        Server.getInstance().pull();
    }

    /**
     * Handle click in the Arrow Up-button. Increase the speed and pull the server.
     * @param mouseEvent Triggering event
     */
    synchronized public void handleUpArrowClick(MouseEvent mouseEvent) {
        // Ignore request if car is in autonomous mode
        if (ControlMode.FULL_AUTO == Car.getInstance().controlMode.get())
            return;

        speed += CLICK_SPEED_STEP;

        if (speed > MAX_SPEED)
            speed = MAX_SPEED - 1;

        Server.getInstance().getRequestBuilder().addSetMaxSpeedRequest(speed);
        Server.getInstance().pull();
    }

    /**
     * Handle click in the Arrow Left-button. Turn to the left and pull the server.
     * @param mouseEvent Triggering event
     */
    synchronized public void handleLeftArrowClick(MouseEvent mouseEvent) {
        // Ignore request if car is in autonomous mode
        if (ControlMode.FULL_AUTO == Car.getInstance().controlMode.get())
            return;

        turn -= CLICK_TURN_STEP;

        if (turn < -MAX_TURN)
            turn = -MAX_TURN + 1;

        Server.getInstance().getRequestBuilder().addTurnRequest(turn);
        Server.getInstance().pull();
    }

    /**
     * Handle click in the Arrow Right-button. Turn to the right and pull the server.
     * @param mouseEvent Triggering event
     */
    synchronized public void handleRightArrowClick(MouseEvent mouseEvent) {
        // Ignore request if car is in autonomous mode
        if (ControlMode.FULL_AUTO == Car.getInstance().controlMode.get())
            return;

        turn += CLICK_TURN_STEP;

        if (turn > MAX_TURN)
            turn = MAX_TURN - 1;

        Server.getInstance().getRequestBuilder().addTurnRequest(turn);
        Server.getInstance().pull();
    }

    /**
     * Handle click in the Arrow Down-button. Decrease the speed and pull the server.
     * @param mouseEvent Triggering event
     */
    synchronized public void handleDownArrowClick(MouseEvent mouseEvent) {
        // Ignore request if car is in autonomous mode
        if (ControlMode.FULL_AUTO == Car.getInstance().controlMode.get())
            return;

        speed -= CLICK_SPEED_STEP;

        if (speed < -MAX_SPEED)
            speed = -MAX_SPEED + 1;

        Server.getInstance().getRequestBuilder().addSetMaxSpeedRequest(speed);
        Server.getInstance().pull();
    }

    /**
     * Handle click in the WASD-button. This toggles the WASD input state.
     * @param mouseEvent Triggering event
     */
    public void handleWASDToggleClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() != MouseButton.PRIMARY)
            return;

        wasdState.set(!wasdState.get());
        wasdButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), wasdState.get());

        if (wasdState.get()) {
            (new Thread(this::wasdControl)).start();
        }
    }

    /**
     * Handle key pressed key event to track WASD (and E) buttons. This event sets the key state of the corresponding key.
     * Input is ignored if the WASD state is not true (tracking)
     * @param keyEvent Triggering event
     */
    public void handleKeyPressed(KeyEvent keyEvent) {
        // Ignore key press if not in key input mode
        if (!wasdState.get())
            return;

        keyEvent.consume();

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

    /**
     * Handle key released key event to track WASD (and E) buttons. This event sets the key state of the corresponding key.
     * Input is ignored if the WASD state is not true (tracking)
     * @param keyEvent Triggering event
     */
    public void handleKeyReleased(KeyEvent keyEvent) {
        // Ignore key press if not in key input mode
        if (!wasdState.get())
            return;

        keyEvent.consume();

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

    /**
     * Handle click in the Full Auto Mode-button. Add Full Mode request and pulls the server.
     * @param mouseEvent Triggering event
     */
    public void handleFullModeButtonClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() != MouseButton.PRIMARY)
            return;
        Server.getInstance().getRequestBuilder().addSetModeRequest(ControlMode.FULL_AUTO);
        Car.getInstance().controlMode.poll();
        Server.getInstance().pull();
    }

    /**
     * Handle click in the Manual Mode-button. Add Manual Mode request and pulls the server.
     * @param mouseEvent Triggering event
     */
    public void handleManualModeButtonClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() != MouseButton.PRIMARY)
            return;
        Server.getInstance().getRequestBuilder().addSetModeRequest(ControlMode.MANUAL);
        Car.getInstance().controlMode.poll();
        Server.getInstance().pull();
    }

    /**
     * WASD Control method. This mehtod is meant to be run on a separate thread to continually controll the car when the
     * WASD state is true (tracking). Pulls the server every 100ms with updated speed and turn values.
     */
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
