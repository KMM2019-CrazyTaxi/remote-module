package remote;

import enums.ControlMode;
import enums.PacketCommand;

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

    public Car() {
        Server server = Server.getInstance();

        accelerationX = new RemoteData<>(PacketCommand.REQUEST_SENSOR_DATA, server);
        accelerationY = new RemoteData<>(PacketCommand.REQUEST_SENSOR_DATA, server);
        accelerationZ = new RemoteData<>(PacketCommand.REQUEST_SENSOR_DATA, server);
        distance = new RemoteData<>(PacketCommand.REQUEST_SENSOR_DATA, server);
        speed = new RemoteData<>(PacketCommand.REQUEST_SENSOR_DATA, server);

        controlMode = new RemoteData<>(PacketCommand.REQUEST_MODE, server);

        temperature = new RemoteData<>(PacketCommand.REQUEST_TEMPERATURE, server);

        distanceToLeft = new RemoteData<>(PacketCommand.REQUEST_LATERAL_DISTANCE, server);
        distanceToMiddle = new RemoteData<>(PacketCommand.REQUEST_LATERAL_DISTANCE, server);
        distanceToRight= new RemoteData<>(PacketCommand.REQUEST_LATERAL_DISTANCE, server);
    }

    public static Car getInstance() {
        return instance;
    }
}
