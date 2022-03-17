package com.ebicep.warlords.game.option;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.ChatColor;

public class GameFreezeWhenOfflineOption implements Option {

    private static final String FROZEN_MESSAGE = ChatColor.YELLOW + "Missing player detected!";
    public static final int UNFREEZE_TIME = 5;
    private static final int FREEZE_TIME = 10;

    private int timer = 0;
    private boolean isFrozen = false;

    @Override
    public void start(Game game) {
        new GameRunnable(game, true) {
            @Override
            public void run() {
                boolean anyOffline = game.offlinePlayersWithoutSpectators().anyMatch(e -> !e.getKey().isOnline());
                if (isFrozen) {
                    if (anyOffline) {
                        timer = 0;
                    } else if (timer >= UNFREEZE_TIME) {
                        game.removeFrozenCause(FROZEN_MESSAGE);
                        isFrozen = false;
                        timer = 0;
                    } else {
                        game.forEachOnlinePlayerWithoutSpectators((p, team) -> {
                            PacketUtils.sendTitle(p, ChatColor.BLUE + "Resuming in... " + ChatColor.GREEN + (UNFREEZE_TIME - timer), "", 0, 40, 0);
                        });
                        timer++;
                    }
                } else {
                    if (!anyOffline) {
                        timer = 0;
                    } else if (timer >= FREEZE_TIME) {
                        game.addFrozenCause(FROZEN_MESSAGE);
                        isFrozen = true;
                        timer = 0;
                    } else {
                        timer++;
                    }
                }
            }

        }.runTaskTimer(0, 20);
    }
}
