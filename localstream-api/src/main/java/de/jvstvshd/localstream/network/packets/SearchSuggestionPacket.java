package de.jvstvshd.localstream.network.packets;

import de.jvstvshd.localstream.network.handling.PacketClientHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SearchSuggestionPacket extends ClientHandlingPacket {

    //private final ArrayList<String> suggestions;
    private final Map<UUID, String> suggestions;

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

    public Map<UUID, String> getSuggestions() {
        return suggestions;
    }

    @Override
    public void process(PacketClientHandler handler) {
        handler.handleSearchSuggestions(this);
    }
}
