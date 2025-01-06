package net.solostudio.drawlock.enums.keys;

import me.clip.placeholderapi.PlaceholderAPI;
import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.processor.MessageProcessor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.List;

import static net.solostudio.drawlock.hooks.PlaceholderAPI.isRegistered;

public enum ConfigKeys {
    DATABASE("database.type"),
    LANGUAGE("language"),

    BASIC_DATE_FORMAT("basic.date-format"),

    BEAUTIFIER_JOIN_MESSAGE("beautifier.join.message"),
    BEAUTIFIER_JOIN_MESSAGE_IS_ENABLED("beautifier.join.enabled"),

    MINIMUM_PASSWORD_LENGTH("password.min-max-length"),

    MAX_ATTEMPTS("password.max-attempts"),
    ATTEMPT_COMMAND("password.attempt-command"),

    MENU_TYPE("password.menu-type"),
    MENU_SIZE("password.menu-size"),

    LOGIN_NAME("login.gui.name"),
    LOGIN_SCOREBOARD_TITLE("beautifier.scoreboards.login.title"),
    LOGIN_SCOREBOARD_LINES("beautifier.scoreboards.login.lines"),

    CHANGE_PASSWORD_NAME("change-password.gui.name"),

    REGISTER_NAME("register.gui.name"),
    REGISTER_SCOREBOARD_TITLE("beautifier.scoreboards.register.title"),
    REGISTER_SCOREBOARD_LINES("beautifier.scoreboards.register.lines"),

    RESET_COMMANDS_ON_RESET("reset.commands-on-reset"),

    TOTP_ENABLED("totp.enabled"),
    TOTP_NAME("totp.name");

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

    public List<String> getScoreboardList(@NotNull Player player) {
        return isRegistered ? MessageProcessor.processList(PlaceholderAPI.setPlaceholders(player, DrawLock.getInstance().getConfiguration().getList(path))) : MessageProcessor.processList(DrawLock.getInstance().getConfiguration().getList(path));
    }
}
