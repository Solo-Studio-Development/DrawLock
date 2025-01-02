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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MenuLogin extends Menu {
    public static final Map<Player, Integer> ATTEMPTS = new HashMap<>();

    private int greenCount = 0;
    private final List<Integer> selectedSlots = new ArrayList<>();
    private MyScheduledTask task;
    private boolean locked;

    private final Player player;
    private final Runnable onSuccess;

    public MenuLogin(@NotNull MenuController menuController, @Nullable Runnable onSuccess) {
        super(menuController);
        this.locked = false;
        this.onSuccess = onSuccess;
        this.player = menuController.owner();
    }

    @Override
    public String getMenuName() {
        return ConfigKeys.LOGIN_NAME.getString();
    }

    @Override
    public void handleMenu(final InventoryClickEvent event) {
        if (locked) return;

        var slot = event.getSlot();
        var clickedItem = event.getCurrentItem();

        if (clickedItem != null && clickedItem.isSimilar(ItemKeys.LOGIN_BLANK.getItem())) {
            inventory.setItem(slot, ItemKeys.LOGIN_PASSWORD.getItem());
            selectedSlots.add(slot);
            greenCount++;

            if (greenCount >= ConfigKeys.MINIMUM_PASSWORD_LENGTH.getInt()) validatePassword();
        }
    }

    private void validatePassword() {
        String password = selectedSlots.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        String encryptedPassword = DrawLock.getDatabase().getPassword(player.getName());

        if (encryptedPassword.equals(password)) handleSuccessfulLogin();
        else setErrorItems();
    }

    private void handleSuccessfulLogin() {
        ATTEMPTS.remove(player);
        close();
        player.sendMessage(MessageKeys.SUCCESS_LOGIN.getMessage());
        DrawLockUtils.playSound(player, "login.sounds", ".success");
        DrawLock.getDatabase().saveDate(player.getName(), "LAST_LOGIN");

        if (onSuccess != null) onSuccess.run();
    }

    @Override
    public void setMenuItems() {
        IntStream.range(0, inventory.getSize())
                .filter(index -> inventory.getItem(index) == null)
                .forEach(index -> inventory.setItem(index, ItemKeys.LOGIN_BLANK.getItem()));
    }

    @Override
    public void handleInventoryClose(final InventoryCloseEvent event) {
        super.handleInventoryClose(event);
    }

    private void setErrorItems() {
        int currentAttempts = ATTEMPTS.getOrDefault(player, 0) + 1;

        ATTEMPTS.put(player, currentAttempts);

        if (currentAttempts >= ConfigKeys.MAX_ATTEMPTS.getInt()) {
            close();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ConfigKeys.ATTEMPT_COMMAND
                    .getString()
                    .replace("{player}", player.getName()));
            ATTEMPTS.remove(player);
            return;
        }

        flashErrorItems();
    }

    private void flashErrorItems() {
        final ItemStack redPane = ItemKeys.LOGIN_ERROR.getItem();
        final ItemStack grayPane = ItemKeys.LOGIN_BLANK.getItem();
        final int[] currentFlash = {0};
        int totalFlashes = 6;
        locked = true;

        if (task != null) task.cancel();

        player.sendMessage(MessageKeys.WRONG_PASSWORD.getMessage());
        selectedSlots.clear();
        greenCount = 0;

        task = DrawLock.getInstance().getScheduler().runTaskTimer(() -> {
            IntStream.range(0, inventory.getSize())
                    .forEach(index -> inventory.setItem(index, (currentFlash[0] % 2 == 0) ? redPane : grayPane));

            DrawLockUtils.playSound(player, "login.sounds", ".denied");
            currentFlash[0]++;

            if (currentFlash[0] == totalFlashes) {
                task.cancel();
                locked = false;
            }
        }, 0, 5);
    }
}