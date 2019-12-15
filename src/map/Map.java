package map;

import exceptions.IllegalMapException;
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

    public Map(byte[] bytes) throws IllegalMapException {
        this();

        int numberOfJunctions = DataConversionHelper.byteArrayToUnsignedInt(bytes, 0, 2);
        int numberOfNodes = DataConversionHelper.byteArrayToUnsignedInt(bytes, 2, 2);
        int numberOfConnections = DataConversionHelper.byteArrayToUnsignedInt(bytes, 4, 2);

        if (numberOfJunctions != 0)
            throw new IllegalMapException("Map does not seem to be of correct typ.");

        int offset = 6;

        for (int i = 0; i < numberOfNodes; i++) {
            double x = DataConversionHelper.byteArrayToDouble(bytes, offset);
            offset += 8;

            double y = DataConversionHelper.byteArrayToDouble(bytes, offset);
            offset += 8;

            Position pos = new Position(x, y);
            Node n = new Node(pos);
            addNode(n);
        }

        for (int i = 0; i < numberOfConnections; i++) {
            int fromIndex = DataConversionHelper.byteArrayToUnsignedInt(bytes, offset, 1);
            offset += 1;

            int toIndex = DataConversionHelper.byteArrayToUnsignedInt(bytes, offset, 1);
            offset += 1;

            int distance = DataConversionHelper.byteArrayToUnsignedInt(bytes, offset, 2);
            offset += 2;

            boolean stopable = DataConversionHelper.byteArrayToUnsignedInt(bytes, offset, 1) != 0;
            offset += 1;

            Direction direction = Direction.fromByte(bytes[offset]);
            offset += 1;

            double x = DataConversionHelper.byteArrayToDouble(bytes, offset);
            offset += 8;

            double y = DataConversionHelper.byteArrayToDouble(bytes, offset);
            offset += 8;

            getNode(fromIndex).addNeighbor(new Connection(getNode(toIndex), direction, distance, stopable, new Position(x, y)));
        }
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

    public Node getNode(int index) {
        if (index > nodes.size())
            throw new IllegalArgumentException("Given index is out of range (" + index + ").");
        return nodes.get(index);
    }
}
