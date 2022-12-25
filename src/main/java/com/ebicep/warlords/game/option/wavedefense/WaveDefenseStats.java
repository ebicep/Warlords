package com.ebicep.warlords.game.option.wavedefense;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsLegendFragmentGainEvent;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

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
                    if (waveDefenseOption.getWavesCleared() >= waveDefenseOption.getMaxWave() || waveDefenseOption.getDifficulty() == DifficultyIndex.ENDLESS) {
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
        DifficultyIndex difficulty = waveDefenseOption.getDifficulty();
        waveDefenseOption.getGame()
                .warlordsPlayers()
                .forEach(warlordsPlayer -> {
                    if (warlordsPlayer.getWeapon() instanceof AbstractLegendaryWeapon) {
                        UUID uuid = warlordsPlayer.getUuid();
                        Specializations currentSpec = warlordsPlayer.getSpecClass();
                        DatabaseManager.getPlayer(uuid, databasePlayer -> {
                            AtomicLong legendFragmentGain = new AtomicLong();
                            if (won || difficulty == DifficultyIndex.ENDLESS) {
                                legendFragmentGain.set(wavesCleared);
                            } else {
                                legendFragmentGain.set((long) (wavesCleared * 0.5));
                            }
                            //warlordsPlayer.sendMessage("Legend Fragment Gain: " + legendFragmentGain.get());
                            legendFragmentGain.updateAndGet(v -> (long) (v * difficulty.getRewardsMultiplier()));
                            //warlordsPlayer.sendMessage("Legend Fragment Gain After Rewards Multiplier: " + legendFragmentGain.get());
                            int specPrestigeBonus = databasePlayer.getSpec(currentSpec).getPrestige() * 5;
                            int otherSpecPrestigeBonus = 0;
                            for (Specializations value : Specializations.VALUES) {
                                if (value != currentSpec) {
                                    otherSpecPrestigeBonus += databasePlayer.getSpec(value).getPrestige() * 2;
                                }
                            }
                            legendFragmentGain.addAndGet((long) ((specPrestigeBonus + otherSpecPrestigeBonus) * difficulty.getRewardsMultiplier() * (wavesCleared / 25)));
                            //warlordsPlayer.sendMessage("Legend Fragment Gain After Prestiges: " + legendFragmentGain.get());
                            Bukkit.getPluginManager().callEvent(new WarlordsLegendFragmentGainEvent(warlordsPlayer, legendFragmentGain, waveDefenseOption));
                            //warlordsPlayer.sendMessage("Legend Fragment Gain After Guild: " + legendFragmentGain.get());
                            getPlayerWaveDefenseStats(uuid).setLegendFragmentGain(legendFragmentGain.get());
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
