package gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.util.concurrent.atomic.AtomicBoolean;

public class ControlParameterController {

    @FXML private GridPane controlGrid;

    @FXML private TextField controlParameterKI;
    @FXML private TextField controlParameterKD;
    @FXML private TextField controlParameterAlpha;
    @FXML private TextField controlParameterSpeed;
    @FXML private TextField controlParameterBeta;
    @FXML private TextField controlParameterAngle;
    @FXML private TextField controlParameterMin;
    @FXML private TextField controlParameterSlope;
    @FXML private TextField controlParameterKP;

    private AtomicBoolean editing;

    public ControlParameterController() {
        editing = new AtomicBoolean(false);
    }


    public void handleUpdateClicked(MouseEvent mouseEvent) {
        if (editing.get()) {
            
        }
    }
}
