package net.solostudio.drawlock.utils;

import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.commands.CommandDrawLock;
import net.solostudio.drawlock.exception.CommandExceptionHandler;
import revxrsal.commands.bukkit.BukkitLamp;

public class RegisterUtils {
    public static void registerListeners() {
        LoggerUtils.info("### Registering listeners... ###");

        //Bukkit.getPluginManager().registerEvents(new DatabaseListener(), Vaultcher.getInstance()); - Registering

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
