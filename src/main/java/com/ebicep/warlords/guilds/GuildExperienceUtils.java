package com.ebicep.warlords.guilds;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.game.option.ExperienceGainOption;
import com.ebicep.warlords.game.option.RecordTimeElapsedOption;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.onslaught.OnslaughtOption;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.Pair;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

public class GuildExperienceUtils {

    public static final HashMap<Integer, Long> LEVEL_EXP_COST = new HashMap<>();
    public static final HashMap<Integer, Long> LEVEL_TO_EXP = new HashMap<>();
    public static final HashMap<UUID, LinkedHashMap<String, Long>> CACHED_PLAYER_EXP_SUMMARY = new HashMap<>();

    static {
        //level 1-5 5000 per level
        for (int i = 1; i <= 5; i++) {
            LEVEL_EXP_COST.put(i, (long) (5000 * i));
        }
        //level 6-10 start at 50000 and double from previous level
        for (int i = 6; i <= 10; i++) {
            LEVEL_EXP_COST.put(i, LEVEL_EXP_COST.get(i - 1) * 2);
        }
        //level 11-15 1600000 each level
        for (int i = 11; i <= 15; i++) {
            LEVEL_EXP_COST.put(i, 1_600_000L);
        }

        //level to exp, adding all previous levels exp cost to get to current level
        for (int i = 1; i <= 15; i++) {
            long totalExpForLevel = 0;
            for (int j = 1; j < i; j++) {
                totalExpForLevel += LEVEL_EXP_COST.get(j);
            }
            LEVEL_TO_EXP.put(i, totalExpForLevel);
        }
    }

    public static LinkedHashMap<String, Long> getExpFromPvE(WarlordsEntity warlordsPlayer, PveOption pveOption, boolean recalculate) {
        if (!recalculate &&
                CACHED_PLAYER_EXP_SUMMARY.containsKey(warlordsPlayer.getUuid()) &&
                CACHED_PLAYER_EXP_SUMMARY.get(warlordsPlayer.getUuid()) != null
        ) {
            return CACHED_PLAYER_EXP_SUMMARY.get(warlordsPlayer.getUuid());
        }

        LinkedHashMap<String, Long> expSummary = new LinkedHashMap<>();

        if (DatabaseManager.guildService == null) {
            return expSummary;
        }

        ExperienceGainOption experienceGainOption = warlordsPlayer
                .getGame()
                .getOptions()
                .stream()
                .filter(ExperienceGainOption.class::isInstance)
                .map(ExperienceGainOption.class::cast)
                .findAny()
                .orElse(null);
        if (experienceGainOption == null) {
            return expSummary;
        }

        if (experienceGainOption.getGuildExpPer() != 0) {
            if (pveOption instanceof WaveDefenseOption waveDefenseOption) {
                expSummary.put("Waves Cleared", experienceGainOption.getGuildExpPer() * waveDefenseOption.getWavesCleared());
            } else if (pveOption instanceof OnslaughtOption onslaughtOption) {
                expSummary.put("Minutes Elapsed", experienceGainOption.getGuildExpPer() * onslaughtOption.getTicksElapsed() / 20 / 60);
            }
        }
        if (experienceGainOption.getGuildExpGameWinBonus() != 0) {
            if (pveOption instanceof WaveDefenseOption waveDefenseOption) {
                int maxWaves = waveDefenseOption.getDifficulty().getMaxWaves();
                int wavesCleared = Math.min(waveDefenseOption.getWavesCleared(), maxWaves);
                if (experienceGainOption.getGuildExpGameWinBonus() != 0 && wavesCleared == maxWaves) {
                    expSummary.put("Wave " + maxWaves + " Clear Bonus", experienceGainOption.getGuildExpGameWinBonus());
                }
            }
        }
        if (experienceGainOption.getGuildExpPerXSec() != null) {
            RecordTimeElapsedOption recordTimeElapsedOption = pveOption
                    .getGame()
                    .getOptions()
                    .stream()
                    .filter(option -> option instanceof RecordTimeElapsedOption)
                    .map(RecordTimeElapsedOption.class::cast)
                    .findAny()
                    .orElse(null);
            if (recordTimeElapsedOption != null) {
                int secondsElapsed = recordTimeElapsedOption.getTicksElapsed() / 20;
                Pair<Long, Integer> guildExpPerXSec = experienceGainOption.getGuildExpPerXSec();
                if (guildExpPerXSec != null) {
                    expSummary.put("Time Lived", secondsElapsed / guildExpPerXSec.getB() * guildExpPerXSec.getA());
                }
            }
        }

        CACHED_PLAYER_EXP_SUMMARY.put(warlordsPlayer.getUuid(), expSummary);
        return expSummary;
    }

    public static int getLevelFromExp(long exp) {
        for (int i = 1; i <= 15; i++) {
            if (exp < LEVEL_TO_EXP.get(i)) {
                return i - 1;
            }
        }
        return 15;
    }


}
