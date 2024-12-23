package net.solostudio.drawlock.enums.keys;

import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum ConfigKeys {
    DATABASE("database.type"),
    LANGUAGE("language"),

    ENCRYPTION_SECRET_KEY("encryption.secret-key"),
    ENCRYPTION_SALT("encryption.salt"),
    ENCRYPTION_IV("encryption.iv"),

    BEAUTIFIER_JOIN_MESSAGE("beautifier.join.message"),
    BEAUTIFIER_JOIN_MESSAGE_IS_ENABLED("beautifier.join.enabled"),

    MINIMUM_PASSWORD_LENGTH("password.min-length"),

    LOGIN_NAME("login.gui.name"),
    LOGIN_SIZE("login.gui.size"),
    LOGIN_TYPE("login.gui.type"),
    LOGIN_SOUNDS_ENABLED("login.sounds.enabled"),
    LOGIN_SOUNDS("login.sounds.name"),

    REGISTER_NAME("register.gui.name"),
    REGISTER_SIZE("register.gui.size"),
    REGISTER_TYPE("register.gui.type"),
    REGISTER_SOUNDS_ENABLED("register.sounds.enabled"),
    REGISTER_SOUNDS("register.sounds.name");

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
