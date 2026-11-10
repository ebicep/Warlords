package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbilityStats;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.TracksAbilityStats;
import com.ebicep.warlords.database.repositories.player.pojos.cache.CachedMultiPvEWaveDefenseStats;
import com.ebicep.warlords.database.repositories.player.pojos.cache.PushedStatTotals;
import com.ebicep.warlords.database.repositories.player.pojos.cache.StatPushUp;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.option.pve.onslaught.PouchReward;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

public class DatabasePlayerWaveDefenseStats implements CachedMultiPvEWaveDefenseStats, TracksAbilityStats {

    @Transient
    private final PushedStatTotals pushedStats = new PushedStatTotals();

    @Field("easy_stats")
    private DatabasePlayerPvEWaveDefenseDifficultyStats easyStats = new DatabasePlayerPvEWaveDefenseDifficultyStats();
    @Field("normal_stats")
    private DatabasePlayerPvEWaveDefenseDifficultyStats normalStats = new DatabasePlayerPvEWaveDefenseDifficultyStats();
    @Field("hard_stats")
    private DatabasePlayerPvEWaveDefenseDifficultyStats hardStats = new DatabasePlayerPvEWaveDefenseDifficultyStats();
    @Field("extreme_stats")
    private DatabasePlayerPvEWaveDefenseDifficultyStats extremeStats = new DatabasePlayerPvEWaveDefenseDifficultyStats();
    @Field("endless_stats")
    private DatabasePlayerPvEWaveDefenseDifficultyStats endlessStats = new DatabasePlayerPvEWaveDefenseDifficultyStats();
    @Field("ability_stats")
    private Map<Ability<?>, AbstractAbilityStats<?, ?>> abilityStats = new HashMap<>();

    @Override
    public Map<Ability<?>, AbstractAbilityStats<?, ?>> getAbilityStats() {
        return abilityStats;
    }

    public DatabasePlayerWaveDefenseStats() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEWaveDefense databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEWaveDefense gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        updateModeStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
    }

    /**
     * @return true if a difficulty leaf was updated and local push-up was applied
     */
    public boolean updateModeStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEWaveDefense databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEWaveDefense gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        Map<Spendable, Long> ascendantPouch = gamePlayer.getAscendantPouch();
        if (multiplier > 0) {
            LinkedHashMap<Spendable, Long> sortedAscendantPouch = new LinkedHashMap<>();
            ascendantPouch.entrySet()
                    .stream()
                    .sorted((o1, o2) -> Long.compare(o2.getValue(), o1.getValue()))
                    .forEachOrdered(spendableLongEntry -> sortedAscendantPouch.put(spendableLongEntry.getKey(), spendableLongEntry.getValue()));
            if (!sortedAscendantPouch.isEmpty()) {
                databasePlayer.getPveStats().getPouchRewards().add(new PouchReward(sortedAscendantPouch, PouchReward.PouchType.ASCENDANT));
            }
        } else {
            ascendantPouch.forEach((spendable, amount) -> spendable.addToPlayer(databasePlayer, amount * multiplier));
        }

        DatabasePlayerPvEWaveDefenseDifficultyStats difficultyStats = getDifficultyStats(databaseGame.getDifficulty());
        if (difficultyStats == null) {
            ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("Error: Difficulty stats is null", NamedTextColor.GREEN));
            return false;
        }
        boolean updated = difficultyStats.updateModeStats(databasePlayer, databaseGame, gamePlayer, result, multiplier, playersCollection);
        updateAbilityStats(gamePlayer, multiplier);
        if (updated) {
            StatPushUp.applyPvE(pushedStats, gamePlayer, result, databaseGame, multiplier);
        }
        return updated;
    }

    public DatabasePlayerPvEWaveDefenseDifficultyStats getDifficultyStats(DifficultyIndex difficultyIndex) {
        return switch (difficultyIndex) {
            case EASY -> getEasyStats();
            case NORMAL -> getNormalStats();
            case HARD -> getHardStats();
            case EXTREME -> getExtremeStats();
            case ENDLESS -> getEndlessStats();
            default -> null;
        };
    }


    public DatabasePlayerPvEWaveDefenseDifficultyStats getEasyStats() {
        return easyStats;
    }

    public DatabasePlayerPvEWaveDefenseDifficultyStats getNormalStats() {
        return normalStats;
    }

    public DatabasePlayerPvEWaveDefenseDifficultyStats getHardStats() {
        return hardStats;
    }

    public DatabasePlayerPvEWaveDefenseDifficultyStats getExtremeStats() {
        return extremeStats;
    }

    public DatabasePlayerPvEWaveDefenseDifficultyStats getEndlessStats() {
        return endlessStats;
    }

    @Override
    public Collection<WaveDefenseStatsWarlordsClasses> getStats() {
        return Stream.of(easyStats, normalStats, hardStats, extremeStats, endlessStats)
                     .flatMap(stats -> stats.getStats().stream())
                     .toList();
    }

    @Override
    public PushedStatTotals pushedStats() {
        return pushedStats;
    }

    @Override
    public int treeWalkKills() {
        return CachedMultiPvEWaveDefenseStats.super.treeWalkKills();
    }

    @Override
    public Map<String, Long> treeWalkMobKills() {
        return CachedMultiPvEWaveDefenseStats.super.treeWalkMobKills();
    }

}
