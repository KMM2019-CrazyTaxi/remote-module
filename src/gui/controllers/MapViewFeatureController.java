package gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.QuadCurve;
import map.Connection;
import map.Map;
import map.Node;
import map.Position;
import remote.Car;
import remote.listeners.DataListener;

/**
 * Map View-feature controller. This is the main controller class for the Map View-feature.
 *
 * @author Henrik Nilsson
 */
public class MapViewFeatureController implements DataListener<Map> {
    private static final double NODE_DOT_SIZE = 10;

    @FXML private Button confirmMarkButton;

    @FXML private Pane mapViewFeature;
    @FXML private Group mapViewNodeLayer;
    @FXML private Group mapViewPathLayer;

    /**
     * Map View Feature Controller constructor.
     */
    public MapViewFeatureController() {
        Car.getInstance().map.subscribe(this);
    }

    /**
     * Update the shown map.
     * @param data New Map data.
     */
    public void update(Map data) {
        redraw(data);
    }

    /**
     * Draw the given Map to the view.
     * @param map Map data
     */
    private void redraw(Map map) {
        mapViewNodeLayer.getChildren().clear();
        mapViewPathLayer.getChildren().clear();

        double width = mapViewFeature.getWidth();
        double height = mapViewFeature.getHeight();

        Position mapCenterMass = calculateCenterMass(map);
        double scaleFactor = calculateScaleFactor(map, width, height, mapCenterMass);

        for (Node n : map.getNodes()) {
            // Add node dot
            Position startPos = repositionPoint(n.getPosition(), width, height, mapCenterMass, scaleFactor);

            Circle fxNodeDot = new Circle(startPos.x, startPos.y, NODE_DOT_SIZE);
            fxNodeDot.getStyleClass().add("mapNodeDot");
            fxNodeDot.idProperty().setValue("mapNodeDot-" + n.getIndex(map));
            mapViewNodeLayer.getChildren().add(fxNodeDot);

            for (Connection c : n.getNeighbors()) {
                Position midPos = repositionPoint(c.getMidPoint(), width, height, mapCenterMass, scaleFactor);
                Position endPos = repositionPoint(c.getConnectingNode().getPosition(), width, height, mapCenterMass, scaleFactor);

                QuadCurve fxPathLine = new QuadCurve(startPos.x, startPos.y, midPos.x, midPos.y, endPos.x, endPos.y);
                fxPathLine.getStyleClass().add("mapPathLine");
                fxPathLine.idProperty().setValue("mapPathLine-" + n.getIndex(map) + "-" + c.getConnectingNode().getIndex(map));

                mapViewPathLayer.getChildren().add(fxPathLine);
            }
        }
    }

    /**
     * Reposition the given point to correspond to the viewport.
     * @param p Base position
     * @param width Width of view
     * @param height Height of view
     * @param offset Center mass offset from (0,0)
     * @param scale Scale factor
     * @return New repositioned position
     */
    private static Position repositionPoint(Position p, double width, double height, Position offset, double scale) {
        Position newPos = new Position(p);

        newPos.subtract(offset);
        newPos.multiply(scale);
        newPos.add(new Position(width / 2, height / 2));

        return newPos;
    }

    /**
     * Calculate a scale factor to fit the given Map to the view.
     * @param map Map data
     * @param width View width
     * @param height View height
     * @param mid Map center mass
     * @return Rescale factor to fit the view
     */
    private static double calculateScaleFactor(Map map, double width, double height, Position mid) {
        return Math.min(calculateScaleFactorX(map, width, mid.x), calculateScaleFactorY(map, height, mid.y));
    }

    /**
     * Calculate a scale factor to fit the width of given Map to the view.
     * @param map Map data
     * @param width View width
     * @param mid Map center mass
     * @return Rescale factor to fit the width of view
     */
    private static double calculateScaleFactorX(Map map, double width, double mid) {
        double min = mid;
        double max = mid;

        // Sum all positions
        for (Node n : map.getNodes()) {
            Position nodePos = n.getPosition();

            if (min > nodePos.x)
                min = nodePos.x;

            if (max < nodePos.x)
                max = nodePos.x;

            for (Connection c : n.getNeighbors()) {
                Position midPos = c.getMidPoint();

                if (min > midPos.x)
                    min = midPos.x;

                if (max < midPos.x)
                    max = midPos.x;
            }
        }

        return width / (2 * (max - min));
    }

    /**
     * Calculate a scale factor to fit the height of given Map to the view.
     * @param map Map data
     * @param height View height
     * @param mid Map center mass
     * @return Rescale factor to fit the width of view
     */
    private static double calculateScaleFactorY(Map map, double height, double mid) {
        double min = mid;
        double max = mid;

        // Sum all positions
        for (Node n : map.getNodes()) {
            Position nodePos = n.getPosition();

            if (min > nodePos.y)
                min = nodePos.y;

            if (max < nodePos.y)
                max = nodePos.y;

            for (Connection c : n.getNeighbors()) {
                Position midPos = c.getMidPoint();

                if (min > midPos.y)
                    min = midPos.y;

                if (max < midPos.y)
                    max = midPos.y;
            }
        }

        return height / (2 * (max - min));
    }

    /**
     * Calculate the center mass of the given map. This takes into account the node and center point positions.
     * @param map Map data
     * @return Position of map center mass
     */
    private static Position calculateCenterMass(Map map) {
        Position offset = new Position();
        int numberOfPositions = 0;

        // Sum all positions
        for (Node n : map.getNodes()) {
            offset.add(n.getPosition());
            numberOfPositions++;

            for (Connection c : n.getNeighbors()) {
                offset.add(c.getMidPoint());
                numberOfPositions++;
            }
        }

        // Divide by number of positions to get center of mass
        offset.divide(numberOfPositions);

        return offset;
    }

    public void handleConfirmMarkClick(MouseEvent mouseEvent) {

    }
}
