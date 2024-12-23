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
    NO_PERMISSION("messages.no_permission");

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
