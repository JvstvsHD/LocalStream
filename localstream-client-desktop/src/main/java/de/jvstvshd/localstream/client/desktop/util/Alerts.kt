package de.jvstvshd.localstream.client.desktop.util

import javafx.scene.control.Alert

class Alerts {

    companion object {
        fun showAlert(
            alertType: Alert.AlertType = Alert.AlertType.INFORMATION,
            contentText: String = "",
            headerText: String = "",
            title: String = ""
        ) = createAlert(alertType, contentText, headerText, title).show()

        fun createAlert(
            alertType: Alert.AlertType = Alert.AlertType.INFORMATION,
            contentText: String = "",
            headerText: String = "",
            title: String = ""
        ): Alert {

            val alert = Alert(alertType)
            alert.contentText = contentText
            alert.headerText = headerText
            alert.title = title
            return alert
        }
    }
}