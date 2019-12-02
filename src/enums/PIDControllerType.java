package enums;

public enum PIDControllerType {
    TURNING,
    PARKING,
    STOPPING,
    LINE_ANGLE,
    LINE_SPEED;

    public byte code() {
        return (byte) (this.ordinal() + 1);
    }

    public static PIDControllerType fromByte(byte b) {
        if ((b & 0xff) > PIDControllerType.values().length)
            throw new IllegalArgumentException("Given byte is out of range. (" + b + ")");
        return PIDControllerType.values()[b];
    }
}
