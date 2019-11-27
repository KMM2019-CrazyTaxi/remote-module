package remote;

import enums.ControlMode;
import enums.PIDControlerType;
import enums.PacketCommand;
import remote.datatypes.CommunicationPacket;
import remote.datatypes.PIDParams;
import remote.datatypes.RemoteData;

public class Car {
    private static Car instance = new Car();

    // Sensor data
    // TODO Change to float
    public RemoteData<Integer> accelerationX;
    public RemoteData<Integer> accelerationY;
    public RemoteData<Integer> accelerationZ;
    public RemoteData<Integer> distance;
    public RemoteData<Integer> speed;

    public RemoteData<ControlMode> controlMode;

    public RemoteData<Float> temperature;

    // TODO change to float
    public RemoteData<Integer> distanceToLeft;
    public RemoteData<Integer> distanceToMiddle;
    public RemoteData<Integer> distanceToRight;

    public RemoteData<PIDParams> turningParams;
    public RemoteData<PIDParams> parkingParams;
    public RemoteData<PIDParams> stoppingParams;
    public RemoteData<PIDParams> lineAngleParams;
    public RemoteData<PIDParams> lineSpeedParams;

    private Car() {

        CommunicationPacket sensorData = new CommunicationPacket(PacketCommand.REQUEST_SENSOR_DATA);
        accelerationX = new RemoteData<>(sensorData);
        accelerationY = new RemoteData<>(sensorData);
        accelerationZ = new RemoteData<>(sensorData);
        distance = new RemoteData<>(sensorData);
        speed = new RemoteData<>(sensorData);

        controlMode = new RemoteData<>(new CommunicationPacket(PacketCommand.REQUEST_MODE));

        temperature = new RemoteData<>(new CommunicationPacket(PacketCommand.REQUEST_TEMPERATURE));

        CommunicationPacket lateralDist = new CommunicationPacket(PacketCommand.REQUEST_LATERAL_DISTANCE);
        distanceToLeft = new RemoteData<>(lateralDist);
        distanceToMiddle = new RemoteData<>(lateralDist);
        distanceToRight= new RemoteData<>(lateralDist);

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
    }

    public static Car getInstance() {
        return instance;
    }
}
