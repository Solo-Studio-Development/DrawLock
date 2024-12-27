package net.solostudio.drawlock.utils;

import lombok.experimental.UtilityClass;
import net.solostudio.drawlock.DrawLock;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("deprecation")
@UtilityClass
public class DrawLockUtils {
    public void playSuccessSound(@NotNull Player player, @NotNull String path) {
        if (!DrawLock.getInstance().getConfiguration().getBoolean(path + ".enabled")) return;

        List<String> soundNames = DrawLock.getInstance().getConfiguration().getList(path + ".success");

        soundNames.stream()
                .map(name -> Sound.valueOf(name.toUpperCase()))
                .forEach(sound -> player.playSound(player, sound, 1.0f, 1.0f));
    }

    public void playDeniedSound(@NotNull Player player, @NotNull String path) {
        if (!DrawLock.getInstance().getConfiguration().getBoolean(path + ".enabled")) return;

        List<String> soundNames = DrawLock.getInstance().getConfiguration().getList(path + ".denied");

        soundNames.stream()
                .map(name -> Sound.valueOf(name.toUpperCase()))
                .forEach(sound -> player.playSound(player, sound, 1.0f, 1.0f));
    }
}
