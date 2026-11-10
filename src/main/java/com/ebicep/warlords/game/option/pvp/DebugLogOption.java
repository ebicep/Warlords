package com.ebicep.warlords.game.option.pvp;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import net.kyori.adventure.text.Component;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.*;

public class DebugLogOption implements Option {

    public static final Map<String, DebugLog> CACHED_DEBUG_LOG = new HashMap<>();

    @Override
    public void onGameEnding(@Nonnull Game game) {
        UUID gameId = game.getGameId();
        game.warlordsPlayers().forEach(warlordsPlayer -> CACHED_DEBUG_LOG.put(warlordsPlayer.getName().toLowerCase(), new DebugLog(
                        gameId,
                        warlordsPlayer.getDebugMessageLog()
                )
        ));
        new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<Map.Entry<String, DebugLog>> iterator = CACHED_DEBUG_LOG.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, DebugLog> entry = iterator.next();
                    DebugLog debugLog = entry.getValue();
                    if (Objects.equals(debugLog.gameID(), gameId)) {
                        iterator.remove();
                    }
                }
            }
        }.runTaskLater(Warlords.getInstance(), 20 * 60 * 30);
    }

    public record DebugLog(UUID gameID, List<Component> debugLog) {
    }

}

