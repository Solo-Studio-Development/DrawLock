package net.solostudio.drawlock.commands;

import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.enums.keys.MessageKeys;
import org.bukkit.command.CommandSender;
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

    @Subcommand("help")
    @Description("Reloads the plugin")
    @CommandPermission("drawlock.reload")
    public void reload(@NotNull CommandSender sender) {
        DrawLock.getInstance().getLanguage().reload();
        DrawLock.getInstance().getConfiguration().reload();
        sender.sendMessage(MessageKeys.RELOAD.getMessage());
    }

    @Subcommand("changepassword")
    @Description("Changes the password")
    @CommandPermission("drawlock.changepassword")
    public void changePassword(@NotNull CommandSender sender) {
        //more here
    }
}
