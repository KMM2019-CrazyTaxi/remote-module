package enums;

public enum PacketCommand {

    REQUEST_SENSOR_DATA,
    REQUEST_MODE,
    REQUEST_CONTROL_PARAMETERS,
    REQUEST_ROUTE_STATUS,
    REQUEST_MAP_LOCATION,
    REQUEST_LATERAL_DISTANCE,
    REQUEST_CONTROL_DECISION,
    REQUEST_STOP_LINE_FOUND,
    REQUEST_PASSED_DISTANCE,
    REQUEST_TEMPERATURE,
    SEND_CURRENT_DATETIME,
    SEND_MAP,
    SEND_PARAMETERS,
    SET_MODE,
    SEND_MAX_SPEED,
    REQUEST_TURN,
    SEND_NEW_ROUTE,
    REQUEST_START_ROUTE,
    REQUEST_EMERGENCY_STOP,
    REQUEST_CAMERA_IMAGE,
    REQUEST_HEARTBEAT,

    CURRENT_SENSOR_DATA,
    CURRENT_MODE,
    CURRENT_CONTROL_PARAMETERS,
    CURRENT_ROUTE_STATUS,
    CURRENT_MAP_LOCATION,
    CURRENT_LATERAL_DISTANCE,
    CURRENT_CONTROL_DECISION,
    CURRENT_STOP_LINE_FOUND,
    CURRENT_PASSED_DISTANCE,
    CURRENT_TEMPERATURE,
    CAMERA_IMAGE,
    DATETIME_ACKNOWLEDGEMENT,
    MAP_ACKNOWLEDGEMENT,
    PARAMETERS_ACKNOWLEDGEMENT,
    MODE_ACKNOWLEDGEMENT,
    MAX_SPEED_ACKNOWLEDGEMENT,
    TURN_ACKNOWLEDGEMENT,
    NEW_ROUTE_ACKNOWLEDGEMENT,
    START_ROUTE_ACKNOWLEDGEMENT,
    EMERGENCY_STOP_ACKNOWLEDGEMENT,
    HEARTBEAT_ACKNOWLEDGEMENT,

    SERSOR_MODULE_ERROR,
    CONTROL_MODULE_ERROR,
    REMOTE_MODULE_COMMUNICATION_ERROR,
    CENTRAL_MODULE_ERROR;

    public static final int NUMBER_OF_REMOTE_REQUESTS = 20;
    public static final int NUMBER_OF_CENTRAL_RESPONSES = 20;
    public static final int NUMBER_OF_ERRORS = 4;

    public static final int REMOTE_REQUEST_BASE = 0x00;
    public static final int CENTRAL_RESPONS_BASE = 0xA0;
    public static final int ERROR_BASE = 0xD0;

    public byte code() {
        int ord = this.ordinal();

        // Remote to Central
        if (ord < NUMBER_OF_REMOTE_REQUESTS) {
            return (byte)(REMOTE_REQUEST_BASE + ord);
        }
        // Central to Remote
        else if (ord < NUMBER_OF_REMOTE_REQUESTS + NUMBER_OF_CENTRAL_RESPONSES) {
            return (byte)(CENTRAL_RESPONS_BASE + ord - NUMBER_OF_REMOTE_REQUESTS);
        }
        // Errors (Remote to Central)
        else {
            return (byte)(ERROR_BASE + ord - (NUMBER_OF_REMOTE_REQUESTS + NUMBER_OF_CENTRAL_RESPONSES));
        }
    }

    public PacketType getType() {
        switch (this) {
            case SEND_CURRENT_DATETIME:
            case REQUEST_CONTROL_DECISION:
            case REQUEST_STOP_LINE_FOUND:
            case REQUEST_PASSED_DISTANCE:
            case REQUEST_TEMPERATURE:
            case REQUEST_SENSOR_DATA:
            case REQUEST_MODE:
            case REQUEST_CONTROL_PARAMETERS:
            case REQUEST_ROUTE_STATUS:
            case REQUEST_MAP_LOCATION:
            case REQUEST_LATERAL_DISTANCE:
            case SEND_MAP:
            case SEND_PARAMETERS:
            case SET_MODE:
            case SEND_MAX_SPEED:
            case REQUEST_TURN:
            case SEND_NEW_ROUTE:
            case REQUEST_START_ROUTE:
            case REQUEST_EMERGENCY_STOP:
            case REQUEST_CAMERA_IMAGE:
            case REQUEST_HEARTBEAT:
                return PacketType.REQUEST;

            case CURRENT_CONTROL_DECISION:
            case CURRENT_STOP_LINE_FOUND:
            case CURRENT_PASSED_DISTANCE:
            case CURRENT_TEMPERATURE:
            case CURRENT_SENSOR_DATA:
            case CURRENT_MODE:
            case CURRENT_CONTROL_PARAMETERS:
            case CURRENT_ROUTE_STATUS:
            case CURRENT_MAP_LOCATION:
            case CURRENT_LATERAL_DISTANCE:
            case CAMERA_IMAGE:
                return PacketType.DATA;

            case DATETIME_ACKNOWLEDGEMENT:
            case MAP_ACKNOWLEDGEMENT:
            case PARAMETERS_ACKNOWLEDGEMENT:
            case MODE_ACKNOWLEDGEMENT:
            case MAX_SPEED_ACKNOWLEDGEMENT:
            case TURN_ACKNOWLEDGEMENT:
            case NEW_ROUTE_ACKNOWLEDGEMENT:
            case START_ROUTE_ACKNOWLEDGEMENT:
            case EMERGENCY_STOP_ACKNOWLEDGEMENT:
            case HEARTBEAT_ACKNOWLEDGEMENT:
                return PacketType.ACKNOWLEDGEMENT;

            case SERSOR_MODULE_ERROR:
            case CONTROL_MODULE_ERROR:
            case REMOTE_MODULE_COMMUNICATION_ERROR:
            case CENTRAL_MODULE_ERROR:
                return PacketType.ERROR;

            default:
                throw new IllegalStateException("You forgot to add a new type of command in the 'getType' function... (" + this + ")");
        }
    }

    public static PacketCommand fromByte(byte b) {
        int ord;

        if ((b & 0xff) < CENTRAL_RESPONS_BASE) {
            ord = (b & 0xff);
        }
        else if ((b & 0xff) < ERROR_BASE) {
            ord = (b & 0xff) - CENTRAL_RESPONS_BASE + NUMBER_OF_REMOTE_REQUESTS + 1;
        }
        else {
            ord = (b & 0xff) - ERROR_BASE + NUMBER_OF_REMOTE_REQUESTS + NUMBER_OF_CENTRAL_RESPONSES + 1;
        }

        if (ord > ControlMode.values().length)
            throw new IllegalArgumentException("Given byte is out of range. (Ordinal: " + ord + ")");

        return PacketCommand.values()[ord];
    }
}
