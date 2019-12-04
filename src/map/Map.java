package map;

import helpers.DataConversionHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Map {
    private List<Node> nodes;

    public Map() {
        nodes = new ArrayList<>();
    }

    public Map(List<Node> nodes) {
        this.nodes = nodes;
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public List<Node> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    public int getIndex(Node n) {
        int index = nodes.indexOf(n);
        if (index == -1)
            throw new IllegalArgumentException("Given Node not found in map.");
        return index;
    }

    public int byteSize() {
        int sum = 2;
        for (Node n : nodes) {
            sum += n.byteSize();
        }
        return sum;
    }

    public byte[] toBytes() {
        byte[] bytes = new byte[this.byteSize()];
        byte[] sizeBytes = DataConversionHelper.intToByteArray(nodes.size(), 2);;

        bytes[0] = sizeBytes[0];
        bytes[1] = sizeBytes[1];

        int offset = 2;
        for (Node n : nodes) {
            int nodeSize = n.byteSize();
            System.arraycopy(n.toBytes(this), 0, bytes, offset, nodeSize);
            offset += nodeSize;
        }

        return bytes;
    }
}
