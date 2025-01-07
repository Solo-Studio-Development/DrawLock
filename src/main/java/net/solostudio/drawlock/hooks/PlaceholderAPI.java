package net.solostudio.drawlock.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.solostudio.drawlock.DrawLock;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class PlaceholderAPI {
    public static boolean isRegistered = false;

    public static void registerHook() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderIntegration().register();
            isRegistered = true;
        }
    }

    private static class PlaceholderIntegration extends PlaceholderExpansion {

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
        public String onPlaceholderRequest(@NotNull final Player player, @NotNull final String params) {
            final var database = DrawLock.getDatabase();

            return switch (params) {
                case "last_login" -> database.getLastLogin(player.getName());
                case "created_at" -> database.getCreation(player.getName());
                default -> "";
            };
        }
    }
}
