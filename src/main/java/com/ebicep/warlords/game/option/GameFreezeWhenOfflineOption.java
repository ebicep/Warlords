package com.ebicep.warlords.game.option;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public class GameFreezeWhenOfflineOption implements Option {

    private static final String FROZEN_MESSAGE = ChatColor.YELLOW + "Missing player detected!";
    public static boolean enabled = true;

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game, true) {

            int cooldown = 0;

            @Override
            public void run() {
                if (!enabled) return;
                if (cooldown > 0) {
                    cooldown--;
                    return;
                }

                boolean anyOffline = game.offlinePlayersWithoutSpectators().anyMatch(e -> !e.getKey().isOnline());

                if (game.isFrozen()) {
                    if (!anyOffline && game.getFrozenCauses().stream().allMatch(s -> s.equals(FROZEN_MESSAGE))) {
                        GameFreezeOption.resumeGame(game);
                        cooldown = 5 + 5; //5 second cooldown + 5 seconds for resume delay
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
