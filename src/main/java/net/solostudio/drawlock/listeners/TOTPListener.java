package net.solostudio.drawlock.listeners;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.ICredentialRepository;
import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.database.TOTPCredentials;
import net.solostudio.drawlock.enums.keys.ConfigKeys;
import net.solostudio.drawlock.utils.BCryptUtils;
import net.solostudio.drawlock.utils.TOTPUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public class TOTPListener implements Listener {
    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        TOTPCredentials credentialRepository = new TOTPCredentials();
        String existingSecretKey = credentialRepository.getSecretKey(player.getName());

        if (existingSecretKey == null) {
            GoogleAuthenticatorKey key = DrawLock.getInstance().getGoogleAuthenticator().createCredentials(player.getName());
            String secretKey = key.getKey();

            credentialRepository.saveUserCredentials(player.getName(), secretKey, 0, null);

            String otpAuthUrl = String.format("otpauth://totp/%s?secret=%s&issuer=%s&logo=%s", player.getName(), secretKey, ConfigKeys.TOTP_NAME.getString(), ConfigKeys.TOTP_LOGO_URL.getString());

            ItemStack mapItem = TOTPUtils.createMapFromQRCode(otpAuthUrl, player);
            player.getInventory().addItem(mapItem);

            player.sendMessage("Your secret key: " + secretKey);
            player.sendMessage("Scan this QR code with your authenticator app.");
        } else {
            player.sendMessage("You already have a secret key set up. Use your authenticator app to log in.");
        }
    }

    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        TOTPCredentials credentialRepository = new TOTPCredentials();

        String secretKey = credentialRepository.getSecretKey(player.getName());

        if (secretKey != null) {
            GoogleAuthenticator googleAuthenticator = DrawLock.getInstance().getGoogleAuthenticator();

            boolean isValid = googleAuthenticator.authorize(secretKey, Integer.parseInt(message));

            if (isValid) {
                event.setCancelled(true);
                player.getInventory().clear();
                player.sendMessage("Sikeres autentikáció!");
            } else {
                player.sendMessage("Helytelen kód! Próbáld újra.");
            }
        }
    }
}
