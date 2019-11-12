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

    private PacketType type;
    private int id;
    private byte[] data;

    /**
     * CommunicationPacket constructor
     * @param id Unique packet id
     * @param type Packet request/respons type
     * @param data Packet data
     */
    public CommunicationPacket(PacketType type, int id, byte[] data) {
        this.type = type;
        this.id = id;
        this.data = data.clone();
    }

    /**
     * CommunicationPacket constructor
     * @param rawData Packet byte array, consisting of packet head (5 byte) and data
     */
    public CommunicationPacket(byte[] rawData, int offset) {
        this.type = PacketType.fromByte(rawData[0]);
        this.id = DataConversionHelper.byteArrayToInt(new byte[]{rawData[1], rawData[2]});
        int size = DataConversionHelper.byteArrayToInt(new byte[]{rawData[3], rawData[4]});

        this.data = new byte[size];
        System.arraycopy(rawData, 5, this.data, 0, size);
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
        bytes[0] = type.code();
        bytes[1] = idBytes[0];
        bytes[2] = idBytes[1];
        bytes[3] = sizeBytes[0];
        bytes[4] = sizeBytes[1];

        // Data copy
        System.arraycopy(data, 0, bytes, 5, data.length);

        return bytes;
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
                "type=" + type +
                ", id=" + id +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
