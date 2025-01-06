package net.solostudio.drawlock.interfaces;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.List;

public interface DrawLockDatabase {
    boolean isConnected();

    void disconnect();

    Connection getConnection();

    void createTable();

    void createPlayer(@NotNull String playerName);

    boolean exists(@NotNull String playerName);

    boolean isRegistered(@NotNull String playerName);

    void savePasswordToDatabase(@NotNull String playerName, @NotNull String password);

    void resetFully(@NotNull String playerName);

    List<String> getEveryPlayerInDatabase();

    void resetWithoutDates(@NotNull String playerName);

    String getPassword(@NotNull String playerName);

    void saveDate(@NotNull String playerName, @NotNull String column);

    String getLastLogin(@NotNull String playerName);

    String getCreation(@NotNull String playerName);
}
