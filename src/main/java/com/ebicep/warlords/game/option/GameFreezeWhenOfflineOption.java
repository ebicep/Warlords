package com.ebicep.warlords.game.option;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public class GameFreezeWhenOfflineOption implements Option {

    public static boolean enabled = true;
    private static final String FROZEN_MESSAGE = ChatColor.YELLOW + "Missing player detected!";

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game, true) {
            @Override
            public void run() {
                if (!enabled) return;

                boolean anyOffline = game.offlinePlayersWithoutSpectators().anyMatch(e -> !e.getKey().isOnline());

                if (game.isFrozen()) {
                    if (!anyOffline && game.getFrozenCauses().stream().allMatch(s -> s.equals(FROZEN_MESSAGE))) {
                        GameFreezeOption.resumeGame(game);
                    }
                } else {
                    if (anyOffline) {
                        game.addFrozenCause(FROZEN_MESSAGE);
                    }
                }
            }

        }.runTaskTimer(0, 20);
    }
}
