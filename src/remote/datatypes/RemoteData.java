package remote.datatypes;

import enums.PacketCommand;
import remote.Server;
import remote.listeners.DataListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Remote data class representing a value stored on the autonomous system.
 *
 * @param <T> Type of stored value
 */
public class RemoteData<T> implements DataListener<T> {
    private T data = null;
    private PacketCommand requestCommand;
    private Server server;

    private List<DataListener<T>> listeners;

    private boolean oldData = false;

    /**
     * RemoteData constructor
     * @param data Initial data value
     * @param requestCommand Request to update this remote data
     * @param server Connecting server
     */
    public RemoteData(T data, PacketCommand requestCommand, Server server) {
        this.data = data;
        this.requestCommand = requestCommand;
        this.server = server;
        listeners = new ArrayList<>();
    }

    /**
     * RemoteData constructor
     * @param requestCommand Request to update this remote data
     * @param server Connecting server
     */
    public RemoteData(PacketCommand requestCommand, Server server) {
        this.requestCommand = requestCommand;
        this.server = server;
        listeners = new ArrayList<>();
    }

    /**
     * Get (cached) remote data value
     * @return Cached value
     */
    synchronized public T get() {
        return data;
    }

    /**
     * Update the data value. This notifies all listeners and wakes waiters
     * @param data New data value
     */
    synchronized public void update(T data){
        this.data = data;
        oldData = false;
        notifyAll();
        notifyListers(data);
    }

    /**
     * Update data value and wait until the new value is returned
     * @return New data value
     */
    synchronized public T fetch() {
        server.getRequestBuilder().addDatalessRequest(requestCommand);

        oldData = true;
        while(oldData) {
            // TODO Add more robust threading behavoiour
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return data;
    }

    /**
     * Add a poll request for this remote data in the request queue
     */
    synchronized public void poll() {
        server.getRequestBuilder().addDatalessRequest(requestCommand);
    }

    /**
     * Subscribe to value updates of this remote data
     * @param o Object to be notified
     */
    synchronized public void subscribe(DataListener<T> o) {
        listeners.add(o);
    }


    /**
     * Unsubscribe to value updates of this remote data
     * @param o Object to be no longer be notified
     */
    synchronized public void unsubscribe(DataListener<T> o) {
        listeners.remove(o);
    }

    synchronized private void notifyListers(T data) {
        for (DataListener<T> o : listeners) {
            o.update(data);
        }
    }
}
