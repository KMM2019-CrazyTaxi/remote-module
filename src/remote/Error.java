package remote;

import enums.ErrorType;

public class Error {
    private ErrorType type;
    private String error;

    public Error(ErrorType type, String error) {
        this.type = type;
        this.error = error;
    }

    public Error(CommunicationPacket pack) {
        this.type = ErrorType.fromPacketType(pack.getCommand());
        this.error = new String(pack.getData());
    }

    public ErrorType getType() {
        return type;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return type + ": \"" + error + "\"";
    }
}
