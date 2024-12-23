package net.solostudio.drawlock.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import net.solostudio.drawlock.utils.AES256Utils;
import net.solostudio.drawlock.utils.LoggerUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@Getter
public class MySQL extends AbstractDatabase {
    private final Connection connection;

    public MySQL(@NotNull ConfigurationSection section) throws SQLException {
        HikariConfig hikariConfig = new HikariConfig();

        String host = section.getString("host");
        String database = section.getString("database");
        String user = section.getString("username");
        String pass = section.getString("password");
        int port = section.getInt("port");
        boolean ssl = section.getBoolean("ssl");
        boolean certificateVerification = section.getBoolean("certificateverification");
        int poolSize = section.getInt("poolsize");
        int maxLifetime = section.getInt("lifetime");

        hikariConfig.setPoolName("DrawLockPool");
        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setMaxLifetime(maxLifetime * 1000L);
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(pass);
        hikariConfig.addDataSourceProperty("useSSL", String.valueOf(ssl));
        if (!certificateVerification)
            hikariConfig.addDataSourceProperty("verifyServerCertificate", String.valueOf(false));
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("encoding", "UTF-8");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("jdbcCompliantTruncation", "false");
        hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "275");
        hikariConfig.addDataSourceProperty("useUnivaultcher", "true");
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        connection = dataSource.getConnection();
    }

    @Override
    public boolean isConnected() {
        return connection != null;
    }

    @Override
    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException exception) {
                LoggerUtils.error(exception.getMessage());
            }
        }
    }

    @Override
    public void createTable() {
        String drawLockTableQuery = "CREATE TABLE IF NOT EXISTS drawlock (PLAYER VARCHAR(255) NOT NULL, PASSWORD VARCHAR(65535) NOT NULL, CREATED_AT VARCHAR(255), LAST_LOGIN VARCHAR(255), PRIMARY KEY (PLAYER))";

        try (PreparedStatement drawLockTableStatement = getConnection().prepareStatement(drawLockTableQuery)) {
            drawLockTableStatement.execute();
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public void createPlayer(@NotNull String playerName) {
        String query = "INSERT IGNORE INTO drawlock (PLAYER) VALUES (?)";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, playerName);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public void savePasswordToDatabase(@NotNull String playerName, @NotNull String password) {
        String query = "UPDATE drawlock SET PASSWORD = ? WHERE PLAYER = ?";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, password);
            preparedStatement.setString(2, playerName);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public String getPassword(@NotNull String playerName) {
        String password = null;

        String query = "SELECT PASSWORD FROM drawlock WHERE PLAYER = ?";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, playerName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) password = resultSet.getString("PASSWORD");
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        return password != null ? AES256Utils.decrypt(password) : null;
    }
}
