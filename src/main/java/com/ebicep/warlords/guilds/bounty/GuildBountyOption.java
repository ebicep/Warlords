package com.ebicep.warlords.guilds.bounty;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.maps.RaidOne;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.onslaught.OnslaughtOption;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.mobs.tiers.PlayerMob;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class GuildBountyOption implements Option, Listener {

    private Game game;

    @Override
    public boolean isEnabled(@Nonnull Game game) {
        GameMode gameMode = game.getGameMode();
        return !game.getAddons().contains(GameAddon.CUSTOM_GAME)
                && gameMode != GameMode.LOBBY
                && gameMode != GameMode.DEBUG
                && gameMode != GameMode.PVE_DEBUG
                && gameMode != GameMode.TUTORIAL;
    }

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        game.registerEvents(this);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMobKill(WarlordsDeathEvent event) {
        if (!(event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC)
                || !(event.getKiller() instanceof WarlordsPlayer warlordsPlayer)
                || warlordsNPC.getMob() == null
                || warlordsNPC.getMob() instanceof PlayerMob) {
            return;
        }
        Pair<Guild, ?> guildPair = GuildManager.getGuildAndGuildPlayerFromPlayer(warlordsPlayer.getUuid());
        if (guildPair == null) {
            return;
        }

        Guild guild = guildPair.getA();
        GuildBountyManager.addProgress(guild, GuildBounty.KILL_MOBS, 1);

        String mobClassName = warlordsNPC.getMob().getClass().getName().toLowerCase(Locale.ROOT);
        if (mobClassName.contains(".skeleton") || mobClassName.contains(".witherskeleton") || mobClassName.contains(".stray")) {
            GuildBountyManager.addProgress(guild, GuildBounty.KILL_SKELETONS, 1);
        }
        if (mobClassName.contains(".zombie") || mobClassName.contains(".husk")) {
            GuildBountyManager.addProgress(guild, GuildBounty.KILL_ZOMBIES, 1);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGameEnd(WarlordsGameTriggerWinEvent event) {
        GuildParticipation participation = getGuildParticipation();
        if (participation.playerCounts().isEmpty()) {
            return;
        }

        boolean successfulCompletion = event.getDeclaredWinner() == Team.BLUE;
        for (Map.Entry<Guild, Integer> entry : participation.playerCounts().entrySet()) {
            Guild guild = entry.getKey();
            int guildMembers = entry.getValue();
            int largestGuildParty = participation.largestPartyCounts().getOrDefault(guild, 0);

            GuildBountyManager.addProgress(guild, GuildBounty.PLAY_GAMES, guildMembers);

            if (successfulCompletion && guildMembers >= 4 && BountyUtils.waveDefenseMatchesDifficulty(game, DifficultyIndex.EXTREME)) {
                GuildBountyManager.addProgress(guild, GuildBounty.COMPLETE_EXTREME, 1);
            }
            if (successfulCompletion && game.getGameMode() == GameMode.ANOMALY && largestGuildParty >= 2) {
                GuildBountyManager.addProgress(guild, GuildBounty.COMPLETE_ANOMALIES, 1);
            }
            if (successfulCompletion && game.getMap() instanceof RaidOne && largestGuildParty >= 4) {
                GuildBountyManager.addProgress(guild, GuildBounty.COMPLETE_REGNUM, 1);
            }
            if (largestGuildParty >= 4) {
                BountyUtils.getOptionFromGame(game, WaveDefenseOption.class).ifPresent(waveDefenseOption -> {
                    if (waveDefenseOption.getDifficulty() == DifficultyIndex.ENDLESS) {
                        GuildBountyManager.updateMaxProgress(guild, GuildBounty.REACH_ENDLESS_WAVE_100, waveDefenseOption.getWavesCleared());
                    }
                });
            }
            if (largestGuildParty >= 3) {
                BountyUtils.getOptionFromGame(game, OnslaughtOption.class).ifPresent(onslaughtOption ->
                        GuildBountyManager.updateMaxProgress(guild, GuildBounty.REACH_ONSLAUGHT_60_MINUTES, onslaughtOption.getTicksElapsed() / 20L)
                );
            }
        }
    }

    private GuildParticipation getGuildParticipation() {
        Map<Guild, Integer> playerCounts = new HashMap<>();
        Map<Guild, Map<Party, Integer>> partyCounts = new HashMap<>();

        game.warlordsPlayersWithoutSpectators().forEach(entry -> {
            UUID uuid = entry.getKey();
            Pair<Guild, ?> guildPair = GuildManager.getGuildAndGuildPlayerFromPlayer(uuid);
            if (guildPair == null) {
                return;
            }

            Guild guild = guildPair.getA();
            playerCounts.merge(guild, 1, Integer::sum);

            Pair<Party, ?> partyPair = PartyManager.getPartyAndPartyPlayerFromAny(uuid);
            if (partyPair != null) {
                partyCounts.computeIfAbsent(guild, key -> new IdentityHashMap<>()).merge(partyPair.getA(), 1, Integer::sum);
            }
        });

        Map<Guild, Integer> largestPartyCounts = new HashMap<>();
        partyCounts.forEach((guild, counts) -> largestPartyCounts.put(guild, counts.values().stream().mapToInt(Integer::intValue).max().orElse(0)));
        return new GuildParticipation(playerCounts, largestPartyCounts);
    }

    private record GuildParticipation(Map<Guild, Integer> playerCounts, Map<Guild, Integer> largestPartyCounts) {
    }
}
