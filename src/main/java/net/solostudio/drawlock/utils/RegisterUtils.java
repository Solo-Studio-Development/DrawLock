package net.solostudio.drawlock.utils;

import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.annotations.FullyOrNot;
import net.solostudio.drawlock.annotations.PlayersFromDatabase;
import net.solostudio.drawlock.commands.CommandDrawLock;
import net.solostudio.drawlock.exception.CommandExceptionHandler;
import net.solostudio.drawlock.listeners.JoinListener;
import net.solostudio.drawlock.listeners.MenuListener;
import net.solostudio.drawlock.listeners.QuitListener;
import net.solostudio.drawlock.listeners.TOTPListener;
import org.bukkit.Bukkit;
import revxrsal.commands.bukkit.BukkitLamp;

import java.util.List;

public class RegisterUtils {
    public static void registerListeners() {
        LoggerUtils.info("### Registering listeners... ###");

        Bukkit.getPluginManager().registerEvents(new MenuListener(), DrawLock.getInstance());
        Bukkit.getPluginManager().registerEvents(new JoinListener(), DrawLock.getInstance());
        Bukkit.getPluginManager().registerEvents(new QuitListener(), DrawLock.getInstance());
        Bukkit.getPluginManager().registerEvents(new TOTPListener(), DrawLock.getInstance());

        LoggerUtils.info("### Successfully registered 4 listener. ###");
    }

    public static void registerCommands() {
        LoggerUtils.info("### Registering commands... ###");

        var lamp = BukkitLamp.builder(DrawLock.getInstance())
                .exceptionHandler(new CommandExceptionHandler())
                .suggestionProviders(providers -> providers.addProviderForAnnotation(PlayersFromDatabase.class, playersFromDatabase -> context -> DrawLock.getDatabase().getEveryPlayerInDatabase()
                        .stream()
                        .toList()))
                .suggestionProviders(providers -> providers.addProviderForAnnotation(FullyOrNot.class, fullyOrNot -> context -> List.of("fully", "not fully")))
                .build();


        lamp.register(new CommandDrawLock());

        LoggerUtils.info("### Successfully registered exception handlers... ###");
    }
}
