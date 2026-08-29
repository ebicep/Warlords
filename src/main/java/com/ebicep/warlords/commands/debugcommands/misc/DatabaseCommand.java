package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.cache.StatPushUp;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.DatabasePlayerPvEWaveDefenseDifficultyStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.DatabasePlayerWaveDefenseStats;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.ConfigUtil;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@CommandAlias("database")
@CommandPermission("group.adminisrator")
public class DatabaseCommand extends BaseCommand {

    @Subcommand("removefromcache")
    public void removeFromCache(CommandIssuer issuer, String uuid) {
        for (PlayersCollections value : PlayersCollections.ACTIVE_COLLECTIONS) {
            ConcurrentHashMap<UUID, DatabasePlayer> cache = DatabaseManager.CACHED_PLAYERS.get(value);
            if (cache != null) {
                cache.remove(UUID.fromString(uuid));
                ChatChannels.sendDebugMessage(issuer, "Removed " + uuid + " from " + value.name() + " cache");
            }
        }
    }

    @Subcommand("printcache")
    public void printCache(CommandIssuer issuer) {
        for (PlayersCollections value : PlayersCollections.ACTIVE_COLLECTIONS) {
            ConcurrentHashMap<UUID, DatabasePlayer> cache = DatabaseManager.CACHED_PLAYERS.get(value);
            if (cache != null) {
                ChatChannels.sendDebugMessage(issuer, "Printing " + value.name() + " cache");
                for (UUID uuid : cache.keySet()) {
                    ChatChannels.sendDebugMessage(issuer, uuid.toString() + " - " + cache.get(uuid));
                }
            }
        }
    }

    @Subcommand("reloadconfig")
    public void reloadConfig(CommandIssuer issuer) {
        try {
            if (DatabaseManager.enabled) {
                ConfigManager.loadConfigs(DatabaseManager.warlordsDatabase);
            } else {
                ConfigManager.loadConfigsFromFolder();
            }
            ChatChannels.sendDebugMessage(issuer, "Reloaded database config");
        } catch (Exception e) {
            ChatChannels.sendDebugMessage(issuer, "Failed to reload database config: " + e.getMessage());
            ChatUtils.MessageType.CONFIG.sendErrorMessage(e);
        }
    }

    @Subcommand("reloadconfiglocal")
    public void reloadConfigLocal(CommandIssuer issuer) {
        try {
            ConfigUtil.loadConfigs(Warlords.getInstance());
            ChatChannels.sendDebugMessage(issuer, "Reloaded local config");
        } catch (Exception e) {
            ChatChannels.sendDebugMessage(issuer, "Failed to reload local config: " + e.getMessage());
            ChatUtils.MessageType.CONFIG.sendErrorMessage(e);
        }
    }

    @Subcommand("verifypushcache")
    public void verifyPushCache(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        DatabasePlayerWaveDefenseStats waveDefenseStats = pveStats.getWaveDefenseStats();
        String mismatch = firstPushCacheMismatch(databasePlayer, pveStats, waveDefenseStats);
        if (mismatch == null) {
            ChatChannels.sendDebugMessage(player, "Push cache matches tree walk"
                    + " playerKills=" + databasePlayer.getKills()
                    + " pveKills=" + pveStats.getKills()
                    + " pveMobKillSum=" + pveStats.getTotalMobKills());
        } else {
            ChatChannels.sendDebugMessage(player, "Push cache DIFFERS from tree walk: " + mismatch);
        }
    }

    private static String firstPushCacheMismatch(
            DatabasePlayer databasePlayer,
            DatabasePlayerPvE pveStats,
            DatabasePlayerWaveDefenseStats waveDefenseStats
    ) {
        String mismatch = killsMismatch("player", databasePlayer.getKills(), databasePlayer.treeWalkKills());
        if (mismatch != null) {
            return mismatch;
        }
        mismatch = killsMismatch("pubCtf", databasePlayer.getPubStats().getCtfStats().getKills(),
                databasePlayer.getPubStats().getCtfStats().treeWalkKills());
        if (mismatch != null) {
            return mismatch;
        }
        mismatch = killsMismatch("pubTdm", databasePlayer.getPubStats().getTdmStats().getKills(),
                databasePlayer.getPubStats().getTdmStats().treeWalkKills());
        if (mismatch != null) {
            return mismatch;
        }
        mismatch = killsMismatch("compCtf", databasePlayer.getCompStats().getCtfStats().getKills(),
                databasePlayer.getCompStats().getCtfStats().treeWalkKills());
        if (mismatch != null) {
            return mismatch;
        }
        var tournamentCurrent = databasePlayer.getTournamentStats().getCurrentTournamentStats();
        mismatch = killsMismatch("tournamentCurrent", tournamentCurrent.getKills(), tournamentCurrent.treeWalkKills());
        if (mismatch != null) {
            return mismatch;
        }
        mismatch = killsMismatch("pve", pveStats.getKills(), pveStats.treeWalkKills());
        if (mismatch != null) {
            return mismatch;
        }
        if (!StatPushUp.mapsEqual(pveStats.getMobKills(), pveStats.treeWalkMobKills())) {
            return "pveMobKills";
        }
        mismatch = killsMismatch("waveDefense", waveDefenseStats.getKills(), waveDefenseStats.treeWalkKills());
        if (mismatch != null) {
            return mismatch;
        }
        if (!StatPushUp.mapsEqual(waveDefenseStats.getMobKills(), waveDefenseStats.treeWalkMobKills())) {
            return "waveDefenseMobKills";
        }
        for (var entry : java.util.List.of(
                java.util.Map.entry("easy", waveDefenseStats.getEasyStats()),
                java.util.Map.entry("normal", waveDefenseStats.getNormalStats()),
                java.util.Map.entry("hard", waveDefenseStats.getHardStats()),
                java.util.Map.entry("extreme", waveDefenseStats.getExtremeStats()),
                java.util.Map.entry("endless", waveDefenseStats.getEndlessStats())
        )) {
            DatabasePlayerPvEWaveDefenseDifficultyStats difficultyStats = entry.getValue();
            mismatch = killsMismatch("waveDefense." + entry.getKey(), difficultyStats.getKills(), difficultyStats.treeWalkKills());
            if (mismatch != null) {
                return mismatch;
            }
            if (!StatPushUp.mapsEqual(difficultyStats.getMobKills(), difficultyStats.treeWalkMobKills())) {
                return "waveDefense." + entry.getKey() + "MobKills";
            }
        }
        var eventStats = pveStats.getEventStats();
        mismatch = killsMismatch("eventStats", eventStats.getKills(), eventStats.treeWalkKills());
        if (mismatch != null) {
            return mismatch;
        }
        if (!StatPushUp.mapsEqual(eventStats.getMobKills(), eventStats.treeWalkMobKills())) {
            return "eventStatsMobKills";
        }
        return null;
    }

    private static String killsMismatch(String label, int pushed, int treeWalk) {
        if (pushed == treeWalk) {
            return null;
        }
        return label + " kills " + pushed + "/" + treeWalk;
    }

    @Subcommand("rebuildpushcache")
    public void rebuildPushCache(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        StatPushUp.rebuildSelectedCaches(databasePlayer);
        ChatChannels.sendDebugMessage(player, "Rebuilt selected push-up caches");
    }

}
