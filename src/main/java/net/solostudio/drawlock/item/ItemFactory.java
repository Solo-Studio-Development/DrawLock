package net.solostudio.drawlock.item;

import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.processor.MessageProcessor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public interface ItemFactory {
    static ItemFactory create(@NotNull final Material material) {
        return new ItemBuilder(material);
    }

    static ItemFactory create(@NotNull final Material material, final int count) {
        return new ItemBuilder(material, count);
    }

    static ItemFactory create(@NotNull final Material material, final int count, final short damage) {
        return new ItemBuilder(material, count, damage);
    }

    static ItemFactory create(@NotNull final Material material, final int count, final short damage, final byte data) {
        return new ItemBuilder(material, count, damage, data);
    }

    static ItemFactory create(final ItemStack item) {
        return new ItemBuilder(item);
    }

    ItemFactory setType(@NotNull final Material material);

    ItemFactory setCount(final int newCount);

    ItemFactory setName(@NotNull final String name);

    ItemBuilder addEnchantment(@NotNull final Enchantment enchantment, int level);

    default ItemFactory addEnchantments(final Map<Enchantment, Integer> enchantments) {
        enchantments.forEach(this::addEnchantment);

        return this;
    }

    ItemBuilder addLore(@NotNull final String... lores);

    ItemFactory setUnbreakable();

    default void addFlag(@NotNull final ItemFlag... flags) {
        Arrays
                .stream(flags)
                .forEach(this::addFlag);
    }

    default ItemFactory setLore(@NotNull final String... lores) {
        Arrays
                .stream(lores)
                .forEach(this::addLore);
        return this;
    }

    ItemFactory removeLore(final int line);

    ItemStack finish();

    boolean isFinished();

    static ItemStack createItemFromString(@NotNull final String path) {
        final var section = DrawLock.getInstance().getConfiguration().getSection(path);
        final var material = Material.valueOf(Objects.requireNonNull(section).getString("material"));
        final var amount = section.getInt("amount", 1);
        final var name = section.getString("name");
        final var loreArray = section.getStringList("lore").toArray(new String[0]);

        IntStream.range(0, loreArray.length).forEach(index -> loreArray[index] = MessageProcessor.process(loreArray[index]));

        return ItemFactory.create(material, amount)
                .setName(Objects.requireNonNull(name))
                .addLore(loreArray)
                .finish();
    }
}
