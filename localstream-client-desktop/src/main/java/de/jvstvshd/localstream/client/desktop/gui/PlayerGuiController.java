package de.jvstvshd.localstream.client.desktop.gui;

import de.jvstvshd.localstream.client.desktop.media.AudioPlayer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.io.IOException;

public class PlayerGuiController extends GuiController {

    private AudioPlayer player;
    @FXML
    private Label text;
    @FXML
    private Label time;
    @FXML
    private ProgressBar progressBar;
    private Stage stage;

    public void setTitleName(String titleName) {
        text.setText(titleName);
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public Label getTime() {
        return time;
    }

    public void changePlayState() throws IOException {
        if (player.isPausing()) {
            player.resume();
            return;
        }
        player.pause();
    }

    public void stopPlaying() throws IOException {
        player.stop();
        stage.close();
    }

    public void setPlayer(AudioPlayer player) {
        this.player = player;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }
}
