package map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {
    private List<Connection> neighbors;

    private int xPos;
    private int yPos;

    public Node() {
        neighbors = new ArrayList<>();
    }

    public Node(List<Connection> neighbors) {
        this.neighbors = neighbors;
    }

    public void addNeighbor(Connection neighbor) {
        neighbors.add(neighbor);
    }

    public List<Connection> getNeighbors() {
        return Collections.unmodifiableList(neighbors);
    }
}
