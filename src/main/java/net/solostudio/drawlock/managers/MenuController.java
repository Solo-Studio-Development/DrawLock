package net.solostudio.drawlock.managers;

import net.solostudio.drawlock.menu.Menu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record MenuController(@NotNull Player owner) {
    private static Menu currentMenu;

    public static final Map<Player, MenuController> menuMap = new ConcurrentHashMap<>();

    public static MenuController getMenuUtils(@NotNull Player player) {
        return menuMap.computeIfAbsent(player, MenuController::new);
    }

    public void setCurrentMenu(Menu menu) {
        currentMenu = menu;
    }

    public Menu getCurrentMenu() {
        return currentMenu;
    }

    public boolean isMenuInstance(@NotNull Class<? extends Menu> clazz) {
        return !clazz.isInstance(currentMenu);
    }
}