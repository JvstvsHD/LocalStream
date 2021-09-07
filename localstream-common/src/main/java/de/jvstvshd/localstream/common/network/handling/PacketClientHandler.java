package de.jvstvshd.localstream.common.network.handling;


import de.jvstvshd.localstream.common.network.packets.StartPlayPacket;
import de.jvstvshd.localstream.common.network.packets.TitleDataPacket;
import de.jvstvshd.localstream.common.network.packets.SearchSuggestionPacket;
import de.jvstvshd.localstream.common.network.packets.ServerResponsePacket;

/**
 * Packet handler whose implementations are found in the client.
 * All operations should be done in the client.
 */
public interface PacketClientHandler extends PacketHandler {

    /**
     * Handles a incoming response from the server.
     * @param packet Packet with response information.
     */
    void handleResponse(ServerResponsePacket packet);

    /**
     * Handle incoming data to play.
     * @param packet The packet with data to play.
     */
    void handleTitleData(TitleDataPacket packet);

    /**
     * Handles incoming search suggestions for the previous asked search query.
     * @param packet Packet containing the search suggestions with their {@link java.util.UUID}s.
     */
    void handleSearchSuggestions(SearchSuggestionPacket packet);

    /**
     * Handles the start of playing a new title with the given {@link javax.sound.sampled.AudioFormat}.
     * @param packet Packet containing the audio format.
     */
    void handleStartPlay(StartPlayPacket packet);
}
