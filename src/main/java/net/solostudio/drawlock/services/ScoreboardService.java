package net.solostudio.drawlock.services;

import fr.mrmicky.fastboard.FastBoard;
import lombok.Getter;
import net.solostudio.drawlock.DrawLock;
import net.solostudio.drawlock.enums.keys.ConfigKeys;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreboardService {
    @Getter
    private static final ConcurrentHashMap<Player, FastBoard> boards = new ConcurrentHashMap<>();

    private static void updateScoreboard(@NotNull Player player, @NotNull FastBoard board, boolean isRegistered) {
        if (!isRegistered) {
            board.updateLines(ConfigKeys.REGISTER_SCOREBOARD_LINES.getScoreboardList(player));
            board.updateTitle(ConfigKeys.REGISTER_SCOREBOARD_TITLE.getString());
        } else {
            board.updateLines(ConfigKeys.LOGIN_SCOREBOARD_LINES.getScoreboardList(player));
            board.updateTitle(ConfigKeys.LOGIN_SCOREBOARD_TITLE.getString());
        }
    }

    public static void giveScoreboard(@NotNull Player player, boolean isRegistered) {
        var board = new FastBoard(player);

        getBoards().put(player, board);
        DrawLock.getInstance().getScheduler().runTaskTimer(() -> {
            if (boards.containsKey(player)) updateScoreboard(player, board, isRegistered);
        }, 0, 20);
    }

    public static void removeScoreboard(@NotNull Player player) {
        var board = boards.remove(player);

        if (board != null) board.delete();
    }
}
