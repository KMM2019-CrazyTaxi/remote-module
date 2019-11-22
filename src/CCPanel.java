import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class CCPanel extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        Stage root = FXMLLoader.load(getClass().getResource("gui/fxml/UI.fxml"));
        root.getScene().addEventFilter(KeyEvent.KEY_PRESSED, root.getScene().lookup("#manualControlModule").getOnKeyPressed());
        root.getScene().addEventFilter(KeyEvent.KEY_RELEASED, root.getScene().lookup("#manualControlModule").getOnKeyReleased());
        root.show();
    }
}
