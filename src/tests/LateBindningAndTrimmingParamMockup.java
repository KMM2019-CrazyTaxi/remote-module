package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import remote.Car;
import remote.datatypes.PIDParams;

public class LateBindningAndTrimmingParamMockup extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        Stage root = FXMLLoader.load(getClass().getResource("gui/fxml/UI.fxml"));
        root.getScene().addEventFilter(KeyEvent.KEY_PRESSED, root.getScene().lookup("#manualControlModule").getOnKeyPressed());
        root.getScene().addEventFilter(KeyEvent.KEY_RELEASED, root.getScene().lookup("#manualControlModule").getOnKeyReleased());
        root.show();

        System.out.println("Late bind.");
        Car.getInstance().lateBindSubscribers();

        new Thread(this::waitAndUpdate).start();
    }

    private void waitAndUpdate(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("New parking params");
        Car.getInstance().parkingParams.update(new PIDParams(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Car.getInstance().turningParams.update(new PIDParams(1.00001, 23.38, 1337, 420, (double)5/7, 69, 007, 808, 0xDEAD));
    }
}
