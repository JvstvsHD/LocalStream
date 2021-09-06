package de.jvstvshd.localstream.network.util;


import de.jvstvshd.localstream.network.packets.PacketBuffer;

@Deprecated(forRemoval = true)
public interface Serializable {

    @Deprecated(forRemoval = true)
    void serialize(PacketBuffer byteBuf);
}
