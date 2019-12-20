package logging;

/**
 * Limit data logger. Logs a set amount of maximum data point entries.
 *
 * @param <T> Data type of logged data
 * @author Henrik Nilsson
 */
public class LimitDataLogger<T> extends DataLogger<T> {
    private int dataLimit;

    /**
     * LimitDataLogger constructor.
     * @param dataLimit Maximum number of data point entries
     */
    public LimitDataLogger(int dataLimit) {
        this.dataLimit = dataLimit;
    }

    /**
     * Update the data value. This notifies all listeners and wakes waiters if the latest update was made longer than
     * limitTime ago. If the number of data points is larger than dataLimit the oldest data point is removed.
     *
     * @param data New data value
     */
    @Override
    public synchronized void update(T data) {
        super.update(data);
        if (this.data.size() > dataLimit)
            this.data.remove(0);
    }
}
