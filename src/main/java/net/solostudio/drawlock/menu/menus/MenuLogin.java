package net.solostudio.drawlock.menu.menus;

import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
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
import java.util.stream.IntStream;

@SuppressWarnings("all")
public class MenuLogin extends Menu {
    private static final String type = ConfigKeys.LOGIN_TYPE.getString();
    private int greenCount = 0;
    private final List<Integer> selectedSlots = new ArrayList<>();
    private MyScheduledTask task;
    private boolean locked;

    public MenuLogin(@NotNull MenuController menuController) {
        super(menuController);
        this.locked = false;
    }

    @Override
    public String getMenuName() {
        return ConfigKeys.LOGIN_NAME.getString();
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int getSlots() {
        int size = ConfigKeys.LOGIN_SIZE.getInt();

        if (size == 0 && !type.isEmpty()) return 0;
        else return size;
    }

    @Override
    public void handleMenu(final InventoryClickEvent event) {
        if (locked) return;

        int slot = event.getSlot();
        ItemStack clickedItem = event.getCurrentItem();
        Player player = menuController.owner();

        if (clickedItem != null && clickedItem.isSimilar(ItemKeys.LOGIN_BLANK.getItem())) {
            inventory.setItem(slot, ItemKeys.LOGIN_PASSWORD.getItem());
            selectedSlots.add(slot);
            greenCount++;

            if (greenCount >= ConfigKeys.MINIMUM_PASSWORD_LENGTH.getInt()) {
                String password = selectedSlots.stream()
                        .map(String::valueOf)
                        .reduce((s1, s2) -> s1 + ", " + s2)
                        .orElse("");

                String encryptedPassword = DrawLock.getDatabase().getPassword(menuController.owner().getName());

                if (encryptedPassword.equals(password)) {
                    close();
                    player.sendMessage(MessageKeys.SUCCESS_LOGIN.getMessage());
                    DrawLockUtils.playSuccessSound(player, "login.sounds");
                    DrawLock.getDatabase().saveDate(player.getName(), "LAST_LOGIN");
                } else setErrorItems();
            }
        }
    }

    @Override
    public void setMenuItems() {
        IntStream.range(0, inventory.getSize()).forEach(index -> {
            if (inventory.getItem(index) == null) inventory.setItem(index, ItemKeys.LOGIN_BLANK.getItem());
        });
    }

    @Override
    public void handleInventoryClose(final InventoryCloseEvent event) {
        super.handleInventoryClose(event);
    }

    private void setErrorItems() {
        final ItemStack redPane = ItemKeys.LOGIN_ERROR.getItem();
        final ItemStack grayPane = ItemKeys.LOGIN_BLANK.getItem();
        int flashCount = 3;
        final int totalFlashes = flashCount * 2;
        final int[] currentFlash = {0};
        locked = true;

        if (task != null) task.cancel();

        menuController.owner().sendMessage(MessageKeys.WRONG_PASSWORD.getMessage());
        selectedSlots.clear();
        greenCount = 0;

        task = DrawLock.getInstance().getScheduler().runTaskTimer(() -> {
            if (currentFlash[0] >= totalFlashes) {
                task.cancel();
                locked = false;
                return;
            }

            IntStream.range(0, inventory.getSize()).forEach(index -> {
                inventory.setItem(index, (currentFlash[0] % 2 == 0) ? redPane : grayPane);
            });

            DrawLockUtils.playDeniedSound(menuController.owner(), "login.sounds");
            currentFlash[0]++;
        }, 0, 5);
    }
}
