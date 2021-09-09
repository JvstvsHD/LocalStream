package de.jvstvshd.localstream.client.desktop.media

abstract class AbstractMediaPlayer(
    private val title: String,
    private val maxPackets: Long,
    private val durationTotal: Long
) : MediaPlayer {
    protected var packets: Long = 0

    override fun getTitle(): String {
        return title
    }

    override fun getMaxPackets(): Long {
        return maxPackets
    }

    override fun getDurationTotal(): Long {
        return durationTotal
    }

    abstract fun playedBytes(): Long
}