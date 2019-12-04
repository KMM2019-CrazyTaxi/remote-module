package map;

import helpers.DataConversionHelper;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {
    private List<Connection> neighbors;

    private int index;
    private Pair<Double, Double> position;

    public Node(int index, Pair<Double, Double> position) {
        this.index = index;
        this.position = position;
        neighbors = new ArrayList<>();
    }

    public Node(int index, Pair<Double, Double> position, List<Connection> neighbors) {
        this.index = index;
        this.neighbors = neighbors;
        this.position = position;
    }

    public void addNeighbor(Connection neighbor) {
        neighbors.add(neighbor);
    }

    public void addAllNeighbors(List<Connection> neighbors) {
        for (Connection c : neighbors) {
            addNeighbor(c);
        }
    }

    public List<Connection> getNeighbors() {
        return Collections.unmodifiableList(neighbors);
    }

    public int getIndex() {
        return index;
    }

    public int byteSize() {
        return 1 + 5 * neighbors.size();
    }

    public Pair<Double, Double> getPosition() {
        return position;
    }

    public byte[] toBytes() {
        byte[] bytes = new byte[this.byteSize()];

        bytes[0] = DataConversionHelper.intToByteArray(index, 1)[0];

        int offset = 1;
        for (Connection c : neighbors) {
            int connectionSize = c.byteSize();
            System.arraycopy(c.toBytes(), 0, bytes, offset, connectionSize);
            offset += connectionSize;
        }

        return bytes;
    }
}
