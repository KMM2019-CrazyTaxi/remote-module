package logging;

/**
 * Limit max data logger. Combines the functionality of MaxDataLogger and LimitDataLogger to both limit update rate and
 * number of data point entries.
 *
 * @param <T> Data type of logged data
 * @author Henrik Nilsson
 */
public class LimitMaxLogger<T> extends MaxDataLogger<T> {
    private int dataLimit;

    /**
     * LimitMaxLogger constructor.
     * @param limitTime Lowest possible time between data points
     * @param dataLimit Maximum number of data point entries
     */
    public LimitMaxLogger(int limitTime, int dataLimit) {
        super(limitTime);
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
