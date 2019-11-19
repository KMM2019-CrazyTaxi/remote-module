package remote;

import enums.ControlMode;
import enums.PacketCommand;
import exceptions.ConnectionClosedException;
import exceptions.IncorrectDataException;
import exceptions.MissingIDException;
import helpers.DataConversionHelper;
import remote.listeners.AcknowledgementListener;
import remote.listeners.ErrorListener;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static final String SERVER_IP = "192.168.4.1";
    public static final int SERVER_PORT = 20001;

    private ServerConnection serverConnection;
    private RequestIDBroker requestIDBroker;
    private RequestBuilder requestBuilder;

    private List<AcknowledgementListener> acknowledgementListeners;
    private List<ErrorListener> errorListeners;

    private Car car;

    private volatile boolean builderLocked = true;

    public Server() throws IOException, java.net.UnknownHostException {
        this.serverConnection = new ServerConnection(SERVER_IP, SERVER_PORT);
        this.requestIDBroker = new RequestIDBroker();
        this.requestBuilder = new RequestBuilder(requestIDBroker);

        acknowledgementListeners = new ArrayList<>();
        errorListeners = new ArrayList<>();

        this.car = new Car(this);

        builderLocked = false;
    }

    public void addAcknowledgementListener(AcknowledgementListener o) {
        acknowledgementListeners.add(o);
    }

    public void addErrorListener(ErrorListener o) {
        errorListeners.add(o);
    }

    public void removeAcknowledgementListener(AcknowledgementListener o) {
        acknowledgementListeners.remove(o);
    }

    public void removeErrorListener(ErrorListener o) {
        errorListeners.remove(o);
    }

    public Car getCar() {
        return car;
    }

    synchronized public RequestBuilder getRequestBuilder() {
        while(builderLocked) {
            // TODO Add more robust threading behavoiour
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();}
        }
        return requestBuilder;
    }

    synchronized public void pull() throws IOException, UnknownHostException, ConnectionClosedException, IncorrectDataException, MissingIDException {
        // Lock builder and copy it, letting new commands be added to the new builder
        builderLocked = true;

        RequestBuilder request = this.requestBuilder;
        this.requestBuilder = new RequestBuilder(this.requestIDBroker);

        builderLocked = false;
        notifyAll();

        // Send request
        try {
            serverConnection.write(request.getPackets().toBytes());
        } catch (ConnectionClosedException e) {
            serverConnection.connect();
            serverConnection.write(request.getPackets().toBytes());
        }
        // Handle respons
        handlePackets(new PacketList(serverConnection.read()));
    }

    private void handlePackets(PacketList packets) throws MissingIDException {
        for (CommunicationPacket packet : packets) {
            try {
                requestIDBroker.releaseID(packet.getId());
            } catch (MissingIDException e) {
                throw new MissingIDException("Recieved unknown ID.", e);
            }

            switch(packet.getCommand().getType()) {
                case REQUEST:
                    throw new IllegalStateException("Got unexpected request as respons: " + packet.getCommand());

                case ACKNOWLEDGEMENT:
                    notifyAcknowledgementListeners(packet.getCommand(), packet.getId());
                    break;

                case ERROR:
                    notifyErrorListeners(packet.getCommand(), new Error(packet));
                    break;

                case DATA:
                    switch (packet.getCommand()) {
                        case CURRENT_SENSOR_DATA:
                            handleSensorData(packet);
                            break;
                        case CURRENT_MODE:
                            handleModeData(packet);
                            break;
                        case CURRENT_CONTROL_PARAMETERS:
                            handleControlParameterData(packet);
                            break;
                        case CURRENT_ROUTE_STATUS:
                            //handleRouteStatusData(packet);
                            break;
                        case CURRENT_MAP_LOCATION:
                            //handleMapLocationData(packet);
                            break;
                        case CURRENT_LATERAL_DISTANCE:
                            break;
                        case CURRENT_CONTROL_DECISION:
                            break;
                        case CURRENT_STOP_LINE_FOUND:
                            break;
                        case CURRENT_PASSED_DISTANCE:
                            break;
                        case CURRENT_TEMPERATURE:
                            handleTemperatureData(packet);
                            break;
                        case CAMERA_IMAGE:
                            break;
                        default:
                            System.out.println("ILLEGAL ARG!");
                            throw new IllegalStateException("You either forgot to add new Data command in 'handlePackets' or 'getType': (" + packet.getCommand() + ")");
                    }
                    break;
            }
        }
    }

    private void handleSensorData(CommunicationPacket packet) {
        car.accelerationX.update(DataConversionHelper.byteArrayToInt(packet.getData(), 0, 2));
        car.accelerationY.update(DataConversionHelper.byteArrayToInt(packet.getData(), 2, 2));
        car.accelerationZ.update(DataConversionHelper.byteArrayToInt(packet.getData(), 4, 2));
        car.distance.update(DataConversionHelper.byteArrayToInt(packet.getData(), 6, 1));
        car.speed.update(DataConversionHelper.byteArrayToInt(packet.getData(), 7, 1));
    }

    private void handleModeData(CommunicationPacket packet) {
        car.controlMode.update(ControlMode.fromByte(packet.getData()[0]));
    }

    private void handleControlParameterData(CommunicationPacket packet) {
        //TODO Decode control parameters
    }

    private void handleTemperatureData(CommunicationPacket packet) {
        car.temperature.update(DataConversionHelper.byteArrayToFloat(packet.getData()));
    }

    private void notifyErrorListeners(PacketCommand type, Error e) {
        for (ErrorListener o : errorListeners) {
            o.call(e);
        }
    }

    private void notifyAcknowledgementListeners(PacketCommand type, int id) {
        for (AcknowledgementListener o : acknowledgementListeners) {
            o.call(type, id);
        }
    }
}
