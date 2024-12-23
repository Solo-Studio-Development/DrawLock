package net.solostudio.drawlock.menu;

import net.solostudio.drawlock.enums.keys.ConfigKeys;
import net.solostudio.drawlock.managers.MenuController;
import net.solostudio.drawlock.processor.MessageProcessor;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

public abstract class Menu implements InventoryHolder {
    protected MenuController menuController;
    protected Inventory inventory;
    private boolean isClosing = false; // Track if the menu is being closed by the code

    public Menu(@NotNull MenuController menuController) {
        this.menuController = menuController;
    }

    public abstract void handleMenu(final InventoryClickEvent event);
    public abstract void setMenuItems();
    public abstract String getMenuName();
    public abstract int getSlots();
    public abstract String getType();

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void open() {
        String type = getType();
        int slots = getSlots();

        if (slots == 0 && !getType().isEmpty()) inventory = Bukkit.createInventory(this, InventoryType.valueOf(type), MessageProcessor.process(getMenuName()));
        else inventory = Bukkit.createInventory(this, slots, MessageProcessor.process(getMenuName()));

        this.setMenuItems();

        menuController.owner().openInventory(inventory);
    }

    public void close() {
        isClosing = true;
        if (inventory != null) {
            inventory.clear();
            menuController.owner().closeInventory();
        }
    }

    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(inventory)) {
            if (isClosing) {
                inventory = null;
            } else {
                menuController.owner().openInventory(inventory);
            }
        }
    }

    public void resetClosingStatus() {
        isClosing = false;
    }
}
