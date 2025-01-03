package net.solostudio.drawlock.menu.menus;

import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.enums.keys.ConfigKeys;
import net.solostudio.drawlock.enums.keys.ItemKeys;
import net.solostudio.drawlock.enums.keys.MessageKeys;
import net.solostudio.drawlock.managers.MenuController;
import net.solostudio.drawlock.menu.Menu;
import net.solostudio.drawlock.utils.AES256Utils;
import net.solostudio.drawlock.utils.DrawLockUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MenuRegister extends Menu {
    private static final int MINIMUM_PASSWORD_LENGTH = ConfigKeys.MINIMUM_PASSWORD_LENGTH.getInt();
    private int greenCount = 0;
    private final List<Integer> selectedSlots = new ArrayList<>();

    public MenuRegister(@NotNull MenuController menuController) {
        super(menuController);
    }

    @Override
    public String getMenuName() {
        return ConfigKeys.REGISTER_NAME.getString();
    }

    @Override
    public void handleMenu(final InventoryClickEvent event) {
        var slot = event.getSlot();
        var clickedItem = event.getCurrentItem();
        var player = menuController.owner();

        if (clickedItem != null && clickedItem.isSimilar(ItemKeys.REGISTER_BLANK.getItem())) {
            inventory.setItem(slot, ItemKeys.REGISTER_PASSWORD.getItem());
            selectedSlots.add(slot);
            greenCount++;

            if (greenCount >= MINIMUM_PASSWORD_LENGTH) processPasswordRegistration(player);
        }
    }

    private void processPasswordRegistration(@NotNull Player player) {
        String password = selectedSlots.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        String encryptedPassword = AES256Utils.encrypt(password);

        close();
        DrawLock.getDatabase().savePasswordToDatabase(player.getName(), Objects.requireNonNull(encryptedPassword));
        DrawLock.getDatabase().saveDate(player.getName(), "CREATED_AT");
        DrawLock.getDatabase().saveDate(player.getName(), "LAST_LOGIN");
        player.sendMessage(MessageKeys.SUCCESS_REGISTER.getMessage());
        DrawLockUtils.playSound(player, "register.sounds", ".success");
        DrawLockUtils.sendToServer(menuController.owner(), "register.server");
    }

    @Override
    public void setMenuItems() {
        IntStream.range(0, inventory.getSize())
                .filter(index -> inventory.getItem(index) == null)
                .forEach(index -> inventory.setItem(index, ItemKeys.REGISTER_BLANK.getItem()));
    }

    @Override
    public void handleInventoryClose(final InventoryCloseEvent event) {
        super.handleInventoryClose(event);
    }
}