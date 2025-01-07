package net.solostudio.drawlock.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.enums.keys.ConfigKeys;
import net.solostudio.drawlock.interfaces.DrawLockDatabase;
import net.solostudio.drawlock.utils.LoggerUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
public class H2 implements DrawLockDatabase {
    private final Connection connection;
    private final HikariDataSource dataSource;

    public H2() throws SQLException, ClassNotFoundException {
        Class.forName("net.solostudio.drawlock.libs.h2.Driver");

        HikariConfig hikariConfig = new HikariConfig();

        String url = "jdbc:h2:" + DrawLock.getInstance().getDataFolder().getAbsolutePath() + "/database;MODE=MySQL";

        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername("sa");
        hikariConfig.setPassword("");
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setPoolName("DrawLockPool");

        dataSource = new HikariDataSource(hikariConfig);
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
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void createTable() {
        final var drawLockTableQuery = "CREATE TABLE IF NOT EXISTS drawlock (PLAYER VARCHAR(255) NOT NULL, PASSWORD VARCHAR(65535) NOT NULL DEFAULT '', CREATED_AT VARCHAR(255), LAST_LOGIN VARCHAR(255), PRIMARY KEY (PLAYER))";
        final var totpTableQuery = "CREATE TABLE IF NOT EXISTS totp (USERNAME VARCHAR(255) NOT NULL, SECRET VARCHAR(255) NOT NULL, SCRATCH_CODES TEXT, PRIMARY KEY (USERNAME))";

        try (PreparedStatement drawLockTableStatement = getConnection().prepareStatement(drawLockTableQuery)) {
            drawLockTableStatement.execute();
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        try (PreparedStatement totpTableStatement = getConnection().prepareStatement(totpTableQuery)) {
            totpTableStatement.execute();
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public void createPlayer(@NotNull final String playerName) {
        final var query = "INSERT IGNORE INTO drawlock (PLAYER, PASSWORD) VALUES (?, '')";

        try {
            if (!exists(playerName)) {
                try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                    preparedStatement.setString(1, playerName);
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public boolean exists(@NotNull final String playerName) {
        final var query = "SELECT * FROM drawlock WHERE PLAYER = ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, playerName);

                return preparedStatement.executeQuery().next();
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        return false;
    }

    @Override
    public boolean isRegistered(@NotNull final String playerName) {
        final var query = "SELECT PASSWORD FROM drawlock WHERE PLAYER = ?";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, playerName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String password = resultSet.getString("PASSWORD");
                return password != null && !password.isEmpty();
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
        return false;
    }

    @Override
    public void savePasswordToDatabase(@NotNull final String playerName, @NotNull final String password) {
        final var query = "UPDATE drawlock SET PASSWORD = ? WHERE PLAYER = ?";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, password);
            preparedStatement.setString(2, playerName);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public void resetFully(@NotNull final String playerName) {
        final var query = "UPDATE drawlock SET PASSWORD = '', CREATED_AT = '', LAST_LOGIN = '' WHERE PLAYER = ?";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, playerName);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public List<String> getEveryPlayerInDatabase() {
        final var players = new ArrayList<String>();
        final var query = "SELECT PLAYER FROM drawlock";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) players.add(resultSet.getString("PLAYER"));
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        return players;
    }

    @Override
    public void resetWithoutDates(@NotNull final String playerName) {
        final var query = "UPDATE drawlock SET PASSWORD = '' WHERE PLAYER = ?";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, playerName);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public String getPassword(@NotNull final String playerName) {
        String hashedPassword = null;

        var query = "SELECT PASSWORD FROM drawlock WHERE PLAYER = ?";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, playerName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) hashedPassword = resultSet.getString("PASSWORD");
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        return hashedPassword;
    }

    @Override
    public void saveDate(@NotNull final String playerName, @NotNull final String column) {
        if (!List.of("CREATED_AT", "LAST_LOGIN").contains(column)) throw new IllegalArgumentException("Invalid column name");

        final var query = "UPDATE drawlock SET " + column + " = ? WHERE PLAYER = ?";
        final var currentDateTime = new SimpleDateFormat(ConfigKeys.BASIC_DATE_FORMAT.getString()).format(new Date());

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, currentDateTime);
            preparedStatement.setString(2, playerName);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public String getLastLogin(@NotNull final String playerName) {
        final var query = "SELECT LAST_LOGIN FROM drawlock WHERE PLAYER = ?";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, playerName);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) return resultSet.getString("LAST_LOGIN");
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        return "";
    }

    @Override
    public String getCreation(@NotNull final String playerName) {
        final var query = "SELECT CREATED_AT FROM drawlock WHERE PLAYER = ?";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, playerName);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) return resultSet.getString("CREATED_AT");
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        return "";
    }
}