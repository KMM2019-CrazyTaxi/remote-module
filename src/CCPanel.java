import enums.PacketType;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import remote.Car;
import remote.FixedTimePoller;
import remote.Server;

public class CCPanel extends Application {
    private static final int FAST_POLL_TIME = 250;
    private static final int MEDIUM_POLL_TIME = 750;
    private static final int SLOW_POLL_TIME = 1500;

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
        addAlerts();
        addPollers();

        Car.getInstance().aliveStatus.subscribe(CCPanel::initialPull);
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

        // Midline data
        mediumPoller.add(Car.getInstance().distanceToMiddle);

        // Car telemetrics
        slowPoller.add(Car.getInstance().temperature);
    }

    private void addAlerts() {
        // Show Error alert on server exception
        Server.getInstance().addExceptionListener(o -> {
            Alert a = new Alert(Alert.AlertType.ERROR, "Server exception:\n" + o.getMessage());
            a.show();
        });

        // Show warning alert on server error
        Server.getInstance().addResponsListener(o -> {
            if (o.getCommand().getType() == PacketType.ERROR){
                Alert a = new Alert(Alert.AlertType.WARNING, "Server error:\n" + o.getCommand());
                a.show();
            }
        });
    }
}
