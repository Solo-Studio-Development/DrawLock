package net.solostudio.drawlock.listeners;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.enums.keys.ConfigKeys;
import net.solostudio.drawlock.utils.TOTPUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public class TOTPListener implements Listener {
    private final Map<String, String> playerSecrets = new HashMap<>();

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        GoogleAuthenticatorKey key = DrawLock.getInstance().getGoogleAuthenticator().createCredentials(player.getName());
        String secretKey = key.getKey();
        playerSecrets.put(player.getName(), secretKey);

        String otpAuthUrl = String.format("otpauth://totp/%s?secret=%s&issuer=%s", player.getName(), secretKey, ConfigKeys.TOTP_NAME.getString());

        String qrCodePath = "plugins/DrawLock/" + player.getName() + "_qr.png";
        TOTPUtils.generateQRCode(otpAuthUrl, qrCodePath);

        player.sendMessage("Your secret key: " + secretKey);
        player.sendMessage("Scan this QR code with your authenticator app.");

        ItemStack mapItem = TOTPUtils.createMapFromQRCode(qrCodePath, player);
        player.getInventory().addItem(mapItem);
    }

    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (playerSecrets.containsKey(player.getName())) {
            String secretKey = playerSecrets.get(player.getName());
            boolean isValid = DrawLock.getInstance().getGoogleAuthenticator().authorize(secretKey, Integer.parseInt(message));

            if (isValid) {
                event.setCancelled(true);
                player.getInventory().clear();
                player.sendMessage("Sikeres autentikáció!");

                String qrCodePath = "plugins/TOTPTest/" + player.getName() + "_qr.png";
                File qrFile = new File(qrCodePath);

                if (qrFile.exists()) qrFile.delete();

                playerSecrets.remove(player.getName());
            } else {
                player.sendMessage("Helytelen jelszó! Próbáld újra.");
            }
        }
    }
}
