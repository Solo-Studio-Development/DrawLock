package net.solostudio.drawlock.item;

import net.solostudio.drawlock.processor.MessageProcessor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("all")
public class ItemBuilder implements ItemFactory {
    private final ItemStack is;
    private final ItemMeta meta;
    private boolean finished = false;

    public ItemBuilder(@NotNull final ItemStack item) {
        is = item;
        meta = item.getItemMeta();
    }

    ItemBuilder(@NotNull final Material type) {
        this(type, 1);
    }

    public ItemBuilder(@NotNull final Material type, @Range(from = 0, to = 64) final int amount) {
        this(type, amount, (short) 0);
    }

    public ItemBuilder(@NotNull final Material type, @Range(from = 0, to = 64) final int amount, final short damage) {
        this(type, amount, damage, null);
    }

    public ItemBuilder(@NotNull final Material type, @Range(from = 0, to = 64) final int amount, final short damage, @Nullable final Byte data) {
        is = new ItemStack(type, amount, damage, data);
        meta = is.getItemMeta();
    }

    @Override
    public ItemBuilder setType(@NotNull final Material material) {
        is.setType(material);
        return this;
    }

    @Override
    public ItemBuilder setCount(@Range(from = 0, to = 64) final int newCount) {
        is.setAmount(newCount);
        return this;
    }

    @Override
    public ItemBuilder setName(@NotNull final String name) {
        meta.setDisplayName(MessageProcessor.process(name));
        return this;
    }

    @Override
    public ItemBuilder addEnchantment(@NotNull final Enchantment enchantment, final int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    @Override
    public ItemBuilder addLore(@NotNull final String... lores) {
        var loreList = Arrays.asList(lores);
        var currentLores = meta.getLore();
        currentLores = currentLores == null ? new ArrayList<>() : currentLores;

        currentLores.addAll(loreList);
        meta.setLore(currentLores);

        return this;
    }

    @Override
    public ItemBuilder setUnbreakable() {
        meta.setUnbreakable(true);

        return this;
    }

    public ItemBuilder addFlag(@NotNull final ItemFlag flag) {
        meta.addItemFlags(flag);

        return this;
    }

    @Override
    public ItemBuilder removeLore(final int line) {
        var lores = meta.getLore();
        lores = lores == null ? new ArrayList<>() : lores;

        lores.remove(Math.min(line, lores.size()));
        meta.setLore(lores);

        return this;
    }

    @Override
    public ItemStack finish() {
        is.setItemMeta(meta);

        finished = true;
        return is;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }
}
