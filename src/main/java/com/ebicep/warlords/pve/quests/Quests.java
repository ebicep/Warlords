package com.ebicep.warlords.pve.quests;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.PlayerStatisticsMinute;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.DifficultyIndex;
import org.bukkit.ChatColor;

import java.util.*;

public enum Quests {

    DAILY_300_KA("DAILY_300_KA",
            "Get 300 Kills/Assists in 1 game",
            PlayersCollections.DAILY,
            new LinkedHashMap<>() {{
                put(Currencies.SYNTHETIC_SHARD, 30L);
                put(Currencies.COIN, 7500L);
            }}
    ) {
        @Override
        public boolean checkReward(WaveDefenseOption waveDefenseOption, WarlordsPlayer warlordsPlayer, DatabasePlayer databasePlayer) {
            PlayerStatisticsMinute.Entry total = warlordsPlayer.getMinuteStats().total();
            return total.getKills() + total.getAssists() >= 300;
        }
    },
    DAILY_2_PLAYS("DAILY_2_PLAYS",
            "Play 2 games",
            PlayersCollections.DAILY,
            new LinkedHashMap<>() {{
                put(Currencies.SYNTHETIC_SHARD, 30L);
                put(Currencies.COIN, 7500L);
            }}
    ) {
        @Override
        public boolean checkReward(WaveDefenseOption waveDefenseOption, WarlordsPlayer warlordsPlayer, DatabasePlayer databasePlayer) {
            System.out.println("PLAYS: " + databasePlayer.getPlays());
            return databasePlayer.getPveStats().getPlays() + 1 >= 2;
        }
    },
    DAILY_WIN("DAILY_WIN",
            "Win a game",
            PlayersCollections.DAILY,
            new LinkedHashMap<>() {{
                put(Currencies.SYNTHETIC_SHARD, 50L);
                put(Currencies.COIN, 15000L);
            }}
    ) {
        @Override
        public boolean checkReward(WaveDefenseOption waveDefenseOption, WarlordsPlayer warlordsPlayer, DatabasePlayer databasePlayer) {
            System.out.println("WINS: " + databasePlayer.getWins());
            return databasePlayer.getPveStats().getWins() + 1 >= 1;
        }
    },

    WEEKLY_20_PLAYS("WEEKLY_20_PLAYS",
            "Play 20 games",
            PlayersCollections.WEEKLY,
            new LinkedHashMap<>() {{
                put(Currencies.SYNTHETIC_SHARD, 300L);
                put(Currencies.COIN, 50000L);
            }}
    ) {
        @Override
        public boolean checkReward(WaveDefenseOption waveDefenseOption, WarlordsPlayer warlordsPlayer, DatabasePlayer databasePlayer) {
            System.out.println("PLAYS: " + databasePlayer.getPlays());
            return databasePlayer.getPveStats().getPlays() + 1 >= 20;
        }
    },
    WEEKLY_30_ENDLESS("WEEKLY_30_ENDLESS",
            "Reach Wave 30 in a game of Endless",
            PlayersCollections.WEEKLY,
            new LinkedHashMap<>() {{
                put(Currencies.SYNTHETIC_SHARD, 150L);
                put(Currencies.COIN, 25000L);
            }}
    ) {
        @Override
        public boolean checkReward(WaveDefenseOption waveDefenseOption, WarlordsPlayer warlordsPlayer, DatabasePlayer databasePlayer) {
            return waveDefenseOption.getDifficulty() == DifficultyIndex.ENDLESS && waveDefenseOption.getWavesCleared() >= 30;
        }
    },

    ;

    public static final Quests[] VALUES = values();
    public static final HashMap<UUID, List<Quests>> CACHED_PLAYER_QUESTS = new HashMap<>();

    public final String name;
    public final String description;
    public final PlayersCollections time;
    public final LinkedHashMap<Currencies, Long> rewards;


    Quests(String name, String description, PlayersCollections time, LinkedHashMap<Currencies, Long> rewards) {
        this.name = name;
        this.description = description;
        this.time = time;
        this.rewards = rewards;
    }

    public static List<Quests> getQuestsFromGameStats(WarlordsPlayer warlordsPlayer, WaveDefenseOption waveDefenseOption, boolean recalculate) {
        if (!recalculate && CACHED_PLAYER_QUESTS.containsKey(warlordsPlayer.getUuid()) && CACHED_PLAYER_QUESTS.get(
                warlordsPlayer.getUuid()) != null) {
            return CACHED_PLAYER_QUESTS.get(warlordsPlayer.getUuid());
        }
        List<Quests> questsCompleted = new ArrayList<>();

        for (Quests quest : VALUES) {
            DatabaseManager.getPlayer(warlordsPlayer.getUuid(), quest.time, databasePlayer -> {
                if (databasePlayer.getPveStats().getQuestsCompleted().containsKey(quest)) {
                    return;
                }
                if (quest.checkReward(waveDefenseOption, warlordsPlayer, databasePlayer)) {
                    questsCompleted.add(quest);
                }
            });
        }

        CACHED_PLAYER_QUESTS.put(warlordsPlayer.getUuid(), questsCompleted);
        return questsCompleted;
    }

    public abstract boolean checkReward(
            WaveDefenseOption waveDefenseOption,
            WarlordsPlayer warlordsPlayer,
            DatabasePlayer databasePlayer
    );

    public String getHoverText() {
        StringBuilder hoverText = new StringBuilder(ChatColor.GREEN + description + "\n");
        rewards.forEach((currencies, aLong) -> hoverText.append(ChatColor.GRAY).append(" - ").append(currencies.getCostColoredName(aLong)).append("\n"));
        return hoverText.toString();
    }

}
