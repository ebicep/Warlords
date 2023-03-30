package com.ebicep.warlords.game.option.pve.wavedefense;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsLegendFragmentGainEvent;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.MobDrops;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class WaveDefenseStats {
    private final HashMap<String, Long> mobsKilled = new HashMap<>();
    private final HashMap<UUID, PlayerWaveDefenseStats> playerWaveDefenseStats = new HashMap<>();
    private boolean boostedGame = false;

    public WaveDefenseStats() {
        boostedGame = ThreadLocalRandom.current().nextInt(0, 100) < 5;
    }

    public void cacheBaseCoinSummary(WaveDefenseOption waveDefenseOption) {
        LinkedHashMap<String, Long> cachedBaseCoinSummary = new LinkedHashMap<>();
        DifficultyIndex difficulty = waveDefenseOption.getDifficulty();

        CoinGainOption coinGainOption = waveDefenseOption
                .getGame()
                .getOptions()
                .stream()
                .filter(CoinGainOption.class::isInstance)
                .map(CoinGainOption.class::cast)
                .findAny()
                .orElse(null);

        if (coinGainOption == null) {
            return;
        }
        if (coinGainOption.isPlayerCoinWavesClearedBonus()) {
            cachedBaseCoinSummary.put("Waves Cleared", 0L);
            for (int i = 1; i <= waveDefenseOption.getWavesCleared(); i++) {
                if ((i - 1) / 5 >= CoinGainOption.COINS_PER_5_WAVES.length) {
                    break;
                }
                cachedBaseCoinSummary.merge("Waves Cleared",
                        (long) (CoinGainOption.COINS_PER_5_WAVES[(i - 1) / 5] * difficulty.getRewardsMultiplier()),
                        Long::sum
                );
            }
        }
        for (Map.Entry<String, LinkedHashMap<String, Long>> stringLinkedHashMapEntry : coinGainOption.getMobCoinValues().entrySet()) {
            cachedBaseCoinSummary.put(stringLinkedHashMapEntry.getKey(), 0L);
            for (Map.Entry<String, Long> stringLongEntry : stringLinkedHashMapEntry.getValue().entrySet()) {
                if (mobsKilled.containsKey(stringLongEntry.getKey())) {
                    cachedBaseCoinSummary.merge(stringLinkedHashMapEntry.getKey(),
                            (long) (mobsKilled.get(stringLongEntry.getKey()) * stringLongEntry.getValue() * difficulty
                                    .getRewardsMultiplier()),
                            Long::sum
                    );
                }
            }
        }
        if (coinGainOption.getPlayerCoinPerKill() != 0) {
            int totalKills = waveDefenseOption
                    .getGame()
                    .warlordsPlayers()
                    .mapToInt(wp -> wp.getMinuteStats().total().getKills()).sum();
            cachedBaseCoinSummary.put("Kills", coinGainOption.getPlayerCoinPerKill() * totalKills);
        }
        long convertBonus = coinGainOption.getGuildCoinInsigniaConvertBonus();
        if (convertBonus != 0) {
            waveDefenseOption
                    .getGame()
                    .warlordsPlayers()
                    .forEach(warlordsPlayer -> {
                        if (waveDefenseOption.getWavesCleared() >= waveDefenseOption.getMaxWave() || difficulty == DifficultyIndex.ENDLESS) {
                            long coinsConverted = warlordsPlayer.getCurrency() / 100;
                            cachedBaseCoinSummary.put("Excess Insignia Converted", Math.min(coinsConverted, convertBonus));
                        }
                    });
        }
        waveDefenseOption
                .getGame()
                .warlordsPlayers()
                .forEach(warlordsPlayer -> getPlayerWaveDefenseStats(warlordsPlayer.getUuid()).setCachedBaseCoinSummary(cachedBaseCoinSummary));
    }

    public PlayerWaveDefenseStats getPlayerWaveDefenseStats(UUID uuid) {
        return playerWaveDefenseStats.computeIfAbsent(uuid, k -> new PlayerWaveDefenseStats());
    }

    public void storeWeaponFragmentGain(WaveDefenseOption waveDefenseOption) {
        int wavesCleared = waveDefenseOption.getWavesCleared();
        boolean won = waveDefenseOption.getWavesCleared() >= waveDefenseOption.getMaxWave();
        DifficultyIndex difficulty = waveDefenseOption.getDifficulty();
        if (difficulty == DifficultyIndex.EVENT) {
            return;
        }
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
                                     Bukkit.getPluginManager()
                                           .callEvent(new WarlordsLegendFragmentGainEvent(warlordsPlayer, legendFragmentGain, waveDefenseOption));
                                     //warlordsPlayer.sendMessage("Legend Fragment Gain After Guild: " + legendFragmentGain.get());
                                     getPlayerWaveDefenseStats(uuid).setLegendFragmentGain(legendFragmentGain.get());
                                 });
                             }
                         });
    }

    public HashMap<String, Long> getMobsKilled() {
        return mobsKilled;
    }

    public static class PlayerWaveDefenseStats {
        private final LinkedHashMap<String, Long> cachedBaseCoinSummary = new LinkedHashMap<>();
        private final List<AbstractWeapon> weaponsFound = new ArrayList<>();
        private final HashMap<MobDrops, Long> mobDropsGained = new HashMap<>();
        private final HashMap<Integer, Long> waveDamage = new HashMap<>();
        private long legendFragmentGain = 0;
        private int weaponsAutoSalvaged = 0;

        public List<AbstractWeapon> getWeaponsFound() {
            return weaponsFound;
        }

        public HashMap<MobDrops, Long> getMobDropsGained() {
            return mobDropsGained;
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
