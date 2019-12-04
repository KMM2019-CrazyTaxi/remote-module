package map;

import helpers.DataConversionHelper;

public class Connection {
    // Byte size as per definition in connection protocol
    private final static int BYTE_SIZE = 5;

    private Node connectingNode;
    private Direction direction;
    private int distance;
    private boolean stopable;

    public Connection(Node connectingNode, Direction direction, int distance, boolean stopable) {
        this.connectingNode = connectingNode;
        this.direction = direction;
        this.distance = distance;
        this.stopable = stopable;
    }

    public int byteSize() {
        return BYTE_SIZE;
    }


    public byte[] toBytes() {
        byte[] bytes = new byte[this.byteSize()];

        bytes[0] = DataConversionHelper.intToByteArray(connectingNode.getIndex(), 1)[0];
        bytes[1] = DataConversionHelper.intToByteArray(distance, 2)[0];
        bytes[2] = DataConversionHelper.intToByteArray(distance, 2)[1];
        bytes[3] = (byte) (stopable ? 1 : 0);
        bytes[4] = direction.code();

        return bytes;
    }
}
