package remote.listeners;

import enums.PacketCommand;

public interface AcknowledgementListener {
    public void call(PacketCommand type, int id);
}
