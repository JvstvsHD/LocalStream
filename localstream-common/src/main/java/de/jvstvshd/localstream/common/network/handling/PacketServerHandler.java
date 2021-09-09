package de.jvstvshd.localstream.common.network.handling;


import de.jvstvshd.localstream.common.network.packets.elements.*;

/**
 * Packet handler whose implementations are found in the client.
 * All operations should be done in the client.
 */
public interface PacketServerHandler extends PacketHandler {

    /**
     * Handles the login of a client.
     * @param packet packet with all important data.
     */
    void handleLogin(LoginPacket packet);

    /**
     * Handles the upload of a file.
     * @param packet A packet containing the data to be written and some metadata.
     */
    void handleUpload(TitleDataUploadPacket packet);

    /**
     * Handles incoming client requests such as deletions and existence checks.
     * @param packet Packet with some metadata. Might be uncomplete.
     */
    void handleTitle(TitlePacket packet);

    /**
     * Handles incoming search requests..
     * @param packet Packet with search request.
     */
    void handleSearchRequest(SearchRequestPacket packet);

    /**
     * Handles packets for controlling the playing of titles.
     * @param packet packet with action and the metadata for the title
     * @deprecated as {@link TitlePlayPacket} is it.
     */
    @Deprecated(forRemoval = true)
    void handleTitlePlay(TitlePlayPacket packet);
}
