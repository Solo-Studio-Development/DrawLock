package net.solostudio.drawlock.commands;

import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.annotations.FullyOrNot;
import net.solostudio.drawlock.annotations.PlayersFromDatabase;
import net.solostudio.drawlock.enums.keys.ConfigKeys;
import net.solostudio.drawlock.enums.keys.MessageKeys;
import net.solostudio.drawlock.managers.MenuController;
import net.solostudio.drawlock.menu.menus.MenuChangePassword;
import net.solostudio.drawlock.menu.menus.MenuLogin;
import net.solostudio.drawlock.menu.menus.MenuRegister;
import net.solostudio.drawlock.utils.DrawLockUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"drawlock", "password"})
public class CommandDrawLock {
    @CommandPlaceholder
    public void defaultCommand(@NotNull CommandSender sender) {
        MessageKeys.HELP
                .getMessages()
                .forEach(sender::sendMessage);
    }

    @Subcommand("reload")
    @Description("Reloads the plugin.")
    @CommandPermission("drawlock.reload")
    public void reload(@NotNull CommandSender sender) {
        DrawLock.getInstance().getLanguage().reload();
        DrawLock.getInstance().getConfiguration().reload();
        sender.sendMessage(MessageKeys.RELOAD.getMessage());
    }

    @Subcommand("changepassword")
    @Description("Changes the password.")
    @CommandPermission("drawlock.changepassword")
    public void changePassword(@NotNull Player player) {
        MenuController menuController = MenuController.getMenuUtils(player);

        MenuLogin menuLogin = new MenuLogin(menuController, () -> {
            new MenuChangePassword(menuController).open();
        });

        menuLogin.open();
    }

    @Subcommand("reset")
    @Description("Resets the player.")
    @CommandPermission("drawlock.reset")
    public void reset(@NotNull CommandSender sender, @PlayersFromDatabase @NotNull String player, @FullyOrNot @NotNull String full) {
        if (DrawLockUtils.valueOf(full)) {
            DrawLock.getDatabase().resetFully(player);
            DrawLockUtils.handleReset(sender, player);
        } else {
            DrawLock.getDatabase().resetWithoutDates(player);
            DrawLockUtils.handleReset(sender, player);
        }
    }
}
