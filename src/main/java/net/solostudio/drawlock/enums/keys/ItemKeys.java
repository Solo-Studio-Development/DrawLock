package net.solostudio.drawlock.enums.keys;

import net.solostudio.drawlock.item.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public enum ItemKeys {
    LOGIN_BLANK("login.gui.blank"),
    LOGIN_PASSWORD("login.gui.password"),

    REGISTER_BLANK("register.gui.blank"),
    REGISTER_PASSWORD("register.gui.password");

    private final String path;

    ItemKeys(@NotNull final String path) {
        this.path = path;
    }

    public ItemStack getItem() {
        return ItemFactory.createItemFromString(path);
    }
}
