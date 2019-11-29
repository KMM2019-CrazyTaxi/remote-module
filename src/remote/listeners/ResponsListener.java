package remote.listeners;

import remote.datatypes.CommunicationPacket;

public interface ResponsListener {
    public void call(CommunicationPacket type);
}
