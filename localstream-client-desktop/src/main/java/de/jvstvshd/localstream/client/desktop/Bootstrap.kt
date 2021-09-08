package de.jvstvshd.localstream.client.desktop

import de.jvstvshd.localstream.client.desktop.util.JavaFXInstaller


fun main(args: Array<String>) {
    val jfxInstaller = JavaFXInstaller()
    //jfxInstaller.installJavaFX()
    LocalStreamClient.main(args)
}
