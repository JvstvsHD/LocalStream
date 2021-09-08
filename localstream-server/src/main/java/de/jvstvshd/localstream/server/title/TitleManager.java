package de.jvstvshd.localstream.server.title;


import de.jvstvshd.localstream.common.network.NetworkManager;
import de.jvstvshd.localstream.common.scheduling.Concurrency;
import de.jvstvshd.localstream.common.scheduling.Scheduler;
import de.jvstvshd.localstream.common.title.TitleException;
import de.jvstvshd.localstream.common.title.TitleMetadata;
import de.jvstvshd.localstream.server.database.DataSourceManager;
import de.jvstvshd.localstream.server.file.FileManager;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class TitleManager {

    private final DataSourceManager dataSource;
    private final Scheduler scheduler;
    private final NetworkManager manager;
    private final Map<UUID, TitlePlayer> titlePlayers;

    public TitleManager(DataSourceManager dataSource, Scheduler scheduler, NetworkManager manager) {
        this.dataSource = dataSource;
        this.scheduler = scheduler;
        this.manager = manager;
        this.titlePlayers = new ConcurrentHashMap<>();
    }

    public CompletableFuture<Boolean> exists(String name) {

        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM titles WHERE name = '" + name + "'")) {

                return statement.executeQuery().next();
            } catch (Throwable t) {
                throw new TitleException("Could not retrieve existence status of title with name " + name, t);
            }
        });
    }

    public CompletableFuture<Boolean> canBePlayed(UUID id) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM titles WHERE id = '" + id.toString().toLowerCase() + "'")) {
                if (!statement.executeQuery().next())
                    return false;
                File file = getFile(id).get();
                return file != null && file.exists();
            } catch (Throwable t) {
                throw new TitleException("Could not retrieve existence status of title with id " + id.toString(), t);
            }
        });
    }

    private CompletableFuture<File> getFile(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM titles WHERE id = '" + uuid.toString().toLowerCase() + "'")) {
                ResultSet rs = statement.executeQuery();
                if (rs.next())
                    return new File(FileManager.parentDir, rs.getString(1));
                return null;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });

    }

    public CompletableFuture<Map<UUID, String>> getSuggestions(String query) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM titles WHERE name LIKE ?")) {
                String sQuery = "%" + query + "%";
                statement.setString(1, sQuery);
                ResultSet rs = statement.executeQuery();
                Map<UUID, String> suggestions = new HashMap<>();
                while (rs.next()) {
                    final String current = rs.getString(1);
                    final UUID currentID = UUID.fromString(rs.getString(2));
                    suggestions.put(currentID, current);
                }
                return suggestions;
            } catch (Exception exception) {
                exception.printStackTrace();
                return new HashMap<>();
            }
        }, Concurrency.GLOBAL_SERVICE);
    }

    public void play(TitleMetadata metadata, NetworkManager manager) {
        scheduler.runAsync(() -> {
            try {
                File file = getFile(metadata.getUuid()).get();
                TitlePlayer player = new TitlePlayer(file, manager, metadata, this, scheduler);
                registerPlayer(metadata.getUuid(), player);
                manager.registerNetworkTask(player);
                player.play();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void registerPlayer(UUID uuid, TitlePlayer player) {
        titlePlayers.put(uuid, player);
    }

    public void unregisterPlayer(UUID uuid) {
        titlePlayers.remove(uuid);
    }

    public AudioFormat getAudioFormat(File file) throws IOException {

        try {
            return AudioSystem.getAudioInputStream(file).getFormat();
        } catch (Exception e) {
            throw new IOException("Cannot compute AudioFormat of file " + file.getAbsolutePath(), e);
        }
    }

    public Optional<TitlePlayer> getTitlePlayer(UUID uuid) {
        for (Map.Entry<UUID, TitlePlayer> uuidTitlePlayerEntry : titlePlayers.entrySet()) {
            System.out.println(uuidTitlePlayerEntry);
        }
        return Optional.ofNullable(titlePlayers.get(uuid));
    }
}
