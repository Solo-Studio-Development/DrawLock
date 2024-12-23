package net.solostudio.drawlock;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import lombok.Getter;
import net.solostudio.drawlock.config.Config;
import net.solostudio.drawlock.enums.LanguageTypes;
import net.solostudio.drawlock.enums.keys.ConfigKeys;
import net.solostudio.drawlock.language.Language;
import net.solostudio.drawlock.utils.LoggerUtils;
import org.bstats.bukkit.Metrics;
import revxrsal.zapper.ZapperJavaPlugin;

import java.lang.reflect.InvocationTargetException;

import static net.solostudio.drawlock.utils.StartingUtils.initialize;
import static net.solostudio.drawlock.utils.StartingUtils.saveResourceIfNotExists;

public final class DrawLock extends ZapperJavaPlugin {
    @Getter private static DrawLock instance;
    @Getter private TaskScheduler scheduler;
    @Getter private Language language;
    private Config config;

    @Override
    public void onLoad() {
        instance = this;
        scheduler = UniversalScheduler.getScheduler(this);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        initializeComponents();
        // database
        // updates - later

        try {
            initialize();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | NoSuchMethodException | IllegalAccessException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        new Metrics(this, 24209);
    }

    @Override
    public void onDisable() {
        // database off
    }

    public Config getConfiguration() {
        return config;
    }

    private void initializeComponents() {
        config = new Config();

        saveResourceIfNotExists("locales/messages_en.yml");
        saveResourceIfNotExists("locales/messages_de.yml");
        saveResourceIfNotExists("config.yml");

        language = new Language("messages_" + LanguageTypes.valueOf(ConfigKeys.LANGUAGE.getString()));
    }
}
