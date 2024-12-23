package net.solostudio.drawlock.listeners;

import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.enums.keys.ConfigKeys;
import net.solostudio.drawlock.managers.MenuController;
import net.solostudio.drawlock.menu.menus.MenuRegister;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();

        DrawLock.getDatabase().createPlayer(player.getName());

        new MenuRegister(MenuController.getMenuUtils(player)).open();

        if (!ConfigKeys.BEAUTIFIER_JOIN_MESSAGE_IS_ENABLED.getBoolean()) return;

        ConfigKeys.BEAUTIFIER_JOIN_MESSAGE.getList().forEach(player::sendMessage);
    }
}
