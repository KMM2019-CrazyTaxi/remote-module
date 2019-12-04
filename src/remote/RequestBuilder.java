package remote;

import enums.ControlMode;
import enums.PIDControllerType;
import enums.PacketCommand;
import helpers.DataConversionHelper;
import map.Map;
import map.Node;
import remote.datatypes.CommunicationPacket;
import remote.datatypes.PIDParams;
import remote.datatypes.PacketList;
import remote.listeners.ResponsListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public int addImageRecognitionRequest() {
        return addDatalessRequest(PacketCommand.REQUEST_IR_DATA);
    }

    public int addSendDatetimeRequest() {
        String date = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
        byte[] dataStr = new byte[date.length() + 1];
        System.arraycopy(date.getBytes(), 0, dataStr, 0, date.length());

        return addRequest(PacketCommand.SEND_CURRENT_DATETIME, dataStr);
    }

    public int addSendControlParametersRequest(PIDControllerType controller, PIDParams params) {
        byte[] paramBytes = params.toBytes();
        byte[] data = new byte[paramBytes.length + 1];

        data[0] = controller.code();
        System.arraycopy(paramBytes, 0, data, 1, paramBytes.length);

        return addRequest(PacketCommand.SEND_PARAMETERS, data);
    }

    public int addSendMapRequest(Map map) {
        int id = addRequest(PacketCommand.SEND_MAP, map.toBytes());

        ResponsListener mapAckListener = new ResponsListener() {
            public void call(CommunicationPacket type) {
                if (id == type.getId()) {
                    Car.getInstance().map.update(map);
                    Server.getInstance().removeResponsListener(this);
                }
            }
        };

        Server.getInstance().addResponsListener(mapAckListener);

        return id;
    }

    public int addSendRouteRequest(List<Node> route) {
        byte[] bytes = new byte[2 + 2 * route.size()];

        byte[] sizeBytes = DataConversionHelper.intToByteArray(route.size(), 2);
        bytes[0] = sizeBytes[0];
        bytes[1] = sizeBytes[1];

        Map map = Car.getInstance().map.get();
        int offset = 2;

        for (Node n : route) {
            byte[] indexBytes = DataConversionHelper.intToByteArray(n.getIndex(map), 2);
            bytes[offset] = indexBytes[0];
            bytes[offset + 1] = indexBytes[1];

            offset += 2;
        }

        return addRequest(PacketCommand.SEND_NEW_ROUTE, bytes);
    }

    public PacketList getPackets() {
        return packets;
    }

}
