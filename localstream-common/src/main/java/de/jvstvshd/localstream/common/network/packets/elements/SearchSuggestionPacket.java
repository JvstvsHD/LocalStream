package de.jvstvshd.localstream.common.network.packets.elements;

import de.jvstvshd.localstream.common.network.handling.PacketClientHandler;
import de.jvstvshd.localstream.common.network.packets.PacketBuffer;
import de.jvstvshd.localstream.common.network.packets.PacketPriority;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A packet for sending suggestion based on a previous by the client sent keyword back to the client.
 */
public class SearchSuggestionPacket extends ClientHandlingPacket {

    private final Map<UUID, String> suggestions;

    /**
     * Creates a new SearchSuggestionPacket.
     * @param priority see {@link de.jvstvshd.localstream.common.network.packets.Packet#Packet(PacketPriority)}
     */
    public SearchSuggestionPacket(PacketPriority priority) {
        super(priority);
        suggestions = new HashMap<>();
    }

    @Override
    public void read(PacketBuffer buffer) {
        int suggestionCount = buffer.readInt();
        for (int i = 0; i < suggestionCount; i++) {
            suggestions.put(buffer.readUniqueId(), buffer.readString());
        }
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeInt(suggestions.size());
        for (Map.Entry<UUID, String> entry : suggestions.entrySet()) {
            buffer.writeUniqueId(entry.getKey());
            buffer.writeString(entry.getValue());
        }
    }

    /**
     * @return the suggestions matching the previous sent keyword.
     */
    public Map<UUID, String> getSuggestions() {
        return suggestions;
    }

    @Override
    public void process(PacketClientHandler handler) {
        handler.handleSearchSuggestions(this);
    }
}
