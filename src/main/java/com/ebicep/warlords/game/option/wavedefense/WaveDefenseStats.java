package com.ebicep.warlords.game.option.wavedefense;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class WaveDefenseStats {
    public static final LinkedHashMap<String, Long> BOSS_COIN_VALUES = new LinkedHashMap<>() {{
        put("Boltaro", 200L);
        put("Ghoulcaller", 300L);
        put("Narmer", 500L);
        put("Physira", 400L);
        put("Mithra", 400L);
        put("Zenith", 1500L);
    }};
    public static final long[] COINS_PER_5_WAVES = new long[]{
            50,
            100,
            150,
            200,
            300,
            400,
            500,
            600,
            700,
            800,
            900,
            1000,
            1100,
            1200,
            1300,
            1400,
            1500,
            1600,
            1700,
            1800
    };
    private final HashMap<String, Long> bossesKilled = new HashMap<>();
    private final HashMap<UUID, PlayerWaveDefenseStats> playerWaveDefenseStats = new HashMap<>();
    private boolean boostedGame = false;

    public WaveDefenseStats() {
        boostedGame = ThreadLocalRandom.current().nextInt(0, 100) < 5;
    }

    public void cacheBaseCoinSummary(WaveDefenseOption waveDefenseOption) {
        LinkedHashMap<String, Long> cachedBaseCoinSummary = new LinkedHashMap<>();
        cachedBaseCoinSummary.put("Waves Cleared", 0L);
        cachedBaseCoinSummary.put("Bosses Killed", 0L);

        for (int i = 1; i <= waveDefenseOption.getWavesCleared(); i++) {
            if ((i - 1) / 5 >= WaveDefenseStats.COINS_PER_5_WAVES.length) {
                break;
            }
            cachedBaseCoinSummary.merge("Waves Cleared",
                    (long) (WaveDefenseStats.COINS_PER_5_WAVES[(i - 1) / 5] * waveDefenseOption.getDifficulty().getRewardsMultiplier()),
                    Long::sum
            );
        }
        for (Map.Entry<String, Long> stringLongEntry : WaveDefenseStats.BOSS_COIN_VALUES.entrySet()) {
            if (bossesKilled.containsKey(stringLongEntry.getKey())) {
                cachedBaseCoinSummary.merge("Bosses Killed",
                        (long) (bossesKilled.get(stringLongEntry.getKey()) * stringLongEntry.getValue() * waveDefenseOption.getDifficulty()
                                .getRewardsMultiplier()),
                        Long::sum
                );
            }
        }

        waveDefenseOption.getGame()
                .warlordsPlayers()
                .forEach(warlordsPlayer -> {
                    if (waveDefenseOption.getWavesCleared() >= waveDefenseOption.getMaxWave() && waveDefenseOption.getDifficulty() != DifficultyIndex.ENDLESS) {
                        long coinsConverted = warlordsPlayer.getCurrency() / 100;
                        cachedBaseCoinSummary.put("Excess Insignia Converted",
                                Math.min(coinsConverted, waveDefenseOption.getDifficulty().getMaxInsigniaConverted())
                        );
                    }
                    getPlayerWaveDefenseStats(warlordsPlayer.getUuid()).setCachedBaseCoinSummary(cachedBaseCoinSummary);
                });
    }

    public PlayerWaveDefenseStats getPlayerWaveDefenseStats(UUID uuid) {
        return playerWaveDefenseStats.computeIfAbsent(uuid, k -> new PlayerWaveDefenseStats());
    }

    public void storeWeaponFragmentGain(WaveDefenseOption waveDefenseOption) {
        int wavesCleared = waveDefenseOption.getWavesCleared();
        boolean won = waveDefenseOption.getWavesCleared() >= waveDefenseOption.getMaxWave();
        waveDefenseOption.getGame()
                .warlordsPlayers()
                .forEach(warlordsPlayer -> {
                    if (warlordsPlayer.getAbstractWeapon() instanceof AbstractLegendaryWeapon) {
                        UUID uuid = warlordsPlayer.getUuid();
                        DatabaseManager.getPlayer(uuid, databasePlayer -> {
                            long legendFragmentGain = won || waveDefenseOption.getDifficulty() == DifficultyIndex.ENDLESS ?
                                    wavesCleared : (long) (wavesCleared * 0.5);
                            legendFragmentGain += databasePlayer.getSpec(warlordsPlayer.getSpecClass()).getPrestige() * 5L * wavesCleared / 25;
                            legendFragmentGain *= waveDefenseOption.getDifficulty() == DifficultyIndex.EASY ?
                                    .5 : waveDefenseOption.getDifficulty().getRewardsMultiplier();
                            getPlayerWaveDefenseStats(uuid).setLegendFragmentGain(legendFragmentGain);
                        });
                    }
                });
    }

    public HashMap<String, Long> getBossesKilled() {
        return bossesKilled;
    }

    public static class PlayerWaveDefenseStats {
        LinkedHashMap<String, Long> cachedBaseCoinSummary = new LinkedHashMap<>();
        private final List<AbstractWeapon> weaponsFound = new ArrayList<>();
        private final HashMap<Integer, Long> waveDamage = new HashMap<>();
        private long legendFragmentGain = 0;

        public List<AbstractWeapon> getWeaponsFound() {
            return weaponsFound;
        }

        public long getLegendFragmentGain() {
            return legendFragmentGain;
        }

        public void setLegendFragmentGain(long legendFragmentGain) {
            this.legendFragmentGain = legendFragmentGain;
        }

        public LinkedHashMap<String, Long> getCachedBaseCoinSummary() {
            return cachedBaseCoinSummary;
        }

        public void setCachedBaseCoinSummary(LinkedHashMap<String, Long> cachedBaseCoinSummary) {
            this.cachedBaseCoinSummary.clear();
            this.cachedBaseCoinSummary.putAll(cachedBaseCoinSummary);
        }

        public HashMap<Integer, Long> getWaveDamage() {
            return waveDamage;
        }
    }
}
