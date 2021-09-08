package de.jvstvshd.localstream.client.desktop.network;

import de.jvstvshd.localstream.client.desktop.gui.SearchDialogController;
import de.jvstvshd.localstream.client.desktop.media.AudioPlayer;
import de.jvstvshd.localstream.client.desktop.media.MediaSystem;
import de.jvstvshd.localstream.common.network.handling.PacketClientHandler;
import de.jvstvshd.localstream.common.network.packets.SearchSuggestionPacket;
import de.jvstvshd.localstream.common.network.packets.ServerResponsePacket;
import de.jvstvshd.localstream.common.network.packets.StartPlayPacket;
import de.jvstvshd.localstream.common.network.packets.TitleDataPacket;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.util.Optional;

import static org.apache.logging.log4j.LogManager.getLogger;

public class ClientPacketHandler implements PacketClientHandler {

    private static final Logger logger = getLogger(ClientPacketHandler.class);
    private final MediaSystem mediaSystem;
    private Optional<SearchDialogController> searchDialogController;

    public ClientPacketHandler(MediaSystem mediaSystem) {
        this.mediaSystem = mediaSystem;
        try {
            SourceDataLine dataLine = AudioSystem.getSourceDataLine(AudioSystem.getAudioInputStream(new File("D:\\test.wav")).getFormat());
            dataLine.open();
            FloatControl gainControl = (FloatControl) dataLine.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-20.0f);
            dataLine.start();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleResponse(ServerResponsePacket serverResponsePacket) {
        int responseCode = serverResponsePacket.getResponseCode();
        if (responseCode == 404 || responseCode < 0) {
            logger.error("Illegal response code detected. Response code: " + responseCode);
            throw new RuntimeException("Illegal response code for request " + serverResponsePacket.getId().toString());
        }
        mediaSystem.inject(new MediaSystem.SimpleResponse(null, serverResponsePacket.getId(), serverResponsePacket.getResponseCode()));
    }

    @Override
    public void handleTitleData(TitleDataPacket titleDataPacket) {
        if (titleDataPacket.getNumber() == -1) {
            ((AudioPlayer) mediaSystem.getCurrentMediaPlayer()).stop();
        }
        mediaSystem.getCurrentMediaPlayer().queue(titleDataPacket.getBytes());
    }

    @Override
    public void handleSearchSuggestions(SearchSuggestionPacket packet) {
        searchDialogController.ifPresent(searchDialogController -> {
            searchDialogController.showSearchSuggestions(packet.getSuggestions());
        });
    }

    @Override
    public void handleStartPlay(StartPlayPacket packet) {
        try {
            System.out.println(packet.getMetadata());
            AudioPlayer audioPlayer = (AudioPlayer) mediaSystem.startMediaPlayer(packet.getFormat(), packet.getMaxPacketsCount(), packet.getMetadata());
            audioPlayer.play();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setSearchDialogController(SearchDialogController searchDialogController) {
        this.searchDialogController = Optional.ofNullable(searchDialogController);
    }
}
