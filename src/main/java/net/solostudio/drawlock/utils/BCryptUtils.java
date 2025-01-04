package net.solostudio.drawlock.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.mindrot.jbcrypt.BCrypt;

@UtilityClass
public class BCryptUtils {
    public String hashPassword(@NotNull String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkPassword(@NotNull String password, @NotNull String hashed) {
        return BCrypt.checkpw(password, hashed);
    }
}
