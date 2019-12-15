package gui.controllers;

import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.QuadCurve;
import map.Connection;
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

    private Button confirmMarkButton;

    private AtomicBoolean buildingRoute;
    private List<Node> currentRoute;

    private Node markedNode;
    private Circle markedFxNode;
    private QuadCurve markedFxConnection;

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
        confirmMarkButton = (Button) mapRouteModule.lookup("#confirmMarkButton");
        confirmMarkButton.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleConfirmMarkClick);

        newRouteButton.setText(UNACTIVE_BUTTON_TEXT);

        Car.getInstance().map.subscribe(this);
    }

    private void handleConfirmMarkClick(MouseEvent mouseEvent) {
        if (markedNode != null) {
            currentRoute.add(markedNode);
            unmark();
        }
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

        confirmMarkButton.setVisible(false);
    }

    /**
     * Activate newRoute-button.
     */
    private void activateButton() {
        newRouteButton.setText(ACTIVE_BUTTON_TEXT);
        newRouteButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), true);

        confirmMarkButton.setVisible(true);
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
     * Handle JavaFX Map Node-button click. This gets the corresponding Map Node and marks it along with the connecting
     * path.
     * @param mouseEvent Triggering event.
     */
    private void handleNodeClick(MouseEvent mouseEvent) {
        if (buildingRoute.get()) {
            mouseEvent.consume();

            Map map = Car.getInstance().map.get();

            // Collect data from new mark
            QuadCurve newFxPath = (QuadCurve) mouseEvent.getSource();

            String[] idParts = newFxPath.getId().split("-");
            int id1 = Integer.parseInt(idParts[1]);
            int id2 = Integer.parseInt(idParts[2]);

            Node newNode1 = Car.getInstance().map.get().getNode(id1);
            Node newNode2 = Car.getInstance().map.get().getNode(id2);

            Circle newFxNode = (Circle) mapRouteModule.lookup("#mapNodeDot-" + newNode2.getIndex(map));

            unmark();

            // Disable clicked path
            newFxPath.setDisable(true);

            // Mark clicked path
            newFxPath.getStyleClass().add("marked");
            newFxPath.toFront();
            markedFxConnection = newFxPath;

            // Mark destination node
            newFxNode.getStyleClass().add("marked");
            newFxNode.toFront();
            markedNode = newNode2;
            markedFxNode = newFxNode;
        }
    }

    private void unmark() {
        if (markedFxNode != null) {
            // Unmark last marked node
            markedFxNode.getStyleClass().remove("marked");
        }

        if (markedFxConnection != null) {
            // unmark last marked path
            markedFxConnection.getStyleClass().remove("marked");
            markedFxConnection.setDisable(false);
        }

        markedNode = null;
    }

    /**
     * Apply eventlisteners to all new nodes.
     * @param data New Map data.
     */
    @Override
    public void update(Map data) {
        for (javafx.scene.Node fxNode : ((Group) mapRouteModule.lookup("#mapViewPathLayer")).getChildren()) {
            fxNode.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleNodeClick);
        }
    }
}
