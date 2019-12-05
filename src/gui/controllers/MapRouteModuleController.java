package gui.controllers;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import map.Map;
import map.Node;
import remote.Car;
import remote.Server;
import remote.listeners.DataListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MapRouteModuleController implements DataListener<Map> {
    private static final String UNACTIVE_BUTTON_TEXT = "New route";
    private static final String ACTIVE_BUTTON_TEXT = "Send route";

    @FXML private VBox mapRouteModule;
    @FXML private Button newRouteButton;

    private AtomicBoolean buildingRoute;
    private List<Node> currentRoute;

    public MapRouteModuleController() {
        buildingRoute = new AtomicBoolean(false);
        currentRoute = new ArrayList<>();
    }

    public void initialize() {
        newRouteButton.setText(UNACTIVE_BUTTON_TEXT);

        Car.getInstance().map.subscribe(this);
    }

    public void handleNewRouteClick(MouseEvent mouseEvent) {
        if (buildingRoute.get()) {
            sendNewRoute();
            deactivateButton();
        }
        else {
            activateButton();
            currentRoute = new ArrayList<>();
        }

        buildingRoute.set(!buildingRoute.get());
    }

    private void deactivateButton() {
        newRouteButton.setText(UNACTIVE_BUTTON_TEXT);
        newRouteButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), false);
    }

    private void activateButton() {
        newRouteButton.setText(ACTIVE_BUTTON_TEXT);
        newRouteButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), false);
    }

    private void sendNewRoute() {
        Server.getInstance().getRequestBuilder().addSendRouteRequest(currentRoute);
        Server.getInstance().pull();
    }

    private void handleNodeClick(MouseEvent mouseEvent) {
        if (buildingRoute.get()) {
            mouseEvent.consume();

            Circle fxNode = (Circle) mouseEvent.getSource();
            Node node = Car.getInstance().map.get().getNode(Integer.parseInt(String.valueOf(fxNode.getId().charAt(10))));

            // Check so that new node is not the same as the last node
            Map map = Car.getInstance().map.get();
            if (currentRoute.isEmpty() || node.getIndex(map) != currentRoute.get(currentRoute.size()-1).getIndex(map)) {
                // Add to current route
                currentRoute.add(node);
            }
            else {
                // TODO Notify user that the last node cannot be the next node
            }
        }
    }

    @Override
    public void update(Map data) {
        for (javafx.scene.Node fxNode : ((Group) mapRouteModule.lookup("#mapViewTopLayer")).getChildren()) {
            fxNode.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleNodeClick);
        }
    }
}
