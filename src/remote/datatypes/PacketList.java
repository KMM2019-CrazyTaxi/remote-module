package remote.datatypes;

import enums.PacketCommand;
import exceptions.IncorrectDataException;
import helpers.DataConversionHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A class for keeping packets to be sent to or recieved from the autonomous system.
 *
 * @author Henrik Nilsson
 */
public class PacketList implements Iterable<CommunicationPacket> {

    private List<CommunicationPacket> packets;

    /**
     * PacketList constructor
     */
    public PacketList() {
        packets = new ArrayList<>();
    }


    /**
     * PacketList constructor
     * @param pack Communication packet to be added directly to the list
     */
    public PacketList(CommunicationPacket pack) {
        packets = new ArrayList<>(1);
        packets.add(pack);
    }

    /**
     * PacketList constructor
     * @param rawData Raw server data byte[] to be decoded into packet list
     * @throws IncorrectDataException Thrown if raw data could not be decoded correctly
     */
    public PacketList(byte[] rawData) throws IncorrectDataException {
        packets = new ArrayList<>();

        int numberOfPackets = DataConversionHelper.byteArrayToInt(new byte[]{rawData[0]});
        int offset = 1;

        while(offset < rawData.length) {
            CommunicationPacket pack = new CommunicationPacket(rawData, offset);
            this.addPacket(pack);

            offset += CommunicationPacket.HEADER_SIZE + pack.dataSize();
        }

        if (numberOfPackets != packets.size())
            throw new IncorrectDataException("Number of given and decoded packages does not match (" + numberOfPackets + " given, " + packets.size() + " decoded).");
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
        int size = 1;

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

        byte[] bytes = new byte[byteSize()];
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

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<CommunicationPacket> iterator() {
        return packets.iterator();
    }

    public boolean contains(PacketCommand type) {
        for (CommunicationPacket pack : packets) {
            if (pack.getCommand() == type)
                return true;
        }
        return false;
    }

    public CommunicationPacket get(PacketCommand type) {
        for (CommunicationPacket pack : packets) {
            if (pack.getCommand() == type)
                return pack;
        }
        return null;
    }
}
