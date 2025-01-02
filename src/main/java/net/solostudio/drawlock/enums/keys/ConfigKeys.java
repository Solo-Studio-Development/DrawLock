package net.solostudio.drawlock.enums.keys;

import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum ConfigKeys {
    DATABASE("database.type"),
    LANGUAGE("language"),

    BASIC_DATE_FORMAT("basic.date-format"),

    ENCRYPTION_SECRET_KEY("encryption.secret-key"),
    ENCRYPTION_IV("encryption.iv"),

    BEAUTIFIER_JOIN_MESSAGE("beautifier.join.message"),
    BEAUTIFIER_JOIN_MESSAGE_IS_ENABLED("beautifier.join.enabled"),

    MINIMUM_PASSWORD_LENGTH("password.min-max-length"),

    MAX_ATTEMPTS("password.max-attempts"),
    ATTEMPT_COMMAND("password.attempt-command"),

    MENU_TYPE("password.menu-type"),
    MENU_SIZE("password.menu-size"),

    LOGIN_NAME("login.gui.name"),

    CHANGE_PASSWORD_NAME("change-password.gui.name"),

    REGISTER_NAME("register.gui.name");

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
