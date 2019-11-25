package tests;

import enums.PacketCommand;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import remote.CommunicationPacket;
import remote.PacketList;
import remote.Server;
import remote.ServerConnection;

import java.io.IOException;

public class ManualControlMockup extends Application {
    public static void main(String[] args) {launch();}

    public void start(Stage primaryStage) throws Exception {
        ServerConnection server = new ServerConnection(Server.SERVER_IP, Server.SERVER_PORT);

        StackPane root = new StackPane();
        Scene scene = new Scene(root, 300, 250);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                CommunicationPacket pack = null;
                switch(event.getText()) {
                    case "w":
                        pack = new CommunicationPacket(PacketCommand.fromByte((byte) 0x04), 0, new byte[]{0x10});
                        break;
                    case "a":
                        pack = new CommunicationPacket(PacketCommand.fromByte((byte) 0x05), 0, new byte[]{0x10});
                        break;
                    case "s":
                        pack = new CommunicationPacket(PacketCommand.fromByte((byte) 0x04), 0, new byte[]{0x30});
                        break;
                    case "d":
                        pack = new CommunicationPacket(PacketCommand.fromByte((byte) 0x05), 0, new byte[]{0x20});
                        break;
                }
                System.out.println("Down: " + event.getText());

                PacketList l = new PacketList();
                l.addPacket(pack);
                byte[] data = l.toBytes();

//                System.out.println(l);
//                for (int i = 0; i < data.length; i++) {
//                    System.out.println(String.format("0x%02X", data[i]));
//                }
//                System.out.println("");

                try {
                    server.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        scene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
                public void handle(KeyEvent event) {
                    CommunicationPacket pack = null;
                    switch(event.getText()) {
                        case "w":
                            pack = new CommunicationPacket(PacketCommand.fromByte((byte) 0x04), 0, new byte[]{0x00});
                            break;
                        case "a":
                            pack = new CommunicationPacket(PacketCommand.fromByte((byte) 0x05), 0, new byte[]{0x00});
                            break;
                        case "s":
                            pack = new CommunicationPacket(PacketCommand.fromByte((byte) 0x04), 0, new byte[]{0x00});
                            break;
                        case "d":
                            pack = new CommunicationPacket(PacketCommand.fromByte((byte) 0x05), 0, new byte[]{0x00});
                            break;
                    }
                    System.out.println("Up: " + event.getText());

                    PacketList l = new PacketList();
                    l.addPacket(pack);
                    try {
                        server.write(l.toBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        primaryStage.setTitle("Control test!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
