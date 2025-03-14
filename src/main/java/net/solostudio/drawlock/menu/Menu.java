package net.solostudio.drawlock.menu;

import lombok.Getter;
import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.enums.keys.ConfigKeys;
import net.solostudio.drawlock.managers.MenuController;
import net.solostudio.drawlock.menu.menus.MenuChangePassword;
import net.solostudio.drawlock.menu.menus.MenuLogin;
import net.solostudio.drawlock.menu.menus.MenuRegister;
import net.solostudio.drawlock.processor.MessageProcessor;
import net.solostudio.drawlock.services.ScoreboardService;
import net.solostudio.drawlock.utils.BossBarUtils;
import net.solostudio.drawlock.utils.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public abstract class Menu implements InventoryHolder {
    protected MenuController menuController;
    protected Inventory inventory;
    private boolean canClose = false;

    public Menu(@NotNull MenuController menuController) {
        this.menuController = menuController;
    }

    public abstract void handleMenu(final InventoryClickEvent event);
    public abstract void setMenuItems();
    public abstract String getMenuName();

    @Getter protected int size = ConfigKeys.MENU_SIZE.getInt();
    @Getter protected String type = ConfigKeys.MENU_TYPE.getString();

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void open() {
        canClose = false;

        if (getSize() == 0 && !getType().isEmpty()) inventory = Bukkit.createInventory(this, InventoryType.valueOf(getType()), MessageProcessor.process(getMenuName()));
        else inventory = Bukkit.createInventory(this, getSize(), MessageProcessor.process(getMenuName()));

        switch (this) {
            case MenuLogin ignored -> BossBarUtils.createBossBar(menuController.owner(), "login.bossbar");
            case MenuRegister ignored -> BossBarUtils.createBossBar(menuController.owner(), "register.bossbar");
            case MenuChangePassword ignored -> {}

            default -> LoggerUtils.error("Bossbar Error");
        }

        this.setMenuItems();
        menuController.owner().openInventory(inventory);
    }

    public void close() {
        canClose = true;
        menuController.owner().closeInventory();

        BossBarUtils.removeBossBar(menuController.owner());
        ScoreboardService.removeScoreboard(menuController.owner());

        inventory = null;
    }

    public void handleInventoryClose(final InventoryCloseEvent event) {
        if (event.getInventory().equals(inventory)) {
            if (!canClose) {
                DrawLock.getInstance().getScheduler().runTask(DrawLock.getInstance(), () -> {
                    canClose = false;
                    menuController.owner().openInventory(inventory);
                });
            }
        }
    }
}
