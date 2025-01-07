package net.solostudio.drawlock.listeners;

import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.enums.keys.MessageKeys;
import net.solostudio.drawlock.services.TOTPService;
import net.solostudio.drawlock.utils.DrawLockUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;

@SuppressWarnings("deprecation")
public class TOTPListener implements Listener {
    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        final var player = event.getPlayer();
        final var message = event.getMessage().replace(" ", "");

        event.setCancelled(true);

        final var totpService = DrawLock.getInstance().getTotpService();
        final var secretKey = totpService.getSecretKey(player.getName());

        if (secretKey == null) return;

        try {
            final boolean isAuthorized = DrawLock.getInstance()
                    .getGoogleAuthenticator()
                    .authorize(secretKey, Integer.parseInt(message));

            if (isAuthorized) {
                JoinListener.authenticatedPlayers.add(player.getName());
                DrawLock.getInstance().getScheduler().runTask(() -> {
                    player.sendMessage(MessageKeys.TOTP_SUCCESS.getMessage());
                    player.getInventory().clear();
                    DrawLockUtils.handleLoginOrRegister(player);
                });
            } else player.sendMessage(MessageKeys.TOTP_WRONG.getMessage());
        } catch (NumberFormatException ignored) {
            player.sendMessage(MessageKeys.TOTP_WRONG.getMessage());
        }
    }

    @EventHandler
    public void onMove(final PlayerMoveEvent event) {
        final var player = event.getPlayer();

        if (!JoinListener.authenticatedPlayers.contains(player.getName())) {
            final var from = event.getFrom();
            final var to = event.getTo();

            if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) event.setTo(from);
        }
    }

    @EventHandler
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        final var player = event.getPlayer();

        if (!JoinListener.authenticatedPlayers.contains(player.getName())) event.setCancelled(true);
    }
}
