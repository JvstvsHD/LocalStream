package de.jvstvshd.localstream.common.network.packets;

import java.util.Optional;

public interface PacketComparable<Type> {

    Optional<Type> compare(Type comparator);
}
