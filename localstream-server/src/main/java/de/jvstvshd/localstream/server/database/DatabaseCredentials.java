package de.jvstvshd.localstream.server.database;

import com.zaxxer.hikari.HikariConfig;
import de.jvstvshd.localstream.common.scheduling.Concurrency;
import org.mariadb.jdbc.MariaDbDataSource;

import java.util.Properties;

public class DatabaseCredentials {

    private String database;
    private String url;
    private String username;
    private String password;
    private int maxLifetime;
    private int minIdle;
    private int maxPoolSize;
    private long idleTimeout;

    public DatabaseCredentials(String database, String url, String username, String password, int maxLifetime, int minIdle, int maxPoolSize, long idleTimeout) {
        this.database = database;
        this.url = url;
        this.username = username;
        this.password = password;
        this.maxLifetime = maxLifetime;
        this.minIdle = minIdle;
        this.maxPoolSize = maxPoolSize;
        this.idleTimeout = idleTimeout;
    }

    public DatabaseCredentials() {
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxLifetime() {
        return maxLifetime;
    }

    public void setMaxLifetime(int maxLifetime) {
        this.maxLifetime = maxLifetime;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public long getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public HikariConfig apply() {
        Properties properties = new Properties();
        properties.setProperty("dataSourceClassName", MariaDbDataSource.class.getName());
        String[] address = getUrl().split(":");
        if (address.length < 2)
            throw new IllegalArgumentException("JDBC Url must follow the following schema: host:port (by example, 127.0.0.1:3306)");
        String host = address[0];
        String  port = address[1];
        properties.setProperty("dataSource.serverName", host);
        properties.setProperty("dataSource.portNumber", port);
        properties.setProperty("dataSource.databaseName", getDatabase());
        properties.setProperty("dataSource.user", getUsername());
        properties.setProperty("dataSource.password", getPassword());
        HikariConfig config = new HikariConfig(properties);
        config.setMaxLifetime(getMaxLifetime());
        config.setMinimumIdle(getMinIdle());
        config.setMaximumPoolSize(getMaxPoolSize());
        config.setIdleTimeout(getIdleTimeout());
        config.setThreadFactory(Concurrency.FACTORY);
        config.setPoolName("lcs-pool");
        return new HikariConfig(properties);
    }
}
