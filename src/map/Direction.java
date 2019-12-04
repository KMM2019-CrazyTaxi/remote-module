package map;

public enum Direction {
    STRAIGHT,
    LEFT,
    RIGHT;

    public byte code() {
        return (byte) this.ordinal();
    }

    public static Direction fromByte(byte b) {
        if ((b & 0xff) >= Direction.values().length)
            throw new IllegalArgumentException("Given byte is out of range. (" + b + ")");
        return Direction.values()[b];
    }
}
