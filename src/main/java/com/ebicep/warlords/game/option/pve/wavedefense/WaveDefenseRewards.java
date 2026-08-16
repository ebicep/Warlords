package com.ebicep.warlords.game.option.pve.wavedefense;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsLegendFragmentGainEvent;
import com.ebicep.warlords.game.option.pve.rewards.CoinGainOption;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.java.RandomCollection;
import org.bukkit.Bukkit;

import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class WaveDefenseRewards extends PveRewards<WaveDefenseOption> {

    public static final RandomCollection<Pair<Spendable, Long>> ASCENDANT_POUCH_LOOT_POOL = new RandomCollection<Pair<Spendable, Long>>()
            .add(15, new Pair<>(Currencies.ASCENDANT_SHARD, 2L))
            .add(15, new Pair<>(Currencies.LEGEND_FRAGMENTS, 2000L))
            .add(15, new Pair<>(Currencies.LIMIT_BREAKER, 1L))
            .add(15, new Pair<>(Currencies.ITEM_LOCK_SCROLL, 1L))
            .add(15, new Pair<>(Currencies.PRESTIGE_ORB, 1L))
            .add(15, new Pair<>(Currencies.ETHEREUM_CRYSTAL, 2L))
            .add(10, new Pair<>(Currencies.ASCENDANT_STAR_PIECE, 1L));

    public WaveDefenseRewards(WaveDefenseOption pveOption) {
        super(pveOption);
    }

    @Override
    public void storeRewards() {
        super.storeRewards();
        storePouchRewards();
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
                     DatabasePlayer databasePlayer = DatabaseManager.getPlayer(uuid);
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

    private void storePouchRewards() {
        pveOption.getPlayerAscendantPouch().forEach((uuid, spendableLongHashMap) -> getPlayerRewards(uuid).setAscendantPouch(spendableLongHashMap));
    }

}
