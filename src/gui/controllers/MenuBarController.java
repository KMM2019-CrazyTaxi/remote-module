package gui.controllers;

import exceptions.IllegalMapException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;

import javafx.stage.FileChooser;
import map.Map;
import remote.Car;
import remote.Server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Menu Bar Controller class. This is the main controller of the Menu Bar.
 */
public class MenuBarController {

    @FXML private Menu serverMenu;
    @FXML private FileChooser fileChooser;

    /**
     * Initialize method called by JavaFX thread when JavaFX-objects are constructed and ready.
     * This method sets up a listener for alive status and adds extention filers to file chooser.
     */
    public void initialize() {

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Map files", "*.map"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files", "*"));

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

    /**
     * Handle click in the Server Connect-button. Reads map file and sends it to the car.
     * @param actionEvent Triggering event
     */
    public void handleLoadClick(ActionEvent actionEvent) {
        // Choose file
        File file = fileChooser.showOpenDialog(null);

        if (file == null)
            return;

        try {
            byte[] readBytes = Files.readAllBytes(file.toPath());

            Car.getInstance().map.update(new Map(readBytes));
//            Server.getInstance().getRequestBuilder().addSendMapRequest(new Map(readBytes));
//            Server.getInstance().releaseBuilder();
//            Server.getInstance().pull();
        } catch (IOException | IllegalMapException e) {
            e.printStackTrace();
        }
    }
}
