package de.jvstvshd.localstream.client.desktop.media;

import de.jvstvshd.localstream.client.desktop.LocalStreamClient;
import de.jvstvshd.localstream.client.desktop.gui.PlayerGuiController;
import de.jvstvshd.localstream.common.title.TitleMetadata;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class AudioPlayer extends MediaPlayer {

    private boolean allowQueuing;
    private boolean pausing;
    private final ReentrantLock lock;
    private PlayerGuiController associatedController;
    private final SourceDataLine dataLine;
    private final AudioFormat format;
    private final MediaSystem mediaSystem;
    private final TitleMetadata metadata;

    public AudioPlayer(AudioFormat format, String title, long maxPackets, MediaSystem mediaSystem, TitleMetadata metadata) throws LineUnavailableException {
        super(format, title, maxPackets, metadata.getLength());
        this.mediaSystem = mediaSystem;
        this.metadata = metadata;
        allowQueuing = false;
        lock = new ReentrantLock();
        this.format = format;
        dataLine = AudioSystem.getSourceDataLine(null);
    }

    @Override
    public synchronized void play() throws IOException {
        try {
            dataLine.open(format);
            dataLine.start();
            allowQueuing = true;
            createPlayerGui();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized void queue(byte[] data) {
        if (!allowQueuing)
            return;
        packets++;
        dataLine.write(data, 0, data.length);
        double progress = (dataLine.getMicrosecondPosition() / (1000D) / (getDurationTotal() * 1000D));
        Platform.runLater(() -> {
            associatedController.getProgressBar().setProgress(progress);
            associatedController.setTitleName(formatDate(dataLine.getMicrosecondPosition() / (1000 * 1000) * 1000) + "/" + formatDate(getDurationTotal() * 1000));
        });

    }

    public String formatDate(long difference) {
        long days;
        long hours = 0;
        long minutes = 0;
        long seconds = 0;
        long DAY = 1000 * 60 * 60 * 24;
        if (difference - DAY > 0) {
            days = (difference - (difference % DAY)) / DAY;
            difference = difference - (days * DAY);
        }
        long HOUR = 1000 * 60 * 60;
        if (difference - HOUR > 0) {
            hours = (difference - (difference % HOUR)) / HOUR;
            difference = difference - (hours * HOUR);
        }
        long MINUTE = 1000 * 60;
        if (difference - MINUTE > 0) {
            minutes = (difference - (difference % MINUTE)) / MINUTE;
            difference = difference - (minutes * MINUTE);
        }
        long SECOND = 1000;
        if (difference - SECOND > 0) {
            seconds = (difference - (difference % SECOND)) / SECOND;
        }
        return formatNumber(hours) + ":" + formatNumber(minutes) + ":" + formatNumber(seconds);
    }


    private String formatNumber(long number) {
        String n = String.valueOf(number);
        return n.length() > 2 ? n : build(2 - n.length()) + n;
    }

    private String build(int l) {
        return "0".repeat(Math.max(0, l));
    }

    private void createPlayerGui() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(LocalStreamClient.class.getResource("player-gui.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle(getTitle());
                stage.setOnCloseRequest(event -> stop());
                associatedController = loader.getController();
                associatedController.setTitleName(getTitle());
                associatedController.setPlayer(this);
                associatedController.setStage(stage);
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void stop() {
        lock.lock();
        mediaSystem.shutdownPlayer(this);
        allowQueuing = false;
        dataLine.drain();
        System.out.println("Drained data");
        dataLine.stop();
        dataLine.close();
        lock.unlock();
    }

    public void pause() {
        lock.lock();
        mediaSystem.pausePlayer(this);
        pausing = true;
        dataLine.stop();
        lock.unlock();
    }

    public void resume() {
        lock.lock();
        mediaSystem.resumePlayer(this);
        dataLine.start();
        pausing = false;
        allowQueuing = true;
        lock.unlock();
    }

    public boolean isPausing() {
        return pausing;
    }

    public TitleMetadata getMetadata() {
        return metadata;
    }
}
