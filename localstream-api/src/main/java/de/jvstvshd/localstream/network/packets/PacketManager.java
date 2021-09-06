package de.jvstvshd.localstream.network.packets;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public class PacketManager {

    private final List<Class<? extends Packet<?>>> packets = Lists.newArrayList();
    private ConnectionParticipant connectionParticipant;
    public List<Object> getPackets() {
        return Collections.unmodifiableList(packets);
    }

    public PacketManager() {
        add(LoginPacket.class)
                .add(TitleDataPacket.class)
                .add(TitleDataUploadPacket.class)
                .add(ServerResponsePacket.class)
                .add(TitlePacket.class)
                .add(SearchRequestPacket.class)
                .add(SearchSuggestionPacket.class)
                .add(StartPlayPacket.class)
                .add(TitlePlayPacket.class);

    }

    public PacketManager add(Class<? extends Packet<?>> clazz) {
        Class<? extends Packet<?>> packetClass;
        if (!Packet.class.isAssignableFrom(clazz)) {
            return this;
        }
        try {
            packetClass = clazz;
        } catch (ClassCastException exception) {
            exception.printStackTrace();
            return this;
        }
        packets.add(packetClass);
        return this;
    }

    @SuppressWarnings("rawtypes")
    public int getIdForPacket(Class<? extends Packet> clazz) {
        if (!packets.contains(clazz))
            return -1;
        return packets.indexOf(clazz);
    }

    public Packet<?> getPacketById(int id, PacketPriority priority) throws ReflectiveOperationException {
        Class<? extends Packet<?>> clazz = packets.get(id);
        return packets.get(id).getDeclaredConstructor(PacketPriority.class).newInstance(priority);
    }

    public void writePacket(PacketBuffer buffer, Packet<?> packet) {
        buffer.writeInt(getIdForPacket(packet));
        int priority = packet.getPriority().getPriority();
        buffer.writeInt(priority);
        packet.write(buffer);
    }

    @SuppressWarnings("unchecked")
    public int getIdForPacket(Packet<?> packet) {
        Class<? extends Packet<?>> packetClass = (Class<? extends Packet<?>>) packet.getClass();
        return getIdForPacket(packetClass);
    }

    public ConnectionParticipant getConnectionParticipant() {
        return connectionParticipant;
    }

    public PacketManager setConnectionParticipant(ConnectionParticipant connectionParticipant) {
        this.connectionParticipant = connectionParticipant;
        return this;
    }

    public enum ConnectionParticipant {
        CLIENT("client"),
        SERVER("server"),
        NOT_SET("unknown");

        private final String name;

        ConnectionParticipant(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
