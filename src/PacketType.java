public enum PacketType {

    REQUEST_HEARTBEAT,
    REQUEST_STATUS,
    SEND_MAP,
    SEND_PARAMETERS,
    REQUEST_MANUAL_MODE,
    REQUEST_AUTONOMOUS_MODE,
    SEND_MAX_SPEED,
    REQUEST_MANUAL_TURN,
    SEND_NEW_ROUTE,
    REQUEST_START_ROUTE,
    REQUEST_EMERGENCY_STOP,
    REQUEST_CAMERA_IMAGE,

    HEARTBEAT_ACKNOWLEDGEMENT,
    STATUS,
    MAP_ACKNOWLEDGEMENT,
    PARAMETERS_ACKNOWLEDGEMENT,
    MANUAL_MODE_ACKNOWLEDGEMENT,
    ATONOMOUS_MODE_ACKNOWLEDGEMENT,
    MAX_SPEED_ACKNOWLEDGEMENT,
    MANUAL_TURN_ACKNOWLEDGEMENT,
    NEW_ROUTE_ACKNOWLEDGEMENT,
    START_ROUTE_ACKNOWLEDGEMENT,
    EMERGENCY_STOP_ACKNOWLEDGEMENT,
    CAMERA_IMAGE,

    SERSOR_MODULE_ERROR,
    STEERING_MODULE_ERROR,
    REMOTE_MODULE_COMMUNICATION_ERROR,
    CENTRAL_MODULE_ERROR;

    private static final int NUMBER_OF_REMOTE_REQUESTS = 12;
    private static final int NUMBER_OF_CENTRAL_RESPONSES = 12;
    private static final int NUMBER_OF_ERRORS = 4;

    private static final int REMOTE_REQUEST_BASE = 0x00;
    private static final int CENTRAL_RESPONS_BASE = 0xA0;
    private static final int ERROR_BASE = 0xD0;

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
}
