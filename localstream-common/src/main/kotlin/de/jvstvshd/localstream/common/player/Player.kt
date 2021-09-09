package de.jvstvshd.localstream.common.player

import java.io.IOException
import kotlin.jvm.Throws

interface Player<T> {

    fun getTitle(): String
    @Throws(IOException::class)
    fun play()

    @Throws(IOException::class)
    fun queue(t: T)

    @Throws(IOException::class)
    fun resume()

    @Throws(IOException::class)
    fun pause()

    @Throws(IOException::class)
    fun stop()

    fun isPausing(): Boolean
}