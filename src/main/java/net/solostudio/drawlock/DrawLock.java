package net.solostudio.drawlock;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import lombok.Getter;
import net.solostudio.drawlock.config.Config;
import net.solostudio.drawlock.database.AbstractDatabase;
import net.solostudio.drawlock.database.H2;
import net.solostudio.drawlock.database.MySQL;
import net.solostudio.drawlock.enums.DatabaseTypes;
import net.solostudio.drawlock.enums.LanguageTypes;
import net.solostudio.drawlock.enums.keys.ConfigKeys;
import net.solostudio.drawlock.hooks.PlaceholderAPI;
import net.solostudio.drawlock.language.Language;
import net.solostudio.drawlock.utils.LoggerUtils;
import org.bstats.bukkit.Metrics;
import revxrsal.zapper.ZapperJavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Objects;

import static net.solostudio.drawlock.hooks.PlaceholderAPI.registerHook;
import static net.solostudio.drawlock.utils.StartingUtils.initialize;
import static net.solostudio.drawlock.utils.StartingUtils.saveResourceIfNotExists;

public final class DrawLock extends ZapperJavaPlugin {
    @Getter private static DrawLock instance;
    @Getter private TaskScheduler scheduler;
    @Getter private Language language;
    @Getter private static AbstractDatabase database;
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
        initializeDatabaseManager();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord"); // TODO this!!
        // updates - later

        try {
            initialize();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | NoSuchMethodException | IllegalAccessException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        new Metrics(this, 24209);
        registerHook();
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);

        if (database != null) database.disconnect();
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

    private void initializeDatabaseManager() {
        try {
            switch (DatabaseTypes.valueOf(ConfigKeys.DATABASE.getString().toUpperCase())) {
                case MYSQL -> {
                    LoggerUtils.info("### MySQL support found! Starting to initializing it... ###");
                    database = new MySQL(Objects.requireNonNull(getConfiguration().getSection("database.mysql")));
                    MySQL mySQL = (MySQL) database;

                    mySQL.createTable();
                    LoggerUtils.info("### MySQL database has been successfully initialized! ###");
                }
                case H2 -> {
                    LoggerUtils.info("### H2 support found! Starting to initializing it... ###");
                    database = new H2();
                    H2 h2 = (H2) database;

                    h2.createTable();
                    LoggerUtils.info("### H2 database has been successfully initialized! ###");
                }
                default -> throw new SQLException("Unsupported database type!");
            }
        } catch (SQLException | ClassNotFoundException exception) {
            LoggerUtils.error("Database initialization failed: {}", exception.getMessage());
        }
    }
}
