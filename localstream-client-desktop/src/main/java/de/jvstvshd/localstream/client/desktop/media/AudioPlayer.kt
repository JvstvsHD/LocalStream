package de.jvstvshd.localstream.client.desktop.media

import de.jvstvshd.localstream.client.desktop.LocalStreamClient
import de.jvstvshd.localstream.client.desktop.gui.PlayerGuiController
import de.jvstvshd.localstream.client.desktop.util.Alerts
import de.jvstvshd.localstream.common.title.TitleMetadata
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.stage.Stage
import java.io.IOException
import java.util.concurrent.locks.ReentrantLock
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.LineEvent
import javax.sound.sampled.SourceDataLine


class AudioPlayer(
    format: AudioFormat,
    title: String,
    maxPackets: Long,
    private val mediaSystem: MediaSystem,
    val metadata: TitleMetadata
) : AbstractMediaPlayer(title, maxPackets, metadata.length) {
    private var allowQueuing = false
    private var pausing = false
    private val lock: ReentrantLock = ReentrantLock()
    private var associatedController: PlayerGuiController? = null
    private val dataLine: SourceDataLine
    private val format: AudioFormat
    private var bytesQueued = 0L

    @Synchronized
    @Throws(IOException::class)
    override fun play() {
        try {
            dataLine.open(format)
            dataLine.start()
            allowQueuing = true
            createPlayerGui()
            dataLine.addLineListener { event ->
                println("d")
                if (event.type != LineEvent.Type.STOP) return@addLineListener
                mediaSystem.playFurther(this);
            }
        } catch (e: Exception) {
            throw IOException(e)
        }
    }

    @Synchronized
    @Throws(IOException::class)
    override fun queue(t: ByteArray) {
        if (!allowQueuing) return
        bytesQueued += dataLine.write(t, 0, t.size)
        packets++

        val progress = dataLine.microsecondPosition / 1000.0 / (getDurationTotal() * 1000.0)
        //println(playedBytes())
        Platform.runLater {
            associatedController!!.progressBar.progress = progress
            associatedController!!.setTitleName(
                formatTime(dataLine.microsecondPosition / (1000 * 1000) * 1000) + "/" + formatTime(
                    getDurationTotal() * 1000
                )
            )
        }
    }

    private fun formatTime(diff: Long): String {
        var difference = diff
        val days: Long
        var hours: Long = 0
        var minutes: Long = 0
        var seconds: Long = 0
        val day = (1000 * 60 * 60 * 24).toLong()
        if (difference - day > 0) {
            days = (difference - difference % day) / day
            difference -= days * day
        }
        val hour = (1000 * 60 * 60).toLong()
        if (difference - hour > 0) {
            hours = (difference - difference % hour) / hour
            difference -= hours * hour
        }
        val minute = (1000 * 60).toLong()
        if (difference - minute > 0) {
            minutes = (difference - difference % minute) / minute
            difference -= minutes * minute
        }
        val second: Long = 1000
        if (difference - second > 0) {
            seconds = (difference - difference % second) / second
        }
        return formatNumber(hours) + ":" + formatNumber(minutes) + ":" + formatNumber(seconds)
    }

    private fun formatNumber(number: Long): String {
        val n = number.toString()
        return if (n.length > 2) n else build(2 - n.length) + n
    }

    private fun build(l: Int): String {
        return "0".repeat(0.coerceAtLeast(l))
    }

    private fun createPlayerGui() {
        Platform.runLater {
            try {
                val loader = FXMLLoader(LocalStreamClient::class.java.getResource("player-gui.fxml"))
                val root = loader.load<Parent>()
                val stage = Stage()
                val scene = Scene(root)
                stage.scene = scene
                stage.title = getTitle()
                stage.onCloseRequest = EventHandler {
                    try {
                        stop()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                associatedController = loader.getController()
                if (associatedController == null) {
                    Alerts.showAlert(
                        Alert.AlertType.ERROR,
                        "Die GUI des Players konnte nicht erstellt werden.",
                        "Fehler beim Abspielen von ${getTitle()}",
                        "Fehler!"
                    )
                    return@runLater
                }
                associatedController!!.setTitleName(getTitle())
                associatedController!!.setPlayer(this)
                associatedController!!.stage = stage
                stage.show()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }

    @Throws(IOException::class)
    override fun stop() {
        lock.lock()
        mediaSystem.shutdownPlayer(this)
        allowQueuing = false
        dataLine.drain()
        println("Drained data")
        dataLine.stop()
        dataLine.close()
        lock.unlock()
    }

    @Throws(IOException::class)
    override fun pause() {
        lock.lock()
        mediaSystem.pausePlayer(this)
        pausing = true
        dataLine.stop()
        lock.unlock()
    }

    @Throws(IOException::class)
    override fun resume() {
        lock.lock()
        mediaSystem.resumePlayer(this)
        dataLine.start()
        pausing = false
        allowQueuing = true
        lock.unlock()
    }

    override fun isPausing(): Boolean {
        return pausing
    }

    init {
        this.format = format
        dataLine = AudioSystem.getSourceDataLine(null)
    }

    override fun playedBytes(): Long {
        //println("$bytesQueued | ${dataLine.buff}")
        return bytesQueued - dataLine.bufferSize
    }
}