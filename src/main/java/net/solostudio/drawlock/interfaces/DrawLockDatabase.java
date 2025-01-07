package net.solostudio.drawlock.interfaces;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.List;

public interface DrawLockDatabase {
    boolean isConnected();

    void disconnect();

    Connection getConnection();

    void createTable();

    void createPlayer(@NotNull final String playerName);

    boolean exists(@NotNull final String playerName);

    boolean isRegistered(@NotNull final String playerName);

    void savePasswordToDatabase(@NotNull final String playerName, @NotNull final String password);

    void resetFully(@NotNull final String playerName);

    List<String> getEveryPlayerInDatabase();

    void resetWithoutDates(@NotNull final String playerName);

    String getPassword(@NotNull final String playerName);

    void saveDate(@NotNull final String playerName, @NotNull final String column);

    String getLastLogin(@NotNull final String playerName);

    String getCreation(@NotNull final String playerName);
}
