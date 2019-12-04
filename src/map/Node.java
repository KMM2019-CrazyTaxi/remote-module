package map;

import helpers.DataConversionHelper;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {
    private List<Connection> neighbors;

    private Position position;

    public Node(Position position) {
        this.position = position;
        neighbors = new ArrayList<>();
    }

    public Node(Position position, List<Connection> neighbors) {
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

    public int getIndex(Map map) {
        return map.getIndex(this);
    }

    public int byteSize() {
        return 1 + 5 * neighbors.size();
    }

    public Position getPosition() {
        return position;
    }

    public byte[] toBytes(Map map) {
        byte[] bytes = new byte[this.byteSize()];

        bytes[0] = DataConversionHelper.intToByteArray(getIndex(map), 1)[0];

        int offset = 1;
        for (Connection c : neighbors) {
            int connectionSize = c.byteSize();
            System.arraycopy(c.toBytes(map), 0, bytes, offset, connectionSize);
            offset += connectionSize;
        }

        return bytes;
    }
}
