package gui.controllers;

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import remote.Server;

import java.io.IOException;

public class MenuBarController {

    @FXML private Menu serverMenu;

    public void handleConnectClick(ActionEvent actionEvent) {
        try {
            Server.getInstance().connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        serverMenu.getStyleClass().add("alive");
    }
}
