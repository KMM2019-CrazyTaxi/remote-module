package logging;

import remote.listeners.DataListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data logger class. This class stores and logs data points.
 *
 * @param <T> Data type of logged data
 * @author Henrik Nilsson
 */
public class DataLogger<T> implements DataListener<T> {
    protected volatile List<DataPoint<T>> data;
    private List<DataListener<T>> listeners;

    /**
     * DataLogger constructor.
     */
    public DataLogger() {
        this.data = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }

    /**
     * Get list of logged data point entries
     * @return DataPoint list
     */
    public List<DataPoint<T>> getLogPoints() {
        return Collections.unmodifiableList(data);
    }

    /**
     * Update the data value. This notifies all listeners and wakes waiters
     * @param data New data value
     */
    synchronized public void update(T data){
        this.data.add(new DataPoint<>(data));
        notifyListers(data);
    }

    /**
     * Subscribe to value updates of this data log
     * @param o Object to be notified
     */
    synchronized public void subscribe(DataListener<T> o) {
        listeners.add(o);
    }

    /**
     * Unsubscribe to value updates of this data log
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

    @Override
    public String toString() {
        StringBuilder logString = new StringBuilder("Log:");

        for(DataPoint<T> dp : data) {
            logString.append("\n");
            logString.append(dp);
        }

        return logString.toString();
    }
}
