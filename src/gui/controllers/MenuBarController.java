package gui.controllers;

import javafx.event.ActionEvent;
import remote.Server;

import java.io.IOException;

public class MenuBarController {

    public void handleConnectClick(ActionEvent actionEvent) {
        try {
            Server.getInstance().connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
