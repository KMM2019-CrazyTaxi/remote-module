package map;

public class Connection {
    private Node connectingNode;
    private Direction direction;
    private int distance;

    public Connection(Node connectingNode, Direction direction, int distance) {
        this.connectingNode = connectingNode;
        this.direction = direction;
        this.distance = distance;
    }
}
