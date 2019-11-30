package gui.controllers;

import helpers.MathHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import remote.Car;
import remote.Server;
import remote.listeners.DataListener;

import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Image Recognition Data Grid-feature controller. This is the main controller class for the Image Recognition Data
 * Grid-feature.
 * @author Henrik Nilsson
 */
public class IRDataGridFeatureController {

    /**
     * Default number of shown decimal places.
     */
    public static final int DEFAULT_DECIMAL_PLACES = 2;

    private int decimalPlaces;

    @FXML private GridPane irDataGridFeature;

    @FXML private ContextMenu contextMenu;
    @FXML private TextInputDialog decimalPlacesDialog;

    @FXML private Text irDataLeft;
    @FXML private Text irDataRight;
    @FXML private Text irDataStop;

    public IRDataGridFeatureController() {
        decimalPlaces = DEFAULT_DECIMAL_PLACES;
    }

    /**
     * Initialize method called by JavaFX thread when JavaFX-objects are constructed and ready.
     * This method sets up listeners, styles the input dialog and adds formatting to the input dialog.
     */
    public void initialize() {
        // Remove header from input dialog
        decimalPlacesDialog.setHeaderText(null);
        decimalPlacesDialog.setGraphic(null);

        // Add input formatter to input dialog
        Pattern validEditingState = Pattern.compile("^\\d+$|");

        UnaryOperator<TextFormatter.Change> filter = c -> {
            String text = c.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return c;
            } else {
                return null;
            }
        };

        StringConverter<Integer> converter = new StringConverter<Integer>() {
            @Override
            public Integer fromString(String s) {
                if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                    return DEFAULT_DECIMAL_PLACES;
                } else {
                    return Integer.valueOf(s);
                }
            }

            @Override
            public String toString(Integer d) {
                return d.toString();
            }
        };

        decimalPlacesDialog.getEditor().setTextFormatter(new TextFormatter<Integer>(converter, DEFAULT_DECIMAL_PLACES, filter));

        // Add listeners
        DataListener<Double> rightListener = (Double data) -> {
            irDataRight.setText(String.valueOf(MathHelper.round(data, decimalPlaces)));
        };
        DataListener<Double> leftListener = (Double data) -> {
            irDataLeft.setText(String.valueOf(MathHelper.round(data, decimalPlaces)));
        };
        DataListener<Double> stopListener = (Double data) -> {
            irDataStop.setText(String.valueOf(MathHelper.round(data, decimalPlaces)));
        };

        Car.getInstance().distanceToRight.subscribe(rightListener);
        Car.getInstance().distanceToLeft.subscribe(leftListener);
        Car.getInstance().distanceToStop.subscribe(stopListener);
    }


    /**
     * Handle set decimal accuracy contect menu click. This updates opens a dialog to update the number of shown decimal
     * places of values.
     * @param actionEvent Triggering event
     */
    public void handleSetDecimalsEvent(ActionEvent actionEvent) {
        Optional<String> res = decimalPlacesDialog.showAndWait();

        if (res.isPresent()) {
            decimalPlaces = (Integer) decimalPlacesDialog.getEditor().getTextFormatter().getValue();

            Car.getInstance().distanceToRight.poll();
            Car.getInstance().distanceToLeft.poll();
            Car.getInstance().distanceToStop.poll();

            Server.getInstance().pull();
        }
    }

    /**
     * Show the context menu.
     * @param contextMenuEvent Triggering event
     */
    public void showContextMenu(ContextMenuEvent contextMenuEvent) {
        contextMenu.show(irDataGridFeature, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
    }
}
