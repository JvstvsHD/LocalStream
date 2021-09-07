package de.jvstvshd.localstream.client.desktop.gui;

import de.jvstvshd.localstream.client.desktop.LocalStreamClient;

public class GuiController {

    protected static LocalStreamClient client;

    public static void setClient(LocalStreamClient client) {
        GuiController.client = client;
    }
}