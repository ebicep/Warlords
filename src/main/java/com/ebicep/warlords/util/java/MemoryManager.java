package com.ebicep.warlords.util.java;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.debugcommands.ingame.UnstuckCommand;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameManager;
import com.ebicep.warlords.guilds.GuildExperienceUtils;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.quests.Quests;
import com.ebicep.warlords.pve.weapons.menu.WeaponManagerMenu;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class MemoryManager implements Listener {

    public static final HashMap<UUID, Instant> PLAYER_LOGOUT_TIMES = new HashMap<>();

    public static void init() {
        new BukkitRunnable() {

            @Override
            public void run() {
                Set<UUID> toRemove = new HashSet<>();
                PLAYER_LOGOUT_TIMES.forEach((uuid, instant) -> {
                    if (instant.isBefore(Instant.now().minus(15, ChronoUnit.MINUTES))) {
                        toRemove.add(uuid);
                    }
                });
                Warlords.getGameManager().getGames().stream()
                        .map(GameManager.GameHolder::getGame)
                        .filter(Objects::nonNull)
                        .flatMap(Game::offlinePlayersWithoutSpectators)
                        .map(Map.Entry::getKey)
                        .filter(Objects::nonNull)
                        .map(OfflinePlayer::getUniqueId)
                        .forEach(toRemove::remove);

                toRemove.forEach(uuid -> {
                    PLAYER_LOGOUT_TIMES.remove(uuid);
                    Warlords.SPAWN_POINTS.remove(uuid);
                    Quests.CACHED_PLAYER_QUESTS.remove(uuid);
                    StatsLeaderboardManager.PLAYER_LEADERBOARD_INFOS.remove(uuid);
                    ExperienceManager.CACHED_PLAYER_EXP_SUMMARY.remove(uuid);
                    GuildExperienceUtils.CACHED_PLAYER_EXP_SUMMARY.remove(uuid);
                    Currencies.CACHED_PLAYER_COIN_STATS.remove(uuid);
                    WeaponManagerMenu.PLAYER_MENU_SETTINGS.remove(uuid);
                    UnstuckCommand.STUCK_COOLDOWNS.remove(uuid);
//                    ChatUtils.MessageType.WARLORDS.sendMessage("Removed " + uuid + " from static maps");
                });
            }
        }.runTaskTimer(Warlords.getInstance(), 20 * 5, 20);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PLAYER_LOGOUT_TIMES.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PLAYER_LOGOUT_TIMES.put(event.getPlayer().getUniqueId(), Instant.now());
    }

}
