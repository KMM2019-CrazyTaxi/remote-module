package map;

import helpers.DataConversionHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {
    private List<Connection> neighbors;

    private int index;

    private int xPos;
    private int yPos;

    public Node(int index) {
        this.index = index;
        neighbors = new ArrayList<>();
    }

    public Node(int index, List<Connection> neighbors) {
        this.index = index;
        this.neighbors = neighbors;
    }

    public void addNeighbor(Connection neighbor) {
        neighbors.add(neighbor);
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
