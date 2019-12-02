package enums;

public enum PIDControlerType {
    TURNING,
    PARKING,
    STOPPING,
    LINE_ANGLE,
    LINE_SPEED;

    public byte code() {
        return (byte) (this.ordinal() + 1);
    }

    public static PIDControlerType fromByte(byte b) {
        if ((b & 0xff) > PIDControlerType.values().length)
            throw new IllegalArgumentException("Given byte is out of range. (" + b + ")");
        return PIDControlerType.values()[b];
    }
}
