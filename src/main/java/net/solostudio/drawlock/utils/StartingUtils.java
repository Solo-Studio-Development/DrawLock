package net.solostudio.drawlock.utils;

import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.enums.VersionTypes;
import net.solostudio.drawlock.versions.VersionSupport;
import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.solostudio.drawlock.enums.VersionTypes.determineVersion;

public class StartingUtils {
    private static final int REQUIRED_VM_VERSION = 17;

    public static void initialize() throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        RegisterUtils.registerCommands();
        RegisterUtils.registerListeners();
        validateEnvironment();
    }

    public static void saveResourceIfNotExists(@NotNull final String resourcePath) {
        final var targetFile = new File(DrawLock.getInstance().getDataFolder(), resourcePath);

        if (!targetFile.exists()) DrawLock.getInstance().saveResource(resourcePath, false);
    }

    private static void validateEnvironment() throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if (!checkVMVersion()) {
            disablePlugin("### Wrong VM version! Required version: " + REQUIRED_VM_VERSION + " or higher. ###");
            return;
        }

        if (!checkVersion()) disablePlugin("### Unsupported server version. Please update your server to a supported version. ###");
    }

    private static boolean checkVMVersion() {
        final var vmVersion = getVMVersion();

        if (vmVersion < REQUIRED_VM_VERSION) {
            LoggerUtils.error("### Detected Java version: {}. Required: {} or higher. ###", vmVersion, REQUIRED_VM_VERSION);
            return false;
        }

        return true;
    }

    private static boolean checkVersion() throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if (!isSpigotConfigPresent()) {
            LoggerUtils.error("### SpigotConfig class not found. This might indicate an unsupported server. ###");
            return false;
        }

        final var bukkitVersion = Bukkit.getVersion();

        LoggerUtils.info("### Detected Bukkit version string: {} ###", bukkitVersion);

        VersionTypes version = extractVersionFromBukkitString(bukkitVersion);

        if (version == VersionTypes.UNKNOWN) {
            LoggerUtils.error("### Unknown Minecraft version detected. ###");
            return false;
        }

        return new VersionSupport(version).getVersionSupport() != null;
    }

    private static boolean isSpigotConfigPresent() {
        try {
            Class.forName("org.spigotmc.SpigotConfig");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    private static VersionTypes extractVersionFromBukkitString(@NotNull final String bukkitVersion) {
        final var versionPattern = Pattern.compile("\\(MC: (\\d{1,2})\\.(\\d{1,2})(?:\\.(\\d{1,2}))?\\)");
        final var matcher = versionPattern.matcher(bukkitVersion);

        if (matcher.find()) {
            int majorVersion = Integer.parseInt(matcher.group(1));
            int minorVersion = Integer.parseInt(matcher.group(2));
            int patchVersion = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;

            return determineVersion(majorVersion, minorVersion, patchVersion);
        }

        LoggerUtils.error("### Could not parse Minecraft version from Bukkit string: {} ###", bukkitVersion);
        return VersionTypes.UNKNOWN;
    }

    private static int getVMVersion() {
        final var javaVersion = System.getProperty("java.version");
        final var matcher = Pattern.compile("(?:1\\.)?(\\d+)").matcher(javaVersion);

        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException ignored) {}
        }

        LoggerUtils.error("### Unable to detect Java version from string: {} ###", javaVersion);
        return -1;
    }

    private static void disablePlugin(@NotNull String errorMessage) {
        LoggerUtils.error(errorMessage);
        Bukkit.getPluginManager().disablePlugin(DrawLock.getInstance());
    }
}
