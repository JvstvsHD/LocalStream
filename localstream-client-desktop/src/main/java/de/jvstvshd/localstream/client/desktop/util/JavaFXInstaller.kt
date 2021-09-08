package de.jvstvshd.localstream.client.desktop.util

import net.lingala.zip4j.ZipFile
import org.apache.commons.lang3.SystemUtils
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.net.URLClassLoader
import java.nio.channels.Channels
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Deprecated(message = "installer is useless since a fat jar does not need an extra jfx sdk")
/**
 * The Class JavaFXInstaller is used to download [download] and setup [setUpJfx] JavaFX for an installation.
 * The installation process downloads the JavaFX SDK and loads into the classpath.
 * @see installJavaFX
 */
class JavaFXInstaller {

    private val destination = if (System.getProperty("ls.jfx.location") == null) File("libs/javafx-sdk.zip") else File(
        System.getProperty("ls.jfx.location")
    )
    private val forceInstall = System.getProperty("ls.jfx.force-install").toBoolean()

    private fun installed(): Boolean = destination.exists()

    /**
     * This method downloads the JavaFX SDK as zip into the libs directory.
     * @see java.nio.channels.FileChannel.transferFrom
     * @return the downloaded file
     *
     */
    private fun download(): File {
        val channel = Channels.newChannel(constructPath().openStream())
        destination.parentFile.mkdirs()
        val stream = FileOutputStream(destination)
        stream.channel.transferFrom(channel, 0, Long.MAX_VALUE)
        return destination
    }

    /**
     * Extracts the previous downloaded zip directory.
     * @see download
     * @see setUpJfx
     * @throws UnsupportedOperationException if the zip file is encrypted.
     */
    fun installJavaFX() {
        if (installed() && !forceInstall)
            return
        val file = download()
        println(file.absolutePath)
        val zipFile = ZipFile(file)
        if (zipFile.isEncrypted) {
            throw UnsupportedOperationException("Zip file is encrypted.")
        }
        val destination = File("libs/javafx-sdk-zip/")

        zipFile.extractAll(destination.absolutePath)
        setUpJfx()
        return
    }

    /**
     * Sets the module path to the sdk and loads all it's necessary modules.
     */
    fun setUpJfx() {
        copySdkFiles()
        if (System.getProperty("jdk.module.path") == null) {
            System.setProperty("jdk.module.path", Paths.get("libs/javafx-sdk/lib").toAbsolutePath().toString())
        }
        println(System.getProperty("jdk.module.path"))
        loadModules()
        println(System.getProperty("java.lang.classpath"))
    }

    /**
     * Copies all JavaFX SDK files into an other directory without an extra directory and without any version numbers ini it.
     * @return the [Path] to the copied SDK.
     */
    private fun copySdkFiles() {
        val sourceDirectory = File("libs/javafx-sdk-zip")
        val finalDestination = Paths.get("libs/javafx-sdk")
        for (path in Files.list(sourceDirectory.toPath())) {
            if (path.fileName.toString().startsWith("javafx-sdk-")) {
                if (Files.exists(finalDestination))
                    break
                Files.createDirectories(finalDestination)
                for (content in Files.walk(path)) {
                    if (content.fileName.toString() == path.fileName.toString()) {
                        continue
                    }
                    val fileName = content.toAbsolutePath().toString().substringAfter(path.fileName.toString())
                    val destination =
                        Paths.get(File(finalDestination.toAbsolutePath().toString() + "\\" + fileName).toURI())
                    if (Files.isDirectory(content.parent)) {
                        Files.createDirectories(content.parent)
                    }
                    try {
                        Files.copy(content, destination)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    /**
     * Loads the jar with the given path into the JVM.
     */
    private fun loadModule(path: Path) {
        val method =
            URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
        method.isAccessible = true
        method.invoke(ClassLoader.getSystemClassLoader(), path.toUri())
    }

    /**
     * Loads all necessary modules (javafx.controls and javafx.fxml).
     * @see loadModule
     */
    private fun loadModules() {
        arrayOf("javafx.controls", "javafx.fxml").forEach { path -> loadModule(Paths.get("libs/javafx-sdk/lib/$path")) }
    }

    /**
     * Constructs the url for the matching openjfx SDK 17.
     * @return An URL leading to the required openjfx SDK.
     * @throws UnsupportedOperationException if the os cannot be identified as windows, linux or macOS.
     */
    private fun constructPath(): URL = URL(
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