package remote;

import enums.ControlMode;
import enums.PacketCommand;
import map.Map;
import remote.datatypes.CommunicationPacket;
import remote.datatypes.PIDParams;
import remote.datatypes.RemoteData;
import remote.listeners.DataListener;

import java.util.ArrayList;
import java.util.List;

public class Car {
    private static Car instance = new Car();
    private List<LateBind> lateBinders;

    public RemoteData<Boolean> aliveStatus;

    // Sensor data
    // TODO Change to float
    public RemoteData<Integer> accelerationX;
    public RemoteData<Integer> accelerationY;
    public RemoteData<Integer> accelerationZ;
    public RemoteData<Integer> distance;
    public RemoteData<Integer> speed;

    public RemoteData<Float> temperature;

    // Camera processing
    public RemoteData<Double> distanceToLeft;
    public RemoteData<Double> distanceToRight;
    public RemoteData<Double> distanceToStop;

    // Control
    public RemoteData<PIDParams> turningParams;
    public RemoteData<PIDParams> parkingParams;
    public RemoteData<PIDParams> stoppingParams;
    public RemoteData<PIDParams> lineAngleParams;
    public RemoteData<PIDParams> lineSpeedParams;

    // Control output
    public RemoteData<Integer> targetSpeed;
    public RemoteData<Integer> targetTurn;

    // Decision output
    public RemoteData<ControlMode> controlMode;

    // Calculated data
    public RemoteData<Double> distanceToMiddle;

    // Map
    public RemoteData<Map> map;

    private Car() {
        lateBinders = new ArrayList<>();

        aliveStatus = new RemoteData<>(new CommunicationPacket(PacketCommand.REQUEST_HEARTBEAT));

        CommunicationPacket sensorData = new CommunicationPacket(PacketCommand.REQUEST_SENSOR_DATA);
        accelerationX = new RemoteData<>(sensorData);
        accelerationY = new RemoteData<>(sensorData);
        accelerationZ = new RemoteData<>(sensorData);
        distance = new RemoteData<>(sensorData);
        speed = new RemoteData<>(sensorData);

        controlMode = new RemoteData<>(new CommunicationPacket(PacketCommand.REQUEST_MODE));

        temperature = new RemoteData<>(new CommunicationPacket(PacketCommand.REQUEST_TEMPERATURE));

        CommunicationPacket irRequest = new CommunicationPacket(PacketCommand.REQUEST_IR_DATA);
        distanceToLeft = new RemoteData<>(irRequest);
        distanceToRight = new RemoteData<>(irRequest);
        distanceToStop = new RemoteData<>(irRequest);

        distanceToMiddle = new RemoteData<>(new CommunicationPacket(PacketCommand.REQUEST_LATERAL_DISTANCE));

        CommunicationPacket turningParam = new CommunicationPacket(PacketCommand.REQUEST_CONTROL_PARAMETERS, new byte[]{1});
        turningParams = new RemoteData<>(turningParam);

        CommunicationPacket parkingParam = new CommunicationPacket(PacketCommand.REQUEST_CONTROL_PARAMETERS, new byte[]{2});
        parkingParams = new RemoteData<>(parkingParam);

        CommunicationPacket stoppingParam = new CommunicationPacket(PacketCommand.REQUEST_CONTROL_PARAMETERS, new byte[]{3});
        stoppingParams = new RemoteData<>(stoppingParam);

        CommunicationPacket lineAngleParam = new CommunicationPacket(PacketCommand.REQUEST_CONTROL_PARAMETERS, new byte[]{4});
        lineAngleParams = new RemoteData<>(lineAngleParam);

        CommunicationPacket lineSpeedParam = new CommunicationPacket(PacketCommand.REQUEST_CONTROL_PARAMETERS, new byte[]{5});
        lineSpeedParams = new RemoteData<>(lineSpeedParam);

        CommunicationPacket targetControl = new CommunicationPacket(PacketCommand.REQUEST_CONTROL_DECISION);

        targetSpeed = new RemoteData<>(targetControl);
        targetTurn = new RemoteData<>(targetControl);

        map = new RemoteData<>(null);
    }

    public static Car getInstance() {
        return instance;
    }

    public interface LateBind {
        public void call();
    }

    public void addLateBind(LateBind method) {
        lateBinders.add(method);
    }

    public void lateBindSubscribers() {
        for (LateBind m : lateBinders) {
            m.call();
        }
    }
}
