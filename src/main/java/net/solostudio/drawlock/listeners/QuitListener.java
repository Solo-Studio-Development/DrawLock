package net.solostudio.drawlock.listeners;

import net.solostudio.drawlock.menu.Menu;
import net.solostudio.drawlock.menu.menus.MenuLogin;
import net.solostudio.drawlock.utils.BossBarUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof final Menu menu)) return;

        menu.handleInventoryClose(event);

        if (event.getPlayer() instanceof final Player player && player.isOnline()) MenuLogin.attempts.remove(player);
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        final var player = event.getPlayer();

        MenuLogin.attempts.remove(player);
        BossBarUtils.removeBossBar(player);
    }
}
