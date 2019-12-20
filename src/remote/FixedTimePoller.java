package remote;

import remote.datatypes.RemoteData;
import remote.listeners.DataListener;
import remote.Server;

import java.util.ArrayList;
import java.util.List;

public class FixedTimePoller implements Runnable, DataListener<Boolean> {

    private volatile boolean running = false;
    private int sleepDuration;
    private List<RemoteData> dataPollers;

    public FixedTimePoller(int sleepDuration) {
        this.sleepDuration = sleepDuration;
        this.dataPollers = new ArrayList<>();

        Car.getInstance().aliveStatus.subscribe(this);
    }

    synchronized public void add(RemoteData o) {
        dataPollers.add(o);
    }

    public void stop() {
        running = false;
    }

    synchronized public int getSleepDuration() {
        return sleepDuration;
    }

    synchronized public void setSleepDuration(int sleepDuration) {
        this.sleepDuration = sleepDuration;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    synchronized public void run() {
        running = true;

        while(running) {
            // Add poll requests
            for (RemoteData d : dataPollers){
                d.poll();
            }

            // Pull
            Server.getInstance().pull();

            // Sleep
            // TODO add more robust threading behavoiur
            try {
                Thread.sleep(sleepDuration);
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

    @Override
    public void update(Boolean data) {
        // Connection alive
        if (data.booleanValue()) {
            if (!running) {
                running = true;
                (new Thread(this)).start();
            }
        }
        else {
            running = false;
        }
    }
}
