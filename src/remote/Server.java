package remote;

import enums.ControlMode;
import enums.PacketCommand;
import exceptions.MissingIDException;
import helpers.DataConversionHelper;
import remote.datatypes.CommunicationPacket;
import remote.datatypes.Error;
import remote.datatypes.PacketList;
import remote.listeners.AcknowledgementListener;
import remote.listeners.ErrorListener;
import remote.listeners.ExceptionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static Server instance = new Server();

    /**
     * Default host IP adress
     */
    public static final String SERVER_IP = "192.168.4.1";
    /**
     * Default host port number
     */
    public static final int SERVER_PORT = 20001;

    private ServerConnection serverConnection;
    private RequestIDBroker requestIDBroker;
    private RequestBuilder requestBuilder;

    private List<AcknowledgementListener> acknowledgementListeners;
    private List<ErrorListener> errorListeners;
    private List<ExceptionListener> exceptionListeners;

    private volatile boolean builderLocked = true;

    private Server() {
        this.serverConnection = new ServerConnection();
        this.requestIDBroker = new RequestIDBroker();
        this.requestBuilder = new RequestBuilder(requestIDBroker);

        acknowledgementListeners = new ArrayList<>();
        errorListeners = new ArrayList<>();
        exceptionListeners = new ArrayList<>();

        builderLocked = false;
    }

    public int getID() {
        return requestIDBroker.getID();
    }

    /**
     * Connect to remote
     * @throws IOException If the connection could not be made
     * @throws java.net.UnknownHostException If the default host could not be reached
     */
    synchronized public void connect() throws IOException, java.net.UnknownHostException {
        serverConnection.connect(SERVER_IP, SERVER_PORT);
    }

    /**
     * Connect to remote
     * @param ip IP-adress to connect to
     * @param port Port number to connect to
     * @throws IOException If the connection could not be made
     * @throws java.net.UnknownHostException If the given host could not be reached
     */
    synchronized public void connect(String ip, int port) throws IOException, java.net.UnknownHostException {
        serverConnection.connect(ip, port);
    }

    public void addAcknowledgementListener(AcknowledgementListener o) {
        acknowledgementListeners.add(o);
    }

    public void addErrorListener(ErrorListener o) {
        errorListeners.add(o);
    }

    public void addExceptionListener(ExceptionListener o) {
        exceptionListeners.add(o);
    }

    public void removeAcknowledgementListener(AcknowledgementListener o) {
        acknowledgementListeners.remove(o);
    }

    public void removeErrorListener(ErrorListener o) {
        errorListeners.remove(o);
    }

    public void removeExceptionListener(ExceptionListener o) {
        exceptionListeners.remove(o);
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

    synchronized public void pull() {
        // Lock builder and copy it, letting new commands be added to the new builder
        builderLocked = true;

        RequestBuilder request = this.requestBuilder;
        this.requestBuilder = new RequestBuilder(this.requestIDBroker);

        builderLocked = false;
        notifyAll();

        // Send request
        try {
            serverConnection.write(request.getPackets().toBytes());
        } catch (IOException e) {
            notifyExceptionListeners(e);
        }

        // Handle respons
        try {
            handlePackets(new PacketList(serverConnection.read()));
        } catch (IOException | MissingIDException e) {
            notifyExceptionListeners(e);
        }
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
                            handleLateralDistanceData(packet);
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

    private void handleLateralDistanceData(CommunicationPacket packet) {
        Car.getInstance().distanceToMiddle.update(DataConversionHelper.byteArrayToInt(packet.getData(), 0, 2));
        Car.getInstance().distanceToRight.update(DataConversionHelper.byteArrayToInt(packet.getData(), 2, 2));
        Car.getInstance().distanceToLeft.update(DataConversionHelper.byteArrayToInt(packet.getData(), 4, 2));
    }

    private void handleSensorData(CommunicationPacket packet) {
        Car.getInstance().accelerationX.update(DataConversionHelper.byteArrayToInt(packet.getData(), 0, 2));
        Car.getInstance().accelerationY.update(DataConversionHelper.byteArrayToInt(packet.getData(), 2, 2));
        Car.getInstance().accelerationZ.update(DataConversionHelper.byteArrayToInt(packet.getData(), 4, 2));
        Car.getInstance().distance.update(DataConversionHelper.byteArrayToInt(packet.getData(), 6, 1));
        Car.getInstance().speed.update(DataConversionHelper.byteArrayToInt(packet.getData(), 7, 1));
    }

    private void handleModeData(CommunicationPacket packet) {
        Car.getInstance().controlMode.update(ControlMode.fromByte(packet.getData()[0]));
    }

    private void handleControlParameterData(CommunicationPacket packet) {
        //TODO Decode control parameters
    }

    private void handleTemperatureData(CommunicationPacket packet) {
        Car.getInstance().temperature.update(DataConversionHelper.byteArrayToFloat(packet.getData()));
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

    private void notifyExceptionListeners(Exception e) {
        for (ExceptionListener o : exceptionListeners) {
            o.call(e);
        }
    }

    public static Server getInstance() {
        return instance;
    }
}
