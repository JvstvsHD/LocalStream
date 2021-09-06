package de.jvstvshd.localstream.client.gui;

import de.jvstvshd.localstream.client.media.AudioPlayer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

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

    public void changePlayState() {
        if (player.isPausing()) {
            player.resume();
            return;
        }
        player.pause();
    }

    public void stopPlaying() {
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
