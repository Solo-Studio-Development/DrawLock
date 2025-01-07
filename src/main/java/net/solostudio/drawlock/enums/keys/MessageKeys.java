package net.solostudio.drawlock.enums.keys;

import lombok.Getter;
import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public enum MessageKeys {
    RELOAD("messages.reload"),
    HELP("messages.help"),
    NO_PERMISSION("messages.no_permission"),

    SUCCESS_LOGIN("messages.success-login"),
    SUCCESS_REGISTER("messages.success-register"),
    SUCCESS_CHANGE_PASSWORD("messages.success-change-password"),
    SUCCESS_RESET("messages.success-reset"),

    WRONG_PASSWORD("messages.wrong-password"),

    TOTP_WELCOME("messages.totp-welcome"),
    TOTP_SUCCESS("messages.totp-success"),
    TOTP_WRONG("messages.wrong-totp"),
    TOTP_ALREADY_REGISTERED("messages.totp-already-registered");

    private final String path;

    MessageKeys(@NotNull final String path) {
        this.path = path;
    }

    public String getMessage() {
        return MessageProcessor.process(DrawLock.getInstance().getLanguage().getString(path));
    }

    public List<String> getMessages() {
        return DrawLock.getInstance().getLanguage().getList(path)
                .stream()
                .map(MessageProcessor::process)
                .toList();
    }
}
