package net.solostudio.drawlock.processor;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
@UtilityClass
public class MessageProcessor {
    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F\\d]{6}");

    public @NotNull String process(@Nullable String message) {
        if (message == null) return "";

        final var matcher = HEX_PATTERN.matcher(message);
        final var result = new StringBuilder();

        while (matcher.find()) {
            final var hexCode = matcher.group();
            final var builder = new StringBuilder();

            for (char c : hexCode.substring(1).toCharArray()) {
                builder.append("&").append(c);
            }

            matcher.appendReplacement(result, builder.toString());
        }

        matcher.appendTail(result);

        return ChatColor.translateAlternateColorCodes('&', result.toString());
    }

    public @NotNull List<String> processList(@Nullable List<String> messages) {
        if (messages == null) return new ArrayList<>();

        final var processedMessages = new ArrayList<String>();

        messages.forEach(message -> processedMessages.add(process(message)));
        return processedMessages;
    }
}
