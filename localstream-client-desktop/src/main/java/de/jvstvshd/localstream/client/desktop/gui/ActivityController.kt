package de.jvstvshd.localstream.client.desktop.gui

import de.jvstvshd.localstream.client.desktop.util.activity.NetworkActivities
import de.jvstvshd.localstream.client.desktop.util.activity.NetworkActivity
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.ScrollPane
import javafx.scene.layout.*
import javafx.scene.paint.Color

class ActivityController {

    @FXML
    lateinit var scrollPane: ScrollPane

    fun create(activities: NetworkActivities) {
        if (activities.activities.isEmpty()) {

            val label = Label("Es wurden noch keine Aktivitäten getätigt.")
            label.background = Background(BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets(0.0, 0.0, 0.0, 0.0)))
            label.autosize()
            val pane = Pane(label)
            pane.background = Background(BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets(0.0, 0.0, 0.0, 0.0)))
            pane.autosize()
            scrollPane.background = Background(BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets(0.0, 0.0, 0.0, 0.0)))
            scrollPane.autosize()
            scrollPane.content = pane
            scrollPane.autosize()
            return
        }
        val list = mutableListOf<Pane>()
        for (activity in activities.activities) {
            println(activity.progress)
            list.add(createPane(activity))
        }
        val gridPane = GridPane()
        for (index in list.indices) {
            gridPane.add(list[index], 0, index)
        }
        scrollPane.content = gridPane
    }

    private fun createPane(activity: NetworkActivity): Pane {
        val pane = GridPane()
        val progressBar = ProgressBar(activity.progress)
        val label = Label("${activity.activityType.name}: ${activity.activityState.name}")
        val description = Label(activity.activiyDescription)
        pane.add(label, 0, 0)
        pane.add(progressBar, 0, 1)
        pane.add(description, 0, 2)
        return pane
    }
}