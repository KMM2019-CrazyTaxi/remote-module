package tests;

import enums.PacketCommand;
import exceptions.MissingIDException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import remote.*;
import remote.listeners.DataListener;

import java.io.IOException;

public class ManualControlServerMockup extends Application implements DataListener<Float> {
    private Text temp;
    private FixedTimePoller poller;

    public static void main(String[] args) {launch();}

    public void start(Stage primaryStage) throws Exception {
        Server server = new Server();
        Car car = server.getCar();

        server.getRequestBuilder().addSendDatetimeRequest();
        server.pull();

        car.temperature.subscribe(this);

        poller = new FixedTimePoller(server, 10000);
        poller.add(car.temperature);

        (new Thread(poller)).start();

        StackPane root = new StackPane();
        Scene scene = new Scene(root, 300, 250);
        temp = new Text("null");
        root.getChildren().add(temp);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                try {
                    switch (event.getText()) {
                        case "w":
                            server.getRequestBuilder().addSetMaxSpeedRequest(9);
                            break;
                        case "a":
                            server.getRequestBuilder().addTurnRequest(-10);
                            break;
                        case "s":
                            server.getRequestBuilder().addSetMaxSpeedRequest(-11);
                            break;
                        case "d":
                            server.getRequestBuilder().addTurnRequest(12);
                            break;
                    }

                    try {
                        server.pull();
                    } catch (IOException | MissingIDException e) {
                        e.printStackTrace();
                    }
                } catch (InterruptedException | MissingIDException e) {
                    e.printStackTrace();
                }
            }
        });

        scene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                try {
                    switch (event.getText()) {
                        case "w":
                        case "s":
                            server.getRequestBuilder().addSetMaxSpeedRequest(0);
                            break;
                        case "a":
                        case "d":
                            server.getRequestBuilder().addTurnRequest(0);
                            break;
                    }

                    try {
                        server.pull();
                    } catch (IOException | MissingIDException e) {
                        e.printStackTrace();
                    }
                } catch (InterruptedException | MissingIDException e) {
                    e.printStackTrace();
                }
            }
            });

        primaryStage.setTitle("Control test!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * This method is called when the application should stop, and provides a
     * convenient place to prepare for application exit and destroy resources.
     *
     * <p>
     * The implementation of this method provided by the Application class does nothing.
     * </p>
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     */
    @Override
    public void stop() throws Exception {
        poller.stop();
        super.stop();
    }

    @Override
    public void update(Float data) {
        temp.setText(String.valueOf(data) + " C");
    }
}
