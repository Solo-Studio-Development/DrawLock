package net.solostudio.drawlock.database;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractDatabase {
    public abstract boolean isConnected();

    public abstract void disconnect();

    public abstract void createTable();

    public abstract void createPlayer(@NotNull String playerName);

    public abstract void savePasswordToDatabase(@NotNull String playerName, @NotNull String password);

    public abstract String getPassword(@NotNull String playerName);
}
