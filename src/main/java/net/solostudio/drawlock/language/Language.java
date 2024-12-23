package net.solostudio.drawlock.language;

import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.managers.ConfigurationManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Language extends ConfigurationManager {
    public Language(@NotNull String name) {
        super(DrawLock.getInstance().getDataFolder().getPath() + File.separator + "locales", name);
        save();
    }
}
