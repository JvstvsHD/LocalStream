package de.jvstvshd.localstream.server.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataSourceManager {
    private HikariDataSource dataSource;

    public void init(DatabaseCredentials credentials) {
        HikariConfig config = credentials.apply();

        this.dataSource = new HikariDataSource(config);
        try {
            prepare();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void shutdown() {
        dataSource.close();
    }

    private void prepare() throws SQLException {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS titles(" +
                "name VARCHAR (2048)," +
                "id VARCHAR(64)," +
                "length DOUBLE(16, 4)," +
                "size BIGINT(128)," +
                "interpret VARCHAR (2048)," +
                "title_name VARCHAR (2048))")) {

            statement.executeUpdate();
        }
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }
}
