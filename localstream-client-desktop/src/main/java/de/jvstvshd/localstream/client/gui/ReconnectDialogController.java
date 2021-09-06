package de.jvstvshd.localstream.client.gui;

import de.jvstvshd.localstream.network.NetworkManagerImpl;
import de.jvstvshd.localstream.scheduling.ScheduleTask;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.concurrent.TimeUnit;

public class ReconnectDialogController extends GuiController {

    @FXML
    public ScrollPane text;
    @FXML
    public Button reconnect;
    public Stage stage;

    private int cooldown;
    private ScheduleTask task;

    public void initReconnectCooldown(Throwable throwable) {
        try {
            for (Window window : Window.getWindows()) {
                if (window instanceof Stage stage) {
                    if (stage.equals(client.getPrimaryStage()))
                        continue;
                    stage.hide();
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        }
        Text error = new Text("Fehler beim Verbindungsaufbau: " + throwable.getLocalizedMessage() + "\n" + ExceptionUtils.getStackTrace(throwable));
        error.setFont(new Font("arial", 12.0));
        text.setContent(error);
        stage.sizeToScene();
        cooldown = 4;
        reconnect.setDisable(true);
        task = client.getScheduler().scheduleAsync(() -> {
            Platform.runLater(() -> {
                cooldown--;
                reconnect.setText("neu verbinden (" + cooldown + "s)");
                if (cooldown == 0) {
                    reconnect.setText("erneut verbinden");
                    reconnect.setDisable(false);
                    task.cancel();
                }
            });

        }, 1L, TimeUnit.SECONDS);
    }

    public void reconnect() {
        try {
            client.getNettyClient().start(new NetworkManagerImpl());
            stage.hide();
        } catch (Exception e) {
            e.printStackTrace();

            initReconnectCooldown(e);
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        stage.hide();
    }
}
