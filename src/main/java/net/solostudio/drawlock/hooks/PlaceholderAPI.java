package net.solostudio.drawlock.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.database.AbstractDatabase;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class PlaceholderAPI extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "dl";
    }

    @Override
    public @NotNull String getAuthor() {
        return "User-19fff";
    }

    @Override
    public @NotNull String getVersion() {
        return DrawLock.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(@NotNull Player player, @NotNull String params) {
        AbstractDatabase database = DrawLock.getDatabase();

        return switch (params) {
          case "last_login" -> database.getLastLogin(player.getName());
          case "created_at" -> database.getCreation(player.getName());

          default -> "";
        };
    }

    public static void registerHook() {
        new PlaceholderAPI().register();
    }
}
