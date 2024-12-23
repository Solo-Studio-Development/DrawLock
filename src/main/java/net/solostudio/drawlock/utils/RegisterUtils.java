package net.solostudio.drawlock.utils;

import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.commands.CommandDrawLock;
import net.solostudio.drawlock.exception.CommandExceptionHandler;
import net.solostudio.drawlock.listeners.JoinListener;
import net.solostudio.drawlock.listeners.MenuListener;
import org.bukkit.Bukkit;
import revxrsal.commands.bukkit.BukkitLamp;

public class RegisterUtils {
    public static void registerListeners() {
        LoggerUtils.info("### Registering listeners... ###");

        Bukkit.getPluginManager().registerEvents(new MenuListener(), DrawLock.getInstance());
        Bukkit.getPluginManager().registerEvents(new JoinListener(), DrawLock.getInstance());

        LoggerUtils.info("### Successfully registered 3 listener. ###");
    }

    public static void registerCommands() {
        LoggerUtils.info("### Registering commands... ###");

        var lamp = BukkitLamp.builder(DrawLock.getInstance())
                .exceptionHandler(new CommandExceptionHandler())
                .build();


        lamp.register(new CommandDrawLock());

        LoggerUtils.info("### Successfully registered exception handlers... ###");
    }
}
