package tests;

import enums.PacketCommand;
import logging.*;
import remote.RemoteData;
import remote.Server;

public class LoggingTest {
    public static void main(String[] args) throws InterruptedException {
        RemoteData<Integer> intData = new RemoteData<>(PacketCommand.REQUEST_LATERAL_DISTANCE, Server.getInstance());
        DataLogger<Integer> logger = new LimitDataLogger<>(15);

        intData.subscribe(logger);

        for (int i = 0; i < 1000; i++) {
            intData.update(i);
            Thread.sleep(10);
        }

        for (DataPoint<Integer> p : logger.getLogPoints()) {
            System.out.println((p.getTime().getNano() / 100000) + ": " + p.getValue());
        }

        System.out.println(logger);
    }
}
