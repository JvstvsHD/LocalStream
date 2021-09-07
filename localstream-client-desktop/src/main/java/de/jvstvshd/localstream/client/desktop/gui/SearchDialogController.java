package de.jvstvshd.localstream.client.desktop.gui;

import de.jvstvshd.localstream.client.desktop.network.ClientPacketHandler;
import de.jvstvshd.localstream.network.NetworkManager;
import de.jvstvshd.localstream.network.packets.Packet;
import de.jvstvshd.localstream.network.packets.PacketPriority;
import de.jvstvshd.localstream.network.packets.SearchRequestPacket;
import de.jvstvshd.localstream.network.packets.TitlePacket;
import de.jvstvshd.localstream.title.TitleMetadata;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

public class SearchDialogController extends GuiController {

    private final NetworkManager manager;
    private Map<UUID, String> currentSuggestions;

    public SearchDialogController() {
        this.manager = client.getNettyClient().getManager();
        ((ClientPacketHandler) Packet.defaultHandler).setSearchDialogController(this);

    }

    @FXML
    private TextField searchField;
    @FXML
    ScrollPane scrollPane;

    private String lastQuery;

    @FXML
    public void onSearch() {
        String search = lastQuery = searchField.getText();
        manager.sendPacket(new SearchRequestPacket(PacketPriority.HIGH, search));
        Platform.runLater(() -> scrollPane.setContent(new Label("Suchanfrage wird bearbeitet...")));
    }

    public void showSearchSuggestions(Map<UUID, String> suggestions) {
        currentSuggestions = suggestions;
        Platform.runLater(() -> {
            GridPane info = new GridPane();
            Label infoText = new Label("Die Suche nach " + lastQuery + " ergab " + suggestions.size() + " Treffer.");
            info.add(infoText, 0, 0);
            int row = 1;
            GridPane pane = new GridPane();

            pane.setVgap(20.0);
            pane.setMinHeight(scrollPane.getMinHeight());
            pane.add(info, 0, 0);
            for (Object o : suggestions.values().stream().sorted(Comparator.naturalOrder()).toArray()) {
                pane.add(createPane((String) o), 0, row);
                pane.autosize();
                row++;
            }
            pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, new Insets(0.0, 0.0, 0.0, 0.0))));
            scrollPane.setContent(pane);
            scrollPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, null)));
            scrollPane.autosize();
        });

    }

    private Pane createPane(String suggestion) {
        GridPane pane = new GridPane();
        pane.setMinHeight(20.0);
        pane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        Label label = new Label(suggestion);
        label.autosize();
        pane.add(label, 0, 0);
        pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, new Insets(0.0, 0.0, 0.0, 0.0))));
        pane.autosize();
        pane.cursorProperty().set(Cursor.HAND);
        pane.setOnMouseClicked(event -> {
            UUID titleID = null;
            for (Map.Entry<UUID, String> entry : currentSuggestions.entrySet()) {
                if (entry.getValue().equalsIgnoreCase(label.getText()))
                    titleID = entry.getKey();
            }
            if (titleID == null)
                return;
            manager.sendPacket(new TitlePacket(PacketPriority.HIGHEST, TitlePacket.TitleAction.PLAY, TitleMetadata.builder().setUuid(titleID).build(), UUID.randomUUID()));

        });
        return pane;
    }
}
