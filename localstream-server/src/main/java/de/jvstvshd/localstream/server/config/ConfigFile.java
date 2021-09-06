package de.jvstvshd.localstream.server.config;

import de.jvstvshd.localstream.server.database.DatabaseCredentials;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})

public class ConfigFile {

    private DatabaseCredentials databaseCredentials = new DatabaseCredentials();

    public DatabaseCredentials getDatabaseCredentials() {
        System.out.println("databaseCredentials = " + databaseCredentials);
        return databaseCredentials;
    }
}
