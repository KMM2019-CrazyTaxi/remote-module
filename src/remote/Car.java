package remote;

import enums.ControlMode;
import enums.PacketCommand;

public class Car {
    // Sensor data
    public RemoteData<Integer> accelerationX;
    public RemoteData<Integer> accelerationY;
    public RemoteData<Integer> accelerationZ;
    public RemoteData<Integer> distance;
    public RemoteData<Integer> speed;

    public RemoteData<ControlMode> controlMode;

    public RemoteData<Float> temperature;

    public Car(Server server) {
        accelerationX = new RemoteData<>(PacketCommand.REQUEST_SENSOR_DATA, server);
        accelerationY = new RemoteData<>(PacketCommand.REQUEST_SENSOR_DATA, server);
        accelerationZ = new RemoteData<>(PacketCommand.REQUEST_SENSOR_DATA, server);
        distance = new RemoteData<>(PacketCommand.REQUEST_SENSOR_DATA, server);
        speed = new RemoteData<>(PacketCommand.REQUEST_SENSOR_DATA, server);

        controlMode = new RemoteData<>(PacketCommand.REQUEST_MODE, server);

        temperature = new RemoteData<>(PacketCommand.REQUEST_TEMPERATURE, server);
    }
}
