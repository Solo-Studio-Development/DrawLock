package net.solostudio.drawlock.menu.menus;

import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.enums.keys.ConfigKeys;
import net.solostudio.drawlock.enums.keys.ItemKeys;
import net.solostudio.drawlock.managers.MenuController;
import net.solostudio.drawlock.menu.Menu;
import net.solostudio.drawlock.utils.AES256Utils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@SuppressWarnings("all")
public class MenuRegister extends Menu {
    private static final String type = ConfigKeys.REGISTER_TYPE.getString();
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
    public String getType() {
        return type;
    }

    @Override
    public int getSlots() {
        int size = ConfigKeys.REGISTER_SIZE.getInt();

        if (size == 0 && !type.isEmpty()) return 0;
        else return size;
    }

    @Override
    public void handleMenu(final InventoryClickEvent event) {
        int slot = event.getSlot();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem != null && clickedItem.isSimilar(ItemKeys.REGISTER_BLANK.getItem())) {
            inventory.setItem(slot, ItemKeys.REGISTER_PASSWORD.getItem());
            selectedSlots.add(slot);
            greenCount++;

            if (greenCount >= ConfigKeys.MINIMUM_PASSWORD_LENGTH.getInt()) {
                String password = selectedSlots.stream()
                        .map(String::valueOf)
                        .reduce((s1, s2) -> s1 + ", " + s2)
                        .orElse("");
                String encryptedPassword = AES256Utils.encrypt(password);

                close();
                DrawLock.getDatabase().savePasswordToDatabase(menuController.owner().getName(), Objects.requireNonNull(encryptedPassword));
                menuController.owner().sendMessage(DrawLock.getDatabase().getPassword(menuController.owner().getName()));
            }
        }
    }

    @Override
    public void setMenuItems() {
        IntStream.range(0, inventory.getSize()).forEach(index -> {
            if (inventory.getItem(index) == null) inventory.setItem(index, ItemKeys.REGISTER_BLANK.getItem());
        });
    }

    @Override
    public void handleInventoryClose(final InventoryCloseEvent event) {
        super.handleInventoryClose(event);
    }
}
