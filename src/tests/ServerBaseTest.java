package tests;

import enums.PacketCommand;

import java.io.IOException;

public class ServerBaseTest {
    public static void main(String[] args) throws IOException, java.net.UnknownHostException, exceptions.ConnectionClosedException {
        // Connect
        remote.ServerConnection server = new remote.ServerConnection("192.168.4.1", 20001);

        // Initialize builder and packets
        remote.PacketList builder = new remote.PacketList();
        builder.addPacket(new remote.CommunicationPacket(PacketCommand.REQUEST_HEARTBEAT, 1337, new byte[]{}));
        builder.addPacket(new remote.CommunicationPacket(PacketCommand.REQUEST_CAMERA_IMAGE, 420, new byte[]{}));

        // Communicate with server
        byte[] request = builder.toBytes();
        System.out.println("len(request) = " + request.length);

        server.write(request);
        byte[] data = server.read();

        for (int i = 0; i < data.length; i++) {
            System.out.println("Sent: " + String.format("0x%02X", request[i]) + ", Recieved: " + String.format("0x%02X", data[i]) + " (Equal: " + (request[i] == data[i]) + ")");
        }

        remote.PacketList respons = new remote.PacketList(data);
        System.out.println("Recieved " + respons.numberOfPackets() + " packets");

        System.out.println(respons);
    }
}
