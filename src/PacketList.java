import java.util.ArrayList;
import java.util.List;

/**
 * A class for keeping packets to be sent to or recieved from the autonomous system.
 *
 * @author Henrik Nilsson
 */
public class PacketList {

    private List<CommunicationPacket> packets;

    /**
     * RequestBuilder constructor
     */
    public PacketList() {
        packets = new ArrayList<>();
    }

    /**
     * RequestBuilder constructor
     */
    public PacketList(byte[] rawData) {
        int numberOfPackets = DataConversionHelper.byteArrayToInt(new byte[]{rawData[0]});
        int offset = 1;

        while(offset < rawData.length) {
            CommunicationPacket pack = new CommunicationPacket(rawData, offset);
            addPacket(pack);

            offset += CommunicationPacket.HEADER_SIZE + pack.dataSize();
        }

    }

    /**
     * Add communication packet to request
     * @param pack CommunicationPacket to be sent
     */
    public void addPacket(CommunicationPacket pack) {
        packets.add(pack);
    }

    /**
     * Get number of requests
     * @return Number of packets
     */
    public int numberOfPackets() {
        return packets.size();
    }

    /**
     * Get total size in bytes
     * @return Size in number of bytes
     */
    public int byteSize() {
        int size = 0;

        for (CommunicationPacket packet : packets) {
            size += packet.dataSize() + CommunicationPacket.HEADER_SIZE;
        }

        return size;
    }

    /**
     * Get bytes representation of packets
     * @return byte[] of packets
     */
    public byte[] toBytes() {
        List<byte[]> byteLists = new ArrayList<>();
        for (CommunicationPacket packet : packets) {
            byteLists.add(packet.toBytes());
        }

        byte[] bytes = new byte[byteSize() + 1];
        bytes[0] = (byte) packets.size();
        int offset = 1;

        for (byte[] packetBytes : byteLists) {
            System.arraycopy(packetBytes, 0, bytes, offset, packetBytes.length);
            offset += packetBytes.length;
        }

        return bytes;
    }

    @Override
    public String toString() {
        return "PacketList{" +
                "packets=" + packets +
                '}';
    }
}
