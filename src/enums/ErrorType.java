package enums;

public enum ErrorType {
    SENSOR,
    CONTROL,
    CENTRAL,
    REMOTE;

    public static ErrorType fromPacketType(PacketCommand type) {
        switch (type) {
            case SERSOR_MODULE_ERROR:
                return SENSOR;
            case CONTROL_MODULE_ERROR:
                return CONTROL;
            case REMOTE_MODULE_COMMUNICATION_ERROR:
                return REMOTE;
            case CENTRAL_MODULE_ERROR:
                return CENTRAL;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case SENSOR:
                return "Sensor module error.";
            case CONTROL:
                return "Control module error.";
            case CENTRAL:
                return "Central module error.";
            case REMOTE:
                return "Remote module error.";
        }
        return "You forgot to add a new type of error in the 'toString' function...";
    }
}
