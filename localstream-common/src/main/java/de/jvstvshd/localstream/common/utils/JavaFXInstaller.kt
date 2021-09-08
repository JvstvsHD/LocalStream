package de.jvstvshd.localstream.common.utils

import net.lingala.zip4j.ZipFile
import org.apache.commons.lang3.SystemUtils
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels


class JavaFXInstaller {

    private val destination = if (System.getProperty("ls.jfx.location") == null) File("libs/javafx-sdk.zip") else File(
        System.getProperty("ls.jfx.location")
    )
    private val forceInstall = System.getProperty("ls.jfx.force-install").toBoolean()

    private fun installed(): Boolean = destination.exists()

    private fun download(): File {
        val channel = Channels.newChannel(constructPath().openStream())

        destination.parentFile.mkdirs()
        val stream = FileOutputStream(destination)
        stream.channel.transferFrom(channel, 0, Long.MAX_VALUE)
        return destination
    }

    fun installJavaFX() {
        if (!forceInstall && installed())
            return
        val file = download()
        println(file.absolutePath)
        val zipFile = ZipFile(file)
        if (zipFile.isEncrypted) {
            throw UnsupportedOperationException("Zip file is encrypted.")
        }
        val destination = File("libs/javafx-sdk-zip/")

        zipFile.extractAll(destination.absolutePath)
        return
    }

    /**
     * Constructs the url for the matching openjfx SDK 17.
     * @return An URL leading to the required openjfx SDK.
     * @throws UnsupportedOperationException if the os cannot be identified as windows, linux or macOS.
     */
    fun constructPath(): URL = URL(
        StringBuilder("https://download2.gluonhq.com/openjfx/17/openjfx-17_").append(os().lowercase()).append("-")
            .append(arch()).append("_bin-sdk.zip").trim().toString()
    )

    /**
     * @return the os as "windows", "linux" or "macOS" based on system properties.
     * @throws UnsupportedOperationException if the os cannot be identified as windows, linux or macOS.
     */
    private fun os(): String {
        if (!SystemUtils.IS_OS_WINDOWS && !SystemUtils.IS_OS_LINUX && !SystemUtils.IS_OS_MAC_OSX) {
            throw UnsupportedOperationException("Cannot identify operating system ${SystemUtils.OS_NAME} as windows, linux or OSX.")
        }
        return if (SystemUtils.IS_OS_WINDOWS) "windows" else if (SystemUtils.IS_OS_MAC_OSX) "osx" else "linux"
    }

    /**
     * @return the architecture of this system's processor.
     * @throws UnsupportedOperationException if the architecture is unsupported (not amd64 or x86).
     */
    private fun arch(): String {
        return when (val archProperty = SystemUtils.OS_ARCH.lowercase()) {
            "amd64" -> "x64"
            "x86" -> "x86"
            else -> throw UnsupportedOperationException("Unsupported system architecture: $archProperty")
        }
    }
}