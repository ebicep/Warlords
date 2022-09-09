package com.ebicep.warlords.game.option.wavedefense;

import com.ebicep.warlords.pve.weapons.AbstractWeapon;

import java.util.*;

public class WaveDefenseStats {
    public static final LinkedHashMap<String, Long> BOSS_COIN_VALUES = new LinkedHashMap<>() {{
        put("Boltaro", 200L);
        put("Ghoulcaller", 300L);
        put("Narmer", 500L);
        put("Physira", 400L);
        put("Mithra", 400L);
        put("Zenith", 1500L);
    }};
    public static final long[] COINS_PER_5_WAVES = new long[]{50, 100, 150, 200, 300};
    private final HashMap<String, Long> bossesKilled = new HashMap<>();
    private final LinkedHashMap<String, Long> cachedBaseCoinSummary = new LinkedHashMap<>();
    private final HashMap<UUID, List<AbstractWeapon>> playerWeaponsFound = new HashMap<>();

    public void cacheBaseCoinSummary(WaveDefenseOption waveDefenseOption) {
        cachedBaseCoinSummary.clear();
        cachedBaseCoinSummary.put("Waves Cleared", 0L);
        cachedBaseCoinSummary.put("Bosses Killed", 0L);

        for (int i = 1; i <= waveDefenseOption.getWavesCleared(); i++) {
            if ((i - 1) / 5 >= WaveDefenseStats.COINS_PER_5_WAVES.length) {
                break;
            }
            cachedBaseCoinSummary.merge("Waves Cleared", WaveDefenseStats.COINS_PER_5_WAVES[(i - 1) / 5], Long::sum);
        }
        for (Map.Entry<String, Long> stringLongEntry : WaveDefenseStats.BOSS_COIN_VALUES.entrySet()) {
            if (bossesKilled.containsKey(stringLongEntry.getKey())) {
                cachedBaseCoinSummary.merge("Bosses Killed",
                        bossesKilled.get(stringLongEntry.getKey()) * stringLongEntry.getValue(),
                        Long::sum
                );
            }
        }
    }

    public LinkedHashMap<String, Long> getCachedBaseCoinSummary() {
        return cachedBaseCoinSummary;
    }

    public HashMap<String, Long> getBossesKilled() {
        return bossesKilled;
    }

    public HashMap<UUID, List<AbstractWeapon>> getPlayerWeaponsFound() {
        return playerWeaponsFound;
    }
}
