package com.ebicep.warlords.pve.quests;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.DifficultyIndex;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public enum Quests {

    DAILY_300_KA("Get 300 Kills/Assists in 1 game",
            PlayersCollections.DAILY,
            new LinkedHashMap<>() {{
                put(Currencies.SYNTHETIC_SHARD, 30L);
                put(Currencies.COIN, 7500L);
            }}
    ) {
        @Override
        public boolean checkReward(WaveDefenseOption waveDefenseOption, WarlordsPlayer warlordsPlayer, DatabasePlayer databasePlayer) {
            return databasePlayer.getPveStats().getKills() + databasePlayer.getPveStats().getAssists() >= 300;
        }
    },
    DAILY_2_PLAYS("Play 2 games",
            PlayersCollections.DAILY,
            new LinkedHashMap<>() {{
                put(Currencies.SYNTHETIC_SHARD, 30L);
                put(Currencies.COIN, 7500L);
            }}
    ) {
        @Override
        public boolean checkReward(WaveDefenseOption waveDefenseOption, WarlordsPlayer warlordsPlayer, DatabasePlayer databasePlayer) {
            return databasePlayer.getPveStats().getPlays() >= 2;
        }
    },
    DAILY_WIN("Win a game",
            PlayersCollections.DAILY,
            new LinkedHashMap<>() {{
                put(Currencies.SYNTHETIC_SHARD, 50L);
                put(Currencies.COIN, 15000L);
            }}
    ) {
        @Override
        public boolean checkReward(WaveDefenseOption waveDefenseOption, WarlordsPlayer warlordsPlayer, DatabasePlayer databasePlayer) {
            return databasePlayer.getPveStats().getWins() >= 1;
        }
    },

    WEEKLY_20_PLAYS("Play 20 games",
            PlayersCollections.WEEKLY,
            new LinkedHashMap<>() {{
                put(Currencies.SYNTHETIC_SHARD, 300L);
                put(Currencies.COIN, 50000L);
            }}
    ) {
        @Override
        public boolean checkReward(WaveDefenseOption waveDefenseOption, WarlordsPlayer warlordsPlayer, DatabasePlayer databasePlayer) {
            return databasePlayer.getPveStats().getPlays() >= 20;
        }
    },
    WEEKLY_30_ENDLESS("Reach Wave 30 in a game of Endless",
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
    public final PlayersCollections time;
    public final LinkedHashMap<Currencies, Long> rewards;


    Quests(String name, PlayersCollections time, LinkedHashMap<Currencies, Long> rewards) {
        this.name = name;
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

        return questsCompleted;
    }

    public abstract boolean checkReward(
            WaveDefenseOption waveDefenseOption,
            WarlordsPlayer warlordsPlayer,
            DatabasePlayer databasePlayer
    );
}
