package net.solostudio.drawlock.utils;

import lombok.experimental.UtilityClass;
import net.solostudio.drawlock.enums.keys.ConfigKeys;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@UtilityClass
public class AES256Utils {
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;

    public static String encrypt(@NotNull String text) {
        try {
            byte[] iv = ConfigKeys.ENCRYPTION_IV.getString().getBytes(StandardCharsets.UTF_8);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv);
            SecretKeySpec secretKeySpec = new SecretKeySpec(ConfigKeys.ENCRYPTION_SECRET_KEY.getString().getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);

            byte[] cipherText = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            byte[] encryptedData = new byte[IV_LENGTH + cipherText.length];

            System.arraycopy(iv, 0, encryptedData, 0, IV_LENGTH);
            System.arraycopy(cipherText, 0, encryptedData, IV_LENGTH, cipherText.length);

            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception exception) {
            LoggerUtils.error(exception.getMessage());
            return null;
        }
    }

    public static String decrypt(@NotNull String encryptedText) {
        try {
            byte[] encryptedData = Base64.getDecoder().decode(encryptedText);
            byte[] iv = ConfigKeys.ENCRYPTION_IV.getString().getBytes(StandardCharsets.UTF_8);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            SecretKeySpec secretKeySpec = new SecretKeySpec(ConfigKeys.ENCRYPTION_SECRET_KEY.getString().getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);

            byte[] cipherText = new byte[encryptedData.length - IV_LENGTH];

            System.arraycopy(encryptedData, IV_LENGTH, cipherText, 0, cipherText.length);

            byte[] decryptedText = cipher.doFinal(cipherText);

            return new String(decryptedText, StandardCharsets.UTF_8);
        } catch (Exception exception) {
            LoggerUtils.error(exception.getMessage());
            return null;
        }
    }
}
