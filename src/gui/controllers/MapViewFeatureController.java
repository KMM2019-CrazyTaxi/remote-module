package gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.QuadCurve;
import map.Connection;
import map.Map;
import map.Node;
import map.Position;
import remote.Car;
import remote.listeners.DataListener;

public class MapViewFeatureController implements DataListener<Map> {
    private static final double NODE_DOT_SIZE = 10;

    @FXML private Pane mapViewFeature;
    @FXML private Group mapViewTopLayer;
    @FXML private Group mapViewMiddleLayer;
    @FXML private Group mapViewBottomLayer;

    public MapViewFeatureController() {
        Car.getInstance().map.subscribe(this);
    }

    public void update(Map data) {
        redraw(data);
    }

    private void redraw(Map map) {
        double width = mapViewFeature.getWidth();
        double height = mapViewFeature.getHeight();

        Position mapCenterMass = calculateCenterMass(map);
        double scaleFactor = calculateScaleFactor(map, width, height, mapCenterMass);

        System.out.println(mapCenterMass);
        System.out.println(scaleFactor);

        for (Node n : map.getNodes()) {
            // Add node dot
            Position startPos = repositionPoint(n.getPosition(), width, height, mapCenterMass, scaleFactor);

            Circle fxNodeDot = new Circle(startPos.x, startPos.y, NODE_DOT_SIZE);
            fxNodeDot.getStyleClass().add("mapNodeDot");
            fxNodeDot.idProperty().setValue("mapNodeDot" + n.getIndex(map));
            mapViewTopLayer.getChildren().add(fxNodeDot);

            for (Connection c : n.getNeighbors()) {
                Position midPos = repositionPoint(c.getMidPoint(), width, height, mapCenterMass, scaleFactor);
                Position endPos = repositionPoint(c.getConnectingNode().getPosition(), width, height, mapCenterMass, scaleFactor);

                QuadCurve fxPathLine = new QuadCurve(startPos.x, startPos.y, midPos.x, midPos.y, endPos.x, endPos.y);
                fxPathLine.getStyleClass().add("mapPathLine");
                fxPathLine.idProperty().setValue("mapPathLine" + n.getIndex(map) + ":" + c.getConnectingNode().getIndex(map));
                mapViewBottomLayer.getChildren().add(fxPathLine);
            }
        }
    }

    private static Position repositionPoint(Position p, double width, double height, Position offset, double scale) {
        Position newPos = new Position(p);

        newPos.subtract(offset);
        newPos.multiply(scale);
        newPos.add(new Position(width / 2, height / 2));

        return newPos;
    }

    private static double calculateScaleFactor(Map map, double width, double height, Position mid) {
        return Math.min(calculateScaleFactorX(map, width, mid.x), calculateScaleFactorY(map, height, mid.y));
    }

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
}
