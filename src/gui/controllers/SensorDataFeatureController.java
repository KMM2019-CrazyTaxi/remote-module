package gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import remote.Car;
import remote.listeners.DataListener;

/**
 * Sensor Data Grid-feature controller. This is the main controller class for the Sensor Data Grid-feature.
 * @author Henrik Nilsson
 */
public class SensorDataFeatureController {

    @FXML private Text sensorDataAccelerationX;
    @FXML private Text sensorDataAccelerationY;
    @FXML private Text sensorDataAccelerationZ;
    @FXML private Text sensorDataSpeed;
    @FXML private Text sensorDataDistance;

    /**
     * Initialize method called by JavaFX thread when JavaFX-objects are constructed and ready.
     * This method sets up listeners, styles the input dialog and adds formatting to the input dialog.
     */
    public void initialize() {
        // Add listeners
        DataListener<Integer> accXListener = (Integer data) -> {
            sensorDataAccelerationX.setText(String.valueOf(data));
        };
        DataListener<Integer> accYListener = (Integer data) -> {
            sensorDataAccelerationY.setText(String.valueOf(data));
        };
        DataListener<Integer> accZListener = (Integer data) -> {
            sensorDataAccelerationZ.setText(String.valueOf(data));
        };
        DataListener<Integer> speedListener = (Integer data) -> {
            sensorDataSpeed.setText(String.valueOf(data));
        };
        DataListener<Integer> distanceListener = (Integer data) -> {
            sensorDataDistance.setText(String.valueOf(data));
        };

        Car.getInstance().accelerationX.subscribe(accXListener);
        Car.getInstance().accelerationY.subscribe(accYListener);
        Car.getInstance().accelerationZ.subscribe(accZListener);
        Car.getInstance().distance.subscribe(distanceListener);
        Car.getInstance().speed.subscribe(speedListener);
    }
}
