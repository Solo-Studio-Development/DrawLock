package net.solostudio.drawlock.utils;

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class LoggerUtils {
    private final Logger logger = LogManager.getLogger("DrawLock");

    public void info(@NotNull final String msg, @NotNull final Object... objs) {
        logger.info(msg, objs);
    }

    public void warn(@NotNull final String msg, @NotNull final Object... objs) {
        logger.warn(msg, objs);
    }

    public void error(@NotNull final String msg, @NotNull final Object... objs) {
        logger.error(msg, objs);
    }
}
