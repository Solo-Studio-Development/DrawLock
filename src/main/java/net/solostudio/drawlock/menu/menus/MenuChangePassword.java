package net.solostudio.drawlock.menu.menus;

import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.enums.keys.ConfigKeys;
import net.solostudio.drawlock.enums.keys.ItemKeys;
import net.solostudio.drawlock.enums.keys.MessageKeys;
import net.solostudio.drawlock.managers.MenuController;
import net.solostudio.drawlock.menu.Menu;
import net.solostudio.drawlock.utils.BCryptUtils;
import net.solostudio.drawlock.utils.DrawLockUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class MenuChangePassword extends Menu {
    private final int minPasswordLength = ConfigKeys.MINIMUM_PASSWORD_LENGTH.getInt();
    private final List<Integer> selectedSlots = new ArrayList<>();
    private int greenCount = 0;

    public MenuChangePassword(@NotNull MenuController menuController) {
        super(menuController);
    }

    @Override
    public final String getMenuName() {
        return ConfigKeys.CHANGE_PASSWORD_NAME.getString();
    }

    @Override
    public void handleMenu(final InventoryClickEvent event) {
        final var slot = event.getSlot();
        final var clickedItem = event.getCurrentItem();
        final var player = menuController.owner();

        if (clickedItem != null && clickedItem.isSimilar(ItemKeys.CHANGE_PASSWORD_BLANK.getItem())) {
            inventory.setItem(slot, ItemKeys.CHANGE_PASSWORD_PASSWORD.getItem());
            selectedSlots.add(slot);
            greenCount++;

            if (greenCount >= minPasswordLength) changePassword(player);
        }

        if (event.isShiftClick()) reset();
    }

    private void changePassword(@NotNull final Player player) {
        final var password = selectedSlots.stream()
                .map(String::valueOf)
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElse("");

        final var hashedPassword = BCryptUtils.hashPassword(password);

        close();
        DrawLock.getDatabase().savePasswordToDatabase(player.getName(), hashedPassword);
        player.sendMessage(MessageKeys.SUCCESS_CHANGE_PASSWORD.getMessage());
        DrawLockUtils.playSound(player, "change-password.sounds", ".success");

    }

    @Override
    public void setMenuItems() {
        IntStream.range(0, inventory.getSize())
                .filter(index -> inventory.getItem(index) == null)
                .forEach(index -> inventory.setItem(index, ItemKeys.CHANGE_PASSWORD_BLANK.getItem()));
    }

    @Override
    public void handleInventoryClose(final InventoryCloseEvent event) {
        super.handleInventoryClose(event);
    }

    private void reset() {
        IntStream.range(0, inventory.getSize())
                .forEach(index -> inventory.setItem(index, ItemKeys.CHANGE_PASSWORD_BLANK.getItem()));

        greenCount = 0;
        selectedSlots.clear();
    }
}