package gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;

import remote.Server;

import java.io.IOException;

/**
 * Menu Bar Controller class. This is the main controller of the Menu Bar.
 */
public class MenuBarController {

    @FXML private Menu serverMenu;

    /**
     * Handle click in the Server Connect-button. Tries to connect the server.
     * @param actionEvent Triggering event
     */
    public void handleConnectClick(ActionEvent actionEvent) {
        try {
            Server.getInstance().connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO Change alive status to a RemoteData of Car then implemented
        serverMenu.getStyleClass().add("alive");
    }
}
