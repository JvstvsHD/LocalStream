package de.jvstvshd.localstream.client.gui;

import de.jvstvshd.localstream.client.LocalStreamClient;
import de.jvstvshd.localstream.client.network.ClientPacketHandler;
import de.jvstvshd.localstream.network.packets.Packet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainGuiController extends GuiController {

    @FXML
    public void onUpload() {
        FileChooser fileChooser = new FileChooser();
        File initial = new File(System.getProperty("user.home") + "\\Music");
        fileChooser.setInitialDirectory(initial);
        List<File> fileList = fileChooser.showOpenMultipleDialog(client.getPrimaryStage());
        if (fileList == null)
            return;
        for (File file : fileList) {
            try {
                client.getMediaSystem().queue(file);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    public void onSearchStart() {
        try {
            FXMLLoader loader = new FXMLLoader(LocalStreamClient.class.getResource("search-dialog-gui.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            Scene scene = new Scene(loader.load());
            stage.setOnCloseRequest(event -> ((ClientPacketHandler) Packet.defaultHandler).setSearchDialogController(null));
            stage.setTitle("Suche");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onActivity() {
        FXMLLoader loader = new FXMLLoader(LocalStreamClient.class.getResource("activities-gui.fxml"));
        Stage stage = new Stage(StageStyle.DECORATED);
        Scene scene;
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ActivityController controller = loader.getController();
        controller.create(client.getActivities());
        stage.setTitle("Aktivitäten");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);

        stage.show();
    }
}
