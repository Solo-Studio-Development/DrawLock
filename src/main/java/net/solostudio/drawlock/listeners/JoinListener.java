package net.solostudio.drawlock.listeners;

import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.enums.keys.ConfigKeys;
import net.solostudio.drawlock.managers.MenuController;
import net.solostudio.drawlock.menu.Menu;
import net.solostudio.drawlock.menu.menus.MenuLogin;
import net.solostudio.drawlock.menu.menus.MenuRegister;
import net.solostudio.drawlock.utils.LoggerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener implements Listener {
    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();

        DrawLock.getDatabase().createPlayer(player.getName());

        if (DrawLock.getDatabase().isRegistered(player.getName())) new MenuLogin(MenuController.getMenuUtils(player)).open();
        else new MenuRegister(MenuController.getMenuUtils(player)).open();

        if (!ConfigKeys.BEAUTIFIER_JOIN_MESSAGE_IS_ENABLED.getBoolean()) return;

        ConfigKeys.BEAUTIFIER_JOIN_MESSAGE.getList().forEach(message -> player.sendMessage(message.replace("{player}", player.getName())));
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        Player player = event.getPlayer();
        MenuController.menuMap.remove(player);
    }
}
