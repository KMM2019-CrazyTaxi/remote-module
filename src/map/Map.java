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
}
