package de.jvstvshd.localstream.client.desktop.media;

import de.jvstvshd.localstream.client.desktop.util.CloseableLinkedList;
import de.jvstvshd.localstream.client.desktop.util.CloseableQueue;
import de.jvstvshd.localstream.client.desktop.util.activity.NetworkActivities;
import de.jvstvshd.localstream.client.desktop.util.requests.RequestSystem;
import de.jvstvshd.localstream.common.network.NetworkManager;
import de.jvstvshd.localstream.common.network.packets.PacketPriority;
import de.jvstvshd.localstream.common.network.packets.TitlePacket;
import de.jvstvshd.localstream.common.network.packets.TitlePlayPacket;
import de.jvstvshd.localstream.common.scheduling.Scheduler;
import de.jvstvshd.localstream.common.title.TitleMetadata;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class MediaSystem {

    private final NetworkManager manager;
    private final CloseableQueue<MediaUpload> pending = new CloseableLinkedList<>();
    private final List<MediaRequest> requests = new ArrayList<>();
    private final Scheduler scheduler;
    private final RequestSystem requestSystem;
    private boolean isQueueRunning;
    private MediaPlayer currentPlayer;
    private final NetworkActivities activities;

    public MediaSystem(NetworkManager manager, Scheduler scheduler, RequestSystem requestSystem, NetworkActivities activities) {
        this.manager = manager;
        this.scheduler = scheduler;
        this.requestSystem = requestSystem;
        this.activities = activities;
        this.isQueueRunning = false;
    }

    public void queue(File file) {
        Consumer<SimpleResponse> consumer = response -> {
            int responseCode = response.responseCode;
            if (responseCode == 1) { //true, title exists
                checkUpload(file);
            } else
                upload(file);

        };
        checkExistence(UUID.randomUUID(), consumer, TitleMetadata.create(file.getName(), -1, -1, TitleMetadata.DEFAULT_INTERPRET, TitleMetadata.DEFAULT_TITLE_NAME, UUID.randomUUID()));
    }

    private void checkUpload(File file) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Dieser Titel existiert bereits. Soll er trotzdem hinzugefügt werden (bisherige Dateien werden überschrieben)?",
                    ButtonType.OK, ButtonType.NO);
            alert.setHeaderText(file.getName());
            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == ButtonType.CANCEL || buttonType == ButtonType.NO)
                    return;
                upload(file);
            });
        });
    }

    public MediaPlayer getCurrentMediaPlayer() {
        if (currentPlayer == null)
            return null;
        return currentPlayer;
    }

    public MediaPlayer startMediaPlayer(AudioFormat format, long maxPackets, TitleMetadata metadata) throws LineUnavailableException {
        AudioPlayer audioPlayer = new AudioPlayer(format, metadata.getName(), maxPackets, this, metadata);
        this.currentPlayer = audioPlayer;
        return audioPlayer;
    }

    public void shutdownPlayer(AudioPlayer player) {
        if (player == null) return;
        manager.sendPacket(new TitlePlayPacket(PacketPriority.HIGH, player.getMetadata(), TitlePlayPacket.TitlePlayAction.STOP));
    }

    private void upload(File file) {
        MediaUpload upload;
        try {
            upload = new MediaUpload(file, manager, PacketPriority.HIGH, activities);
            pending.add(upload);
            ensureQueueStarted();
        } catch (Throwable throwable) {
            Dialog<String> error = new Dialog<>();
            error.setContentText("Beim Hochladen der Datei trat folgender Fehler auf: \n" + ExceptionUtils.getStackTrace(throwable));
            error.show();
        }
    }

    private void ensureQueueStarted() {
        if (isQueueRunning)
            return;
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(() -> {
            while (!pending.isEmpty()) {
                MediaUpload upload = pending.poll();
                MediaUpload.UploadResult result = upload.performUpload();
                if (!result.isSuccess()) {
                    System.err.println("Could not upload " + upload.getFile().getAbsolutePath());
                    result.throwExceptions();
                }
            }
            isQueueRunning = false;
        });
    }

    public void checkExistence(MediaRequest request) {
        requests.add(request);
        request.request();
    }

    public void checkExistence(UUID uuid, Consumer<SimpleResponse> consumer, TitleMetadata metadata) {
        checkExistence(new MediaRequest(new MediaRequestData(uuid, consumer, metadata, TitlePacket.TitleAction.CHECK), requestSystem, manager));
    }

    public synchronized void inject(SimpleResponse response) {
        scheduler.runAsync(() -> requests.stream().filter(request -> request.getRequestData().getRequestId().equals(response.getRequestID())).findAny().ifPresent(mediaRequest -> {
            mediaRequest.getRequestData().getConsumer().accept(response);
            requests.removeIf(request -> request.getRequestData().getRequestId().equals(mediaRequest.getRequestData().getRequestId()));
        }));
    }

    public void pausePlayer(AudioPlayer player) {
        System.out.println("pause");
        manager.sendPacket(new TitlePlayPacket(PacketPriority.HIGH, player.getMetadata(), TitlePlayPacket.TitlePlayAction.PAUSE));
    }

    public void resumePlayer(AudioPlayer player) {
        manager.sendPacket(new TitlePlayPacket(PacketPriority.HIGH, player.getMetadata(), TitlePlayPacket.TitlePlayAction.RESUME));
    }

    public void shutdown() {
        try {
            pending.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        shutdownPlayer((AudioPlayer) currentPlayer);
        requestSystem.shutdown();
    }

    public record SimpleResponse(TitleMetadata metadata, UUID requestID,
                                 int responseCode) {
        public UUID getRequestID() {
            return requestID;
        }
    }
}
