package net.solostudio.drawlock.listeners;

import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.enums.keys.ConfigKeys;
import net.solostudio.drawlock.enums.keys.MessageKeys;
import net.solostudio.drawlock.managers.MenuController;
import net.solostudio.drawlock.menu.menus.MenuLogin;
import net.solostudio.drawlock.menu.menus.MenuRegister;
import net.solostudio.drawlock.services.ScoreboardService;
import net.solostudio.drawlock.services.TOTPService;
import net.solostudio.drawlock.utils.DrawLockUtils;
import net.solostudio.drawlock.utils.TOTPUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class JoinListener implements Listener {
    public static final Set<String> authenticatedPlayers = new HashSet<>();

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final var player = event.getPlayer();

        DrawLock.getDatabase().createPlayer(player.getName());

        if (ConfigKeys.BEAUTIFIER_JOIN_BLINDNESS_IS_ENABLED.getBoolean()) player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1, true, false));
        if (ConfigKeys.BEAUTIFIER_JOIN_MESSAGE_IS_ENABLED.getBoolean()) ConfigKeys.BEAUTIFIER_JOIN_MESSAGE.getList().forEach(message -> player.sendMessage(message.replace("{player}", player.getName())));
        DrawLockUtils.playSound(player, "beautifier.join.sounds", ".name");

        if (authenticatedPlayers.contains(player.getName())) {
            DrawLockUtils.handleLoginOrRegister(player);
            return;
        }

        if (!ConfigKeys.TOTP_ENABLED.getBoolean()) {
            DrawLockUtils.handleLoginOrRegister(player);
            return;
        }

        final var totpService = DrawLock.getInstance().getTotpService();
        var secretKey = totpService.getSecretKey(player.getName());

        if (secretKey == null) {
            secretKey = DrawLock.getInstance()
                    .getGoogleAuthenticator()
                    .createCredentials(player.getName())
                    .getKey();
            totpService.saveUserCredentials(player.getName(), secretKey, 0, null);

            final var otpAuthUrl = "otpauth://totp/" + player.getName()
                    + "?secret=" + secretKey
                    + "&issuer=" + ConfigKeys.TOTP_NAME.getString();

            final var mapItem = TOTPUtils.createMapFromQRCode(otpAuthUrl, player);

            for (int i = 0; i < player.getInventory().getSize() - 5; i++) player.getInventory().setItem(i, mapItem);

            player.sendMessage(MessageKeys.TOTP_WELCOME.getMessage());
        } else player.sendMessage(MessageKeys.TOTP_ALREADY_REGISTERED.getMessage());
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        final var player = event.getPlayer();

        authenticatedPlayers.remove(player.getName());
        MenuController.menuMap.remove(player);
    }
}
