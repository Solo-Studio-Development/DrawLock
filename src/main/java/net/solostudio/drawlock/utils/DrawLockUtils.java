package net.solostudio.drawlock.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.experimental.UtilityClass;
import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.config.Config;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("deprecation")
@UtilityClass
public class DrawLockUtils {
    public void playSound(@NotNull Player player, @NotNull String path, @NotNull String reason) {
        if (!DrawLock.getInstance().getConfiguration().getBoolean(path + ".enabled")) return;

        List<String> soundNames = DrawLock.getInstance().getConfiguration().getList(path + reason);

        soundNames.stream()
                .map(name -> Sound.valueOf(name.toUpperCase()))
                .forEach(sound -> player.playSound(player, sound, 1.0f, 1.0f));
    }

    public void sendToServer(@NotNull Player player, @NotNull String path) {
        Config config = DrawLock.getInstance().getConfiguration();

        if (!config.getBoolean(path + ".enabled")) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Connect");
        out.writeUTF(player.getName());
        out.writeUTF(config.getString(path + ".where"));

        player.sendPluginMessage(DrawLock.getInstance(), "BungeeCord", out.toByteArray());
    }
}
