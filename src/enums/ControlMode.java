package enums;

public enum ControlMode {
    MANUAL,
    SEMI_AUTO,
    FULL_AUTO;

    public byte code() {
        return (byte) this.ordinal();
    }

    public static ControlMode fromByte(byte b) {
        return ControlMode.values()[b];
    }
}
