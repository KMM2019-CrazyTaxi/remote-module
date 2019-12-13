import enums.PacketType;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import remote.Server;

public class CCPanel extends Application {
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
