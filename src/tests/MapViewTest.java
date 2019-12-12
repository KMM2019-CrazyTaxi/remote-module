package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import map.*;
import remote.Server;

public class MapViewTest extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        Stage root = FXMLLoader.load(getClass().getResource("gui/fxml/UI.fxml"));
        root.getScene().addEventFilter(KeyEvent.KEY_PRESSED, root.getScene().lookup("#manualControlModule").getOnKeyPressed());
        root.getScene().addEventFilter(KeyEvent.KEY_RELEASED, root.getScene().lookup("#manualControlModule").getOnKeyReleased());
        root.show();

        // Map test
        Map map = new Map();
        Node node0 = new Node(new Position(100, 100));
        Node node1 = new Node(new Position(100, 300));
        Node node2 = new Node(new Position(300, 100));
        Node node3 = new Node(new Position(300, 300));

        node0.addNeighbor(new Connection(node1, Direction.STRAIGHT, 100, false, new Position(50, 200)));
        node1.addNeighbor(new Connection(node3, Direction.STRAIGHT, 100, false, new Position(200, 300)));
        node3.addNeighbor(new Connection(node2, Direction.STRAIGHT, 100, false, new Position(350, 200)));
        node2.addNeighbor(new Connection(node0, Direction.STRAIGHT, 100, false, new Position(200, 100)));

        map.addNode(node0);
        map.addNode(node1);
        map.addNode(node2);
        map.addNode(node3);

        Server.getInstance().addExceptionListener(Throwable::printStackTrace);
        Server.getInstance().addResponsListener(System.out::println);

        Server.getInstance().connect();
        Server.getInstance().getRequestBuilder().addSendMapRequest(map);
        Server.getInstance().pull();
    }
}
