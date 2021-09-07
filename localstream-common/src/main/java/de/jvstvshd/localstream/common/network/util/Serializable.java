package de.jvstvshd.localstream.common.network.util;


import de.jvstvshd.localstream.common.network.packets.PacketBuffer;

@Deprecated(forRemoval = true)
public interface Serializable {

    @Deprecated(forRemoval = true)
    void serialize(PacketBuffer byteBuf);
}
