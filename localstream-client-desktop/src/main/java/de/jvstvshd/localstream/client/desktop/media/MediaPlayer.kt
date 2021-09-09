package de.jvstvshd.localstream.client.desktop.media

import de.jvstvshd.localstream.common.player.Player

interface MediaPlayer : Player<ByteArray> {

    fun getMaxPackets(): Long

    fun getDurationTotal(): Long
}