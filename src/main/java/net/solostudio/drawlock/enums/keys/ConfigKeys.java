package net.solostudio.drawlock.enums.keys;

import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum ConfigKeys {
    DATABASE("database.type"),
    LANGUAGE("language");

    private final String path;

    ConfigKeys(@NotNull final String path) {
        this.path = path;
    }

    public String getString() {
        return MessageProcessor.process(DrawLock.getInstance().getConfiguration().getString(path));
    }

    public boolean getBoolean() {
        return DrawLock.getInstance().getConfiguration().getBoolean(path);
    }

    public int getInt() {
        return DrawLock.getInstance().getConfiguration().getInt(path);
    }

    public List<String> getList() {
        return DrawLock.getInstance().getConfiguration().getList(path);
    }
}
