package net.solostudio.drawlock.utils;

import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.config.Config;
import net.solostudio.drawlock.managers.MenuController;
import net.solostudio.drawlock.menu.Menu;
import net.solostudio.drawlock.processor.MessageProcessor;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class BossBarUtils {
    public static final Map<Player, BossBar> activeBossBars = new ConcurrentHashMap<>();
    private MyScheduledTask task;

    public void removeBossBar(@NotNull Player player) {
        BossBar bossBar = activeBossBars.remove(player);

        if (bossBar == null) return;

        bossBar.setVisible(false);
        bossBar.removeAll();
        task.cancel();
    }

    public void createBossBar(@NotNull Player player, @NotNull String path) {
        Config config = DrawLock.getInstance().getConfiguration();

        if (!config.getBoolean(path + ".enabled")) return;

        String rawTitle = MessageProcessor.process(config.getString(path + ".title"));
        BarColor color = BarColor.valueOf(config.getString(path + ".color").toUpperCase());
        BarStyle style = BarStyle.valueOf(config.getString(path + ".style").toUpperCase());
        List<String> flags = config.getList(path + ".flags");
        List<String> commands = config.getList(path + ".commands");
        final int totalDuration = config.getInt(path + ".time");
        final int[] remainingTime = {totalDuration};

        BossBar bossBar = Bukkit.createBossBar(rawTitle, color, style);

        activeBossBars.put(player, bossBar);
        flags.forEach(flag -> bossBar.addFlag(BarFlag.valueOf(flag.toUpperCase())));
        bossBar.setProgress(1.0);
        bossBar.addPlayer(player);
        bossBar.setVisible(true);

        task = DrawLock.getInstance().getScheduler().runTaskTimer(() -> {
            if (!activeBossBars.containsKey(player)) {
                bossBar.setVisible(false);
                bossBar.removeAll();
                task.cancel();
                return;
            }

            remainingTime[0]--;

            if (remainingTime[0] <= 0) {
                bossBar.setVisible(false);
                bossBar.removeAll();
                task.cancel();
                activeBossBars.remove(player);
                commands.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName())));
                return;
            }

            double progress = (double) remainingTime[0] / totalDuration;
            bossBar.setProgress(progress);
            bossBar.setTitle(MessageProcessor.process(rawTitle.replace("{time}", String.valueOf(remainingTime[0]))));
        }, 0, 20);
    }
}
