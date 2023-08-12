package com.ebicep.warlords.game.option.pve.wavedefense;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsLegendFragmentGainEvent;
import com.ebicep.warlords.game.option.pve.rewards.CoinGainOption;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import org.bukkit.Bukkit;

import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class WaveDefenseRewards extends PveRewards<WaveDefenseOption> {

    public WaveDefenseRewards(WaveDefenseOption pveOption) {
        super(pveOption);
    }

    @Override
    public void storeCustomBaseCoinSummary(LinkedHashMap<String, Long> cachedBaseCoinSummary) {
        if (coinGainOption.isPlayerCoinPer5Bonus()) {
            cachedBaseCoinSummary.put("Waves Cleared", 0L);
            for (int i = 1; i <= pveOption.getWavesCleared(); i++) {
                if ((i - 1) / 5 >= CoinGainOption.COINS_PER_5.length) {
                    break;
                }
                cachedBaseCoinSummary.merge("Waves Cleared",
                        (long) (CoinGainOption.COINS_PER_5[(i - 1) / 5] * difficulty.getRewardsMultiplier()),
                        Long::sum
                );
            }
        }
    }

    @Override
    protected boolean shouldStoreInsigniaConverted() {
        return pveOption.getWavesCleared() >= pveOption.getMaxWave() || pveOption.getDifficulty() == DifficultyIndex.ENDLESS;
    }

    @Override
    public void storeWeaponFragmentGainInternal() {
        int wavesCleared = pveOption.getWavesCleared();
        boolean won = pveOption.getWavesCleared() >= pveOption.getMaxWave();
        pveOption.getGame()
                 .warlordsPlayers()
                 .forEach(warlordsPlayer -> {
                     if (!(warlordsPlayer.getWeapon() instanceof AbstractLegendaryWeapon)) {
                         return;
                     }
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
                         addExtraFragmentGain(wavesCleared, currentSpec, databasePlayer, legendFragmentGain);
                         Bukkit.getPluginManager()
                               .callEvent(new WarlordsLegendFragmentGainEvent(warlordsPlayer, legendFragmentGain, pveOption, wavesCleared));
                         //warlordsPlayer.sendMessage("Legend Fragment Gain After Guild: " + legendFragmentGain.get());
                         getPlayerRewards(uuid).setLegendFragmentGain(legendFragmentGain.get());
                     });
                 });

    }

    @Override
    protected void storeIllusionShardGainInternal() {
        int wavesCleared = pveOption.getWavesCleared();

        pveOption.getGame()
                 .warlordsPlayers()
                 .forEach(warlordsPlayer -> {
                     UUID uuid = warlordsPlayer.getUuid();
                     getPlayerRewards(uuid).setIllusionShardGain(wavesCleared / 5 * (pveOption.getDifficulty() == DifficultyIndex.EXTREME ? 2 : 1));
                 });
    }

}
