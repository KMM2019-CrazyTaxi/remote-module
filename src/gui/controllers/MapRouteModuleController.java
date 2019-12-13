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

/**
 * Map Route-module controller. This is the main controller class for the Map Route-module.
 *
 * @author Henrik Nilsson
 */
public class MapRouteModuleController implements DataListener<Map> {
    private static final String UNACTIVE_BUTTON_TEXT = "New route";
    private static final String ACTIVE_BUTTON_TEXT = "Send route";

    @FXML private VBox mapRouteModule;
    @FXML private Button newRouteButton;

    private AtomicBoolean buildingRoute;
    private List<Node> currentRoute;

    /**
     * Map Route Module Controller constructor.
     */
    public MapRouteModuleController() {
        buildingRoute = new AtomicBoolean(false);
        currentRoute = new ArrayList<>();
    }

    /**
     * Initialize method called by JavaFX thread when JavaFX-objects are constructed and ready.
     * This method sets up listeners and sets default text of newRouteButton.
     */
    public void initialize() {
        newRouteButton.setText(UNACTIVE_BUTTON_TEXT);

        Car.getInstance().map.subscribe(this);
    }

    /**
     * Handle New Route-button click. This toggles the route building state and sends the route.
     * @param mouseEvent Triggering event
     */
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

    /**
     * Deactivate newRoute-button.
     */
    private void deactivateButton() {
        newRouteButton.setText(UNACTIVE_BUTTON_TEXT);
        newRouteButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), false);
    }

    /**
     * Activate newRoute-button.
     */
    private void activateButton() {
        newRouteButton.setText(ACTIVE_BUTTON_TEXT);
        newRouteButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), true);
    }

    /**
     * Send new route to car.
     */
    private void sendNewRoute() {
        Server.getInstance().getRequestBuilder().addSendRouteRequest(currentRoute);
        Server.getInstance().releaseBuilder();
        Server.getInstance().pull();
    }

    /**
     * Handle JavaFX Map Node-button click. This gets the corresponding Map Node and adds it to the current route if
     * currently buildning route and if the clicked node is valid (not the same as last node).
     * @param mouseEvent Triggering event.
     */
    private void handleNodeClick(MouseEvent mouseEvent) {
        if (buildingRoute.get()) {
            mouseEvent.consume();

            Circle fxNode = (Circle) mouseEvent.getSource();
            int id = Integer.parseInt(fxNode.getId().split(":")[1]);
            Node node = Car.getInstance().map.get().getNode(id);

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

    /**
     * Apply eventlisteners to all new nodes.
     * @param data New Map data.
     */
    @Override
    public void update(Map data) {
        for (javafx.scene.Node fxNode : ((Group) mapRouteModule.lookup("#mapViewTopLayer")).getChildren()) {
            fxNode.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleNodeClick);
        }
    }
}
