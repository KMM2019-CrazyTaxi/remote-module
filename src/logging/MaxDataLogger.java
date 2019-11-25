package logging;

/**
 * Max data logger. Logs data points at a set maxumum rate, set by a time interval.
 *
 * @param <T> Data type of logged data
 * @author Henrik Nilsson
 */
public class MaxDataLogger<T> extends DataLogger<T> {
    protected int limitTime;
    private long latestUpdate;

    /**
     * MaxDataLogger constructor.
     * @param limitTime Lowest possible time between data points
     */
    public MaxDataLogger(int limitTime) {
        this.limitTime = limitTime;
        this.latestUpdate = System.currentTimeMillis();
    }

    /**
     * Update the data value. This notifies all listeners and wakes waiters if the latest update was made longer than
     * limitTime ago.
     *
     * @param data New data value
     */
    @Override
    public synchronized void update(T data) {
        long currentTime = System.currentTimeMillis();

        if ((currentTime - latestUpdate) > limitTime){
            super.update(data);
            latestUpdate = currentTime;
        }
    }
}
