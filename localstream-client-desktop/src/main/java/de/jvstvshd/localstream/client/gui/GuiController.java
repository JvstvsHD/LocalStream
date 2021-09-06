package de.jvstvshd.localstream.client.gui;

import de.jvstvshd.localstream.client.LocalStreamClient;

public class GuiController {

    protected static LocalStreamClient client;

    public static void setClient(LocalStreamClient client) {
        GuiController.client = client;
    }
}