package gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;

import remote.Car;
import remote.Server;

/**
 * Menu Bar Controller class. This is the main controller of the Menu Bar.
 */
public class MenuBarController {

    @FXML private Menu serverMenu;

    /**
     * Initialize method called by JavaFX thread when JavaFX-objects are constructed and ready.
     * This method sets up a listener for alive status.
     */
    public void initialize() {
        Car.getInstance().aliveStatus.subscribe(o -> {
            if (o.booleanValue()) {
                serverMenu.getStyleClass().add("alive");
            }
            else {
                serverMenu.getStyleClass().remove("alive");
            }
        });
    }

    /**
     * Handle click in the Server Connect-button. Calls server connect.
     * @param actionEvent Triggering event
     */
    public void handleConnectClick(ActionEvent actionEvent) {
        Server.getInstance().connect();
    }
}
