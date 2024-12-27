package net.solostudio.drawlock.listeners;

import net.solostudio.drawlock.menu.Menu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class QuitListener implements Listener {
    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof Menu menu)) return;

        menu.handleInventoryClose(event);
    }
}
