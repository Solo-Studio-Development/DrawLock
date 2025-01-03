package net.solostudio.drawlock.database;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractDatabase {
    public abstract boolean isConnected();

    public abstract void disconnect();

    public abstract void createTable();

    public abstract void createPlayer(@NotNull String playerName);

    public abstract boolean exists(@NotNull String playerName);

    public abstract boolean isRegistered(@NotNull String playerName);

    public abstract void savePasswordToDatabase(@NotNull String playerName, @NotNull String password);

    public abstract void resetFully(@NotNull String playerName);

    public abstract List<String> getEveryPlayerInDatabase();

    public abstract void resetWithoutDates(@NotNull String playerName);

    public abstract String getPassword(@NotNull String playerName);

    public abstract void saveDate(@NotNull String playerName, @NotNull String column);

    public abstract String getLastLogin(@NotNull String playerName);

    public abstract String getCreation(@NotNull String playerName);
}
