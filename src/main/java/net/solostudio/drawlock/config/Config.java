package net.solostudio.drawlock.config;

import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.managers.ConfigurationManager;

public class Config extends ConfigurationManager {
    public Config() {
        super(DrawLock.getInstance().getDataFolder().getPath(), "config");
        save();
    }
}
