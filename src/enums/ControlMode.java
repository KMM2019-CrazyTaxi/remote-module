package enums;

public enum ControlMode {
    FULL_AUTO,
    MANUAL;

    public byte code() {
        return (byte) this.ordinal();
    }

    public static ControlMode fromByte(byte b) {
        if ((b & 0xff) > ControlMode.values().length)
            throw new IllegalArgumentException("Given byte is out of range. (" + b + ")");
        return ControlMode.values()[b];
    }
}
