package remote;

import enums.ControlMode;
import enums.PacketCommand;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Server request handler class.
 *
 * @author Henrik Nilsson
 */
public class RequestBuilder {
    private PacketList packets;
    private RequestIDBroker requestIDBroker;

    public RequestBuilder(RequestIDBroker requestIDBroker) {
        packets = new PacketList();
        this.requestIDBroker = requestIDBroker;
    }

    public void addRequest(PacketCommand type, byte[] data) {
        // Packet of the same data less type already exists
        if (data.length == 0 && packets.contains(type))
            return;

        CommunicationPacket pack = new CommunicationPacket(type, requestIDBroker.getID(), data);
        packets.addPacket(pack);
    }

    public void addDatalessRequest(PacketCommand type) {
        addRequest(type, new byte[0]);
    }

    public void addStatusRequest() {
        //addDatalessRequest(PacketCommand.REQUEST_STATUS);
    }

//    public void addSendMapRequest(Map map) {
//        addRequest(Enums.PacketType.SEND_MAP, map.toBytes());
//    }

//    public void addSendParametersRequest(ControlParameters param) {
//        addRequest(Enums.PacketType.SEND_PARAMETERS, param.toBytes());
//    }

    public void addSetModeRequest(ControlMode mode) {
        addRequest(PacketCommand.SET_MODE, new byte[]{mode.code()});
    }

    public void addSetMaxSpeedRequest(int speed) {
        if(speed >= 0x7f || speed <= -0x7f)
            throw new IllegalArgumentException("Given speed is out of range (" + speed + ")");

        addRequest(PacketCommand.SEND_MAX_SPEED, new byte[]{(byte) speed});
    }

    public void addTurnRequest(int turn) {
        if(turn >= 0x7f || turn <= -0x7f)
            throw new IllegalArgumentException("Given turn is out of range (" + turn + ")");

        addRequest(PacketCommand.REQUEST_TURN, new byte[]{(byte) turn});
    }

//    public void addSendRoute(Route route) {
//        addRequest(Enums.PacketType.SEND_NEW_ROUTE, route.toBytes());
//    }

    public void addStartRouteRequest() {
        addDatalessRequest(PacketCommand.REQUEST_START_ROUTE);
    }

    public void addEmergencyStopRequest() {
        addDatalessRequest(PacketCommand.REQUEST_EMERGENCY_STOP);
    }

    public void addCameraImageRequest() {
        addDatalessRequest(PacketCommand.REQUEST_CAMERA_IMAGE);
    }

    public void addHeartbeatRequest() {
        addDatalessRequest(PacketCommand.REQUEST_HEARTBEAT);
    }

    public void addSendDatetimeRequest() {
        String date = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
        byte[] dataStr = new byte[date.length() + 1];
        System.arraycopy(date.getBytes(), 0, dataStr, 0, date.length());

        addRequest(PacketCommand.SEND_CURRENT_DATETIME, dataStr);
    }

    public PacketList getPackets() {
        return packets;
    }
}
