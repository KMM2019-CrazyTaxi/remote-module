package remote.datatypes;

import enums.PacketCommand;
import helpers.DataConversionHelper;
import java.util.Arrays;

/**
 * Communication packet data structure class for communication with autonomous system.
 *
 * @author Henrik Nilsson
 */
public class CommunicationPacket {
    /**
     * Packet header size (in number of bytes)
     */
    public final static int HEADER_SIZE = 5;

    private PacketCommand command;
    private int id;
    private byte[] data;

    /**
     * CommunicationPacket constructor. This constructor is meant to construct default CommandPackets used for copying.
     * @param command Packet request/respons command
     */
    public CommunicationPacket(PacketCommand command) {
        this.command = command;
        this.id = 0;
        this.data = new byte[]{};
    }

    /**
     * CommunicationPacket constructor. This constructor is meant to construct default CommandPackets used for copying.
     * @param command Packet request/respons command
     * @param data Packet data
     */
    public CommunicationPacket(PacketCommand command, byte[] data) {
        this.command = command;
        this.id = 0;
        this.data = data.clone();
    }

    /**
     * CommunicationPacket constructor
     * @param id Unique packet id
     * @param command Packet request/respons command
     * @param data Packet data
     */
    public CommunicationPacket(PacketCommand command, int id, byte[] data) {
        this.command = command;
        this.id = id;
        this.data = data.clone();
    }

    /**
     * CommunicationPacket constructor
     * @param rawData Packet byte array, consisting of packet head (5 byte) and data
     */
    public CommunicationPacket(byte[] rawData, int offset) {
        //TODO Add packet structure analysis

        this.command = PacketCommand.fromByte(rawData[offset]);
        this.id = DataConversionHelper.byteArrayToUnsignedInt(rawData, offset + 1, 2);
        int size = DataConversionHelper.byteArrayToUnsignedInt(rawData, offset + 3, 2);

        this.data = new byte[size];
        System.arraycopy(rawData, offset + 5, this.data, 0, size);
    }

    public CommunicationPacket(CommunicationPacket request, int id) {
        this.command = request.command;
        this.id = id;
        this.data = request.data.clone();
    }


    /**
     * Get packet in raw byte form for sending to server
     * @return byte[] representing request/response
     */
    public byte[] toBytes() {
        byte[] bytes = new byte[data.length + HEADER_SIZE];

        // Type conversion
        byte[] idBytes = DataConversionHelper.intToByteArray(id, 2);
        byte[] sizeBytes = DataConversionHelper.intToByteArray(data.length, 2);

        // Packet header
        bytes[0] = command.code();
        bytes[1] = idBytes[0];
        bytes[2] = idBytes[1];
        bytes[3] = sizeBytes[0];
        bytes[4] = sizeBytes[1];

        // Data copy
        System.arraycopy(data, 0, bytes, 5, data.length);

        return bytes;
    }

    public PacketCommand getCommand() {
        return command;
    }

    public int getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    /**
     * Get packet data size
     * @return Size of data
     */
    public int dataSize() {
        return data.length;
    }

    @Override
    public String toString() {
        return "CommunicationPacket{" +
                "command=" + command +
                ", id=" + id +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
