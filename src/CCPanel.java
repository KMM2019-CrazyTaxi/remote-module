import enums.PacketCommand;
import enums.PacketType;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import logging.DataLogger;
import remote.Car;
import remote.FixedTimePoller;
import remote.Server;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class CCPanel extends Application {
    private static final int FAST_POLL_TIME = 250;
    private static final int MEDIUM_POLL_TIME = 750;
    private static final int SLOW_POLL_TIME = 1500;

    private DataLogger<Float> tempratureLog;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        Stage root = FXMLLoader.load(getClass().getResource("gui/fxml/UI.fxml"));
        root.getScene().addEventFilter(KeyEvent.KEY_PRESSED, root.getScene().lookup("#manualControlModule").getOnKeyPressed());
        root.getScene().addEventFilter(KeyEvent.KEY_RELEASED, root.getScene().lookup("#manualControlModule").getOnKeyReleased());
        root.show();

        initialize();
    }

    private void initialize() {
        Car.getInstance().lateBindSubscribers();

        addAlerts();
        addPollers();
        addListeners();
        addLoggers();

        Car.getInstance().aliveStatus.subscribe(CCPanel::initialPull);
    }

    private void addLoggers() {
        tempratureLog = new DataLogger<>();
        Car.getInstance().temperature.subscribe(tempratureLog);
    }

    private void addListeners() {
        // Set alive status to false when getting a disconnect acknowledgement
        Server.getInstance().addResponsListener(o -> {
            if (o.getCommand() == PacketCommand.DISCONNECT_ACKNOWLEDGEMENT) {
                Car.getInstance().aliveStatus.update(false);
            }
        });
    }

    private static void initialPull(boolean alive) {
        if (alive) {
            // Control params
            Car.getInstance().turningParams.poll();
            Car.getInstance().parkingParams.poll();
            Car.getInstance().stoppingParams.poll();
            Car.getInstance().lineSpeedParams.poll();
            Car.getInstance().lineAngleParams.poll();

            // Control mode
            Car.getInstance().controlMode.poll();

            Server.getInstance().pull();
        }
    }

    private void addPollers() {
        FixedTimePoller fastPoller = new FixedTimePoller(FAST_POLL_TIME);
        FixedTimePoller mediumPoller = new FixedTimePoller(MEDIUM_POLL_TIME);
        FixedTimePoller slowPoller = new FixedTimePoller(SLOW_POLL_TIME);

        // Heartbeat
        fastPoller.add(Car.getInstance().aliveStatus);

        // Sensor data
        fastPoller.add(Car.getInstance().accelerationX);
        fastPoller.add(Car.getInstance().accelerationY);
        fastPoller.add(Car.getInstance().accelerationZ);
        fastPoller.add(Car.getInstance().distance);
        fastPoller.add(Car.getInstance().speed);

        // IR data
        fastPoller.add(Car.getInstance().distanceToLeft);
        fastPoller.add(Car.getInstance().distanceToRight);
        fastPoller.add(Car.getInstance().distanceToStop);

        // Car telemetrics
        slowPoller.add(Car.getInstance().temperature);
    }

    private void addAlerts() {

        // Show Error alert on server exception
        Server.getInstance().addExceptionListener(o -> {
            Platform.runLater( () -> {
                Alert a = new Alert(Alert.AlertType.ERROR, "Server exception:\n" + o.getMessage());
                a.show();
                }
            );
        });

        // Show warning alert on server error
        Server.getInstance().addResponsListener(o -> {
            if (o.getCommand().getType() == PacketType.ERROR){
                Platform.runLater( () -> {
                    Alert a = new Alert(Alert.AlertType.WARNING, "Server error:\n" + o.getCommand());
                    a.show();
                    }
                );
            }
        });
    }

    @Override
    public void stop() throws Exception {
        int id = Server.getInstance().getRequestBuilder().addDisconnectRequest();
        Server.getInstance().addResponsListener(o -> {
            if (o.getId() == id) {
                Car.getInstance().aliveStatus.update(false);
            }
        });

        Server.getInstance().releaseBuilder();
        Server.getInstance().pull();

        // If connection is still alive
        if (Car.getInstance().aliveStatus.get()) {

            // Kill all pollers by setting alive status to false
            System.out.println("FORCE QUITTING");
            Car.getInstance().aliveStatus.update(false);

        }

        writeLog("tempLog.txt", tempratureLog);

        super.stop();
    }

    private void writeLog(String filePath, DataLogger log) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(filePath);
        out.write(log.toString());
        out.close();
    }
}
