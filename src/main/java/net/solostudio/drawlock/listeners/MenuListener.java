package net.solostudio.drawlock.listeners;

import net.solostudio.drawlock.menu.Menu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuListener implements Listener {
    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof final Menu menu)) return;

        event.setCancelled(true);
        menu.handleMenu(event);
    }
}
