package net.solostudio.drawlock;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.Getter;
import net.solostudio.drawlock.config.Config;
import net.solostudio.drawlock.database.DatabaseProxy;
import net.solostudio.drawlock.services.TOTPService;
import net.solostudio.drawlock.interfaces.DrawLockDatabase;
import net.solostudio.drawlock.database.H2;
import net.solostudio.drawlock.database.MySQL;
import net.solostudio.drawlock.enums.DatabaseTypes;
import net.solostudio.drawlock.enums.LanguageTypes;
import net.solostudio.drawlock.enums.keys.ConfigKeys;
import net.solostudio.drawlock.language.Language;
import net.solostudio.drawlock.utils.LoggerUtils;
import org.bstats.bukkit.Metrics;
import revxrsal.zapper.ZapperJavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

import static net.solostudio.drawlock.hooks.PlaceholderAPI.registerHook;
import static net.solostudio.drawlock.utils.StartingUtils.initialize;
import static net.solostudio.drawlock.utils.StartingUtils.saveResourceIfNotExists;

public final class DrawLock extends ZapperJavaPlugin {
    private static final int BSTATS_ID = 24209;

    @Getter private static DrawLock instance;
    @Getter private static DrawLockDatabase database;
    @Getter private TaskScheduler scheduler;
    @Getter private Language language;
    @Getter private GoogleAuthenticator googleAuthenticator;
    @Getter private TOTPService totpService;
    private Config config;

    @Override
    public void onLoad() {
        instance = this;
        scheduler = UniversalScheduler.getScheduler(this);
        googleAuthenticator = new GoogleAuthenticator();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        initializeComponents();
        initializeDatabaseManager();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord"); // TODO this!!
        totpService = new TOTPService();
        // updates - later

        try {
            initialize();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | NoSuchMethodException | IllegalAccessException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        new Metrics(this, BSTATS_ID);
        getGoogleAuthenticator().setCredentialRepository(new TOTPService());
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

        saveResourceIfNotExists("config.yml");

        Arrays.stream(LanguageTypes.values())
                .map(LanguageTypes::name)
                .forEach(name -> saveResourceIfNotExists("locales/messages_" + name.toLowerCase() + ".yml"));

        language = new Language("messages_" + LanguageTypes.valueOf(ConfigKeys.LANGUAGE.getString()));
    }

    private void initializeDatabaseManager() {
        try {
            DrawLockDatabase databaseInstance;

            switch (DatabaseTypes.valueOf(ConfigKeys.DATABASE.getString().toUpperCase())) {
                case MYSQL -> {
                    LoggerUtils.info("### MySQL support found! Starting to initialize it... ###");
                    databaseInstance = new MySQL(Objects.requireNonNull(getConfiguration().getSection("database.mysql")));
                    databaseInstance.createTable();
                    LoggerUtils.info("### MySQL database has been successfully initialized! ###");
                }
                case H2 -> {
                    LoggerUtils.info("### H2 support found! Starting to initialize it... ###");
                    databaseInstance = new H2();
                    databaseInstance.createTable();
                    LoggerUtils.info("### H2 database has been successfully initialized! ###");
                }
                default -> throw new SQLException("Unsupported database type!");
            }

            database = DatabaseProxy.createProxy(DrawLockDatabase.class, databaseInstance);
        } catch (SQLException | ClassNotFoundException exception) {
            LoggerUtils.error("Database initialization failed: {}", exception.getMessage());
        }
    }
}
