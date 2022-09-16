package com.ebicep.warlords.guilds;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

public class GuildExperienceUtils {

    public static final long EXP_PER_WAVE = 4;
    public static final HashMap<Integer, Long> LEVEL_EXP_COST = new HashMap<>();
    public static final HashMap<Integer, Long> LEVEL_TO_EXP = new HashMap<>();
    private static final HashMap<UUID, LinkedHashMap<String, Long>> CACHED_PLAYER_EXP_SUMMARY = new HashMap<>();

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

    public static LinkedHashMap<String, Long> getExpFromWaveDefense(WarlordsEntity warlordsPlayer, boolean recalculate) {
        if (!recalculate &&
                CACHED_PLAYER_EXP_SUMMARY.containsKey(warlordsPlayer.getUuid()) &&
                CACHED_PLAYER_EXP_SUMMARY.get(warlordsPlayer.getUuid()) != null
        ) {
            return CACHED_PLAYER_EXP_SUMMARY.get(warlordsPlayer.getUuid());
        }

        LinkedHashMap<String, Long> expSummary = new LinkedHashMap<>();

        for (Option option : warlordsPlayer.getGame().getOptions()) {
            if (option instanceof WaveDefenseOption) {
                if (DatabaseManager.guildService == null) {
                    break;
                }

                WaveDefenseOption waveDefenseOption = (WaveDefenseOption) option;
                int wavesCleared = waveDefenseOption.getWavesCleared();
                if (wavesCleared == 0) {
                    break;
                }

                Player player = Bukkit.getPlayer(warlordsPlayer.getUuid());
                if (player != null) {
                    Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player);
                    if (guildPlayerPair != null) {
                        expSummary.put("Waves Cleared", (long) wavesCleared * waveDefenseOption.getDifficulty().getWaveGuildExperienceMultiplier());
                        if (wavesCleared == 25) {
                            if (waveDefenseOption.getDifficulty() == DifficultyIndex.NORMAL) {
                                expSummary.put("Wave 25 Clear Bonus", 200L);
                            } else if (waveDefenseOption.getDifficulty() == DifficultyIndex.HARD) {
                                expSummary.put("Wave 25 Clear Bonus", 500L);
                            }
                        }
                        guildPlayerPair.getA().queueUpdate();
                    }
                }

                break;
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
