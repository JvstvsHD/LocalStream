package de.jvstvshd.localstream.server.network;

import de.jvstvshd.localstream.common.network.NetworkManager;
import de.jvstvshd.localstream.common.network.handling.PacketServerHandler;
import de.jvstvshd.localstream.common.network.packets.elements.*;
import de.jvstvshd.localstream.common.title.TitleAction;
import de.jvstvshd.localstream.common.title.TitleMetadata;
import de.jvstvshd.localstream.server.LocalStreamServer;
import de.jvstvshd.localstream.server.file.FileManager;
import de.jvstvshd.localstream.server.file.FileUpload;
import de.jvstvshd.localstream.server.title.TitleManager;
import de.jvstvshd.localstream.server.title.TitlePlayer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class ServerPacketHandler implements PacketServerHandler {

    private final FileManager fileManager;
    private final NetworkManager manager;
    private final TitleManager titleManager;
    private final LocalStreamServer lss;

    public ServerPacketHandler(NetworkManager manager, TitleManager titleManager, LocalStreamServer lss) {
        this.manager = manager;
        this.titleManager = titleManager;
        this.lss = lss;
        this.fileManager = new FileManager(this.lss);
    }

    @Override
    public void handleLogin(LoginPacket packet) {

    }

    @Override
    public void handleUpload(TitleDataUploadPacket packet) {
        fileManager.getFileUpload("", TitleMetadata.builder().setUuid(packet.getUuid()).build()).queue(packet.getTransferData());
    }

    @Override
    public void handleTitle(TitlePacket packet) {
        System.out.println(packet.getAction());
        TitleAction action = packet.getAction();
        switch (packet.getAction()) {
            case ADD_START -> fileManager.getFileUpload(packet.getMetadata().getName(), packet.getMetadata()).queue(new byte[0]);
            case ADD_END -> fileManager.getFileUpload(packet.getMetadata().getName(), packet.getMetadata()).setState(FileUpload.State.COMPLETED);
            case REMOVE -> throw new UnsupportedOperationException("Not supported yet.");
            case CHECK -> titleManager.exists(packet.getMetadata().getName())
                    .thenAcceptAsync(aBoolean -> manager.sendPacket(new ServerResponsePacket(packet.getPriority(), aBoolean ? 1 : 0, packet.getRequestId())));
            case NOTHING -> System.err.println("Nothing was transmitted.");
            case PLAY -> titleManager.canBePlayed(packet.getMetadata().getUuid()).thenAcceptAsync(aBoolean -> {
                        if (!aBoolean) {
                            manager.sendPacket(new ServerResponsePacket(packet.getPriority(), 0, packet.getRequestId()));
                            return;
                        }
                        Connection connection = lss.getDataSourceManager().getConnection();
                        TitleMetadata tm =
                                TitleMetadata.resolve(packet.getMetadata().getUuid(), lss.getDataSourceManager().getConnection());
                        titleManager.play(tm, manager);
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
            case STOP, RESUME, PAUSE, ACQUIRE_DATA -> {
                System.out.println(packet.getAction());
                System.out.println(packet);
                Optional<TitlePlayer> optPlayer = titleManager.getTitlePlayer(packet.getMetadata().getUuid());
                if (optPlayer.isEmpty()) {
                    System.err.println("Could not found title player; metadata: " + packet.getMetadata());
                    return;
                }
                TitlePlayer player = optPlayer.get();
                System.out.println(action);
                lss.getScheduler().runAsync(() -> {
                    System.out.println(action);
                    switch (action) {
                        case PAUSE -> {
                            player.pause();
                        }
                        case RESUME -> {
                            player.resume(packet.getMetadata().getLength());
                        }
                        case STOP -> {
                            player.stop();
                        }
                        case ACQUIRE_DATA -> player.next();
                    }
                });
            }
        }
    }

    @Override
    public void handleSearchRequest(SearchRequestPacket packet) {
        titleManager.getSuggestions(packet.getRequest()).thenAcceptAsync(results -> {
            SearchSuggestionPacket ssp = new SearchSuggestionPacket(packet.getPriority());
            ssp.getSuggestions().putAll(results);
            manager.sendPacket(ssp);
        });
    }
}
