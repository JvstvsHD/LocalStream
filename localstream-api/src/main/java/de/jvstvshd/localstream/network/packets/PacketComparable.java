package de.jvstvshd.localstream.network.packets;

import java.util.Optional;

public interface PacketComparable<Type> {

    Optional<Type> compare(Type comparator);
}
