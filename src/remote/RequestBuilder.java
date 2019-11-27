package remote;

import enums.ControlMode;
import enums.PIDControlerType;
import enums.PacketCommand;
import remote.datatypes.CommunicationPacket;
import remote.datatypes.PIDParams;
import remote.datatypes.PacketList;

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

    public int addRequest(PacketCommand type, byte[] data) {
        // Packet of the same data less type already exists
        if (data.length == 0 && packets.contains(type))
            return packets.get(type).getId();

        int id = requestIDBroker.getID();
        CommunicationPacket pack = new CommunicationPacket(type, id, data);
        packets.addPacket(pack);
        return id;
    }

    public int addRequest(CommunicationPacket referencePack) {
        int id = requestIDBroker.getID();
        CommunicationPacket pack = new CommunicationPacket(referencePack, id);
        packets.addPacket(pack);
        return id;
    }

    public int addDatalessRequest(PacketCommand type) {
        return addRequest(type, new byte[0]);
    }

    public int addSetModeRequest(ControlMode mode) {
        return addRequest(PacketCommand.SET_MODE, new byte[]{mode.code()});
    }

    // TODO Change speed to an SI float parameter and add conversion
    public int addSetMaxSpeedRequest(int speed) {
        if(speed >= 0x7f || speed <= -0x7f)
            throw new IllegalArgumentException("Given speed is out of range (" + speed + ")");

        return addRequest(PacketCommand.SEND_MAX_SPEED, new byte[]{(byte) speed});
    }

    public int addTurnRequest(int turn) {
        if(turn >= 0x7f || turn <= -0x7f)
            throw new IllegalArgumentException("Given turn is out of range (" + turn + ")");

        return addRequest(PacketCommand.REQUEST_TURN, new byte[]{(byte) turn});
    }

    public int addStartRouteRequest() {
        return addDatalessRequest(PacketCommand.REQUEST_START_ROUTE);
    }

    public int addEmergencyStopRequest() {
        return addDatalessRequest(PacketCommand.REQUEST_EMERGENCY_STOP);
    }

    public int addCameraImageRequest() {
        return addDatalessRequest(PacketCommand.REQUEST_CAMERA_IMAGE);
    }

    public int addHeartbeatRequest() {
        return addDatalessRequest(PacketCommand.REQUEST_HEARTBEAT);
    }

    public int addSendDatetimeRequest() {
        String date = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
        byte[] dataStr = new byte[date.length() + 1];
        System.arraycopy(date.getBytes(), 0, dataStr, 0, date.length());

        return addRequest(PacketCommand.SEND_CURRENT_DATETIME, dataStr);
    }

    public int addSendControlParametersRequest(PIDControlerType controller, PIDParams params) {
        byte[] paramBytes = params.toBytes();
        byte[] data = new byte[paramBytes.length + 1];

        data[0] = controller.code();
        System.arraycopy(paramBytes, 0, data, 1, paramBytes.length);

        return addRequest(PacketCommand.SEND_PARAMETERS, data);
    }

    public PacketList getPackets() {
        return packets;
    }
}
