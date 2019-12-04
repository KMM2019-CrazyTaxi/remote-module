package map;

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

    public int byteSize() {
        int sum = 0;
        for (Node n : nodes) {
            sum += n.byteSize();
        }
        return sum;
    }

    public byte[] toBytes() {
        byte[] bytes = new byte[this.byteSize()];

        int offset = 0;
        for (Node n : nodes) {
            int nodeSize = n.byteSize();
            System.arraycopy(n.toBytes(), 0, bytes, offset, nodeSize);
            offset += nodeSize;
        }

        return bytes;
    }
}
