package net.solostudio.drawlock.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.experimental.UtilityClass;
import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.config.Config;
import net.solostudio.drawlock.enums.keys.ConfigKeys;
import net.solostudio.drawlock.enums.keys.MessageKeys;
import net.solostudio.drawlock.managers.MenuController;
import net.solostudio.drawlock.menu.menus.MenuLogin;
import net.solostudio.drawlock.menu.menus.MenuRegister;
import net.solostudio.drawlock.services.ScoreboardService;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
@UtilityClass
public class DrawLockUtils {
    public void playSound(@NotNull final Player player, @NotNull final String path, @NotNull final String reason) {
        if (!DrawLock.getInstance().getConfiguration().getBoolean(path + ".enabled")) return;

        final var soundNames = DrawLock.getInstance().getConfiguration().getList(path + reason);

        soundNames.stream()
                .map(name -> Sound.valueOf(name.toUpperCase()))
                .forEach(sound -> player.playSound(player, sound, 1.0f, 1.0f));
    }

    public void sendToServer(@NotNull final Player player, @NotNull final String path) {
        final var config = DrawLock.getInstance().getConfiguration();

        if (!config.getBoolean(path + ".enabled")) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Connect");
        out.writeUTF(player.getName());
        out.writeUTF(config.getString(path + ".where"));

        player.sendPluginMessage(DrawLock.getInstance(), "BungeeCord", out.toByteArray());
    }

    public void handleReset(@NotNull final CommandSender sender, @NotNull final String player) {
        ConfigKeys.RESET_COMMANDS_ON_RESET.getList().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player)));
        sender.sendMessage(MessageKeys.SUCCESS_RESET.getMessage());
    }

    public boolean valueOf(@NotNull final String value) {
        return switch (value.toLowerCase()) {
            case "fully" -> true;
            case "not fully" -> false;
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }

    public static void handleLoginOrRegister(@NotNull final Player player) {
        if (DrawLock.getDatabase().isRegistered(player.getName())) {
            new MenuLogin(MenuController.getMenuUtils(player), null).open();
            ScoreboardService.giveScoreboard(player, true);
        } else {
            new MenuRegister(MenuController.getMenuUtils(player)).open();
            ScoreboardService.giveScoreboard(player, false);
        }
    }
}
