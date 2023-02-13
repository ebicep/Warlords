package com.ebicep.warlords.game.option.freeze;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GameFreezeWhenOfflineOption implements Option {

    private static final String FROZEN_MESSAGE = ChatColor.YELLOW + "Missing player detected!";
    public static boolean enabled = true;

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game, true) {

            final HashMap<UUID, Integer> offlineDuration = new HashMap<>();
            final HashMap<UUID, Integer> leaveCheckDuration = new HashMap<>();
            int cooldown = 0;

            @Override
            public void run() {
                if (game.getState() instanceof EndState) {
                    cancel();
                }
                if (!enabled) {
                    return;
                }
                if (cooldown > 0) {
                    cooldown--;
                    return;
                }

                boolean anyOffline = game.offlineWarlordsPlayersWithoutSpectators().anyMatch(e -> !e.getKey().isOnline());

                if (game.isFrozen()) {
                    if (!anyOffline && game.getFrozenCauses().stream().allMatch(s -> s.equals(FROZEN_MESSAGE))) {
                        GameFreezeOption.resumeGame(game);
                        cooldown = 20; //5 second cooldown + 5 seconds for resume delay
                    }
                } else {
                    if (anyOffline) {
                        List<Map.Entry<OfflinePlayer, Team>> players = game.offlinePlayersWithoutSpectators()
                                                                           .filter(offlinePlayerTeamEntry -> !offlinePlayerTeamEntry.getKey().isOnline())
                                                                           .toList();
                        for (Map.Entry<OfflinePlayer, Team> player : players) {
                            offlineDuration.merge(player.getKey().getUniqueId(), 1, Integer::sum);
                            if (offlineDuration.get(player.getKey().getUniqueId()) > leaveCheckDuration.getOrDefault(player.getKey().getUniqueId(), 4)) {
                                leaveCheckDuration.put(player.getKey().getUniqueId(), leaveCheckDuration.getOrDefault(player.getKey().getUniqueId(), 4) + 4);
                                game.addFrozenCause(FROZEN_MESSAGE);

                                ChatChannels.sendDebugMessage(
                                        (CommandIssuer) null,
                                        ChatColor.GREEN + "Leave Cooldown of " + ChatColor.AQUA + player.getKey().getName() +
                                                ChatColor.GREEN + " increased to " + (leaveCheckDuration.get(player.getKey().getUniqueId()) / 2) + " seconds",
                                        true
                                );
                                break;
                            }
                        }
                    } else {
                        offlineDuration.clear();
                    }
                }
            }

        }.runTaskTimer(0, 10);
    }
}
