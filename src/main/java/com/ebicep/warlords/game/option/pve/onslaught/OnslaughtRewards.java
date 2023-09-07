package com.ebicep.warlords.game.option.pve.onslaught;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsLegendFragmentGainEvent;
import com.ebicep.warlords.game.option.pve.rewards.CoinGainOption;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.java.RandomCollection;
import org.bukkit.Bukkit;

import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class OnslaughtRewards extends PveRewards<OnslaughtOption> {

    public static final RandomCollection<Pair<Spendable, Long>> SYNTHETIC_POUCH_LOOT_POOL = new RandomCollection<Pair<Spendable, Long>>()
            .add(54.9, new Pair<>(Currencies.COIN, 3000L))
            .add(35, new Pair<>(Currencies.SYNTHETIC_SHARD, 40L))
            .add(10, new Pair<>(Currencies.LEGEND_FRAGMENTS, 30L))
            .add(.1, new Pair<>(MobDrop.ZENITH_STAR, 1L));
    public static final RandomCollection<Pair<Spendable, Long>> ASPIRANT_POUCH_LOOT_POOL = new RandomCollection<Pair<Spendable, Long>>()
            .add(65, new Pair<>(Currencies.LEGEND_FRAGMENTS, 100L))
            .add(25, new Pair<>(Currencies.SUPPLY_DROP_TOKEN, 10L))
            .add(5, new Pair<>(MobDrop.ZENITH_STAR, 1L))
            .add(5, new Pair<>(Currencies.LEGENDARY_STAR_PIECE, 1L));

    public OnslaughtRewards(OnslaughtOption pveOption) {
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
            int minutesElapsed = pveOption.getTicksElapsed() / 20 / 60;
            cachedBaseCoinSummary.put("Minutes Elapsed", 0L);
            for (int i = 1; i <= minutesElapsed; i++) {
                if ((i - 1) / 5 >= CoinGainOption.COINS_PER_5.length) {
                    break;
                }
                cachedBaseCoinSummary.merge("Minutes Elapsed",
                        (long) (CoinGainOption.COINS_PER_5[(i - 1) / 5] * difficulty.getRewardsMultiplier()),
                        Long::sum
                );
            }
        }
    }

    @Override
    protected boolean shouldStoreInsigniaConverted() {
        return true;
    }

    @Override
    public void storeWeaponFragmentGainInternal() {
        int minutesElapsed = pveOption.getTicksElapsed() / 20 / 60;

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
                         legendFragmentGain.set(minutesElapsed);
                         //warlordsPlayer.sendMessage("Legend Fragment Gain: " + legendFragmentGain.get());
                         addExtraFragmentGain(minutesElapsed, currentSpec, databasePlayer, legendFragmentGain);
                         Bukkit.getPluginManager()
                               .callEvent(new WarlordsLegendFragmentGainEvent(warlordsPlayer, legendFragmentGain, pveOption, minutesElapsed));
                         //warlordsPlayer.sendMessage("Legend Fragment Gain After Guild: " + legendFragmentGain.get());
                         getPlayerRewards(uuid).setLegendFragmentGain(legendFragmentGain.get());
                     });
                 });

    }

    @Override
    protected void storeIllusionShardGainInternal() {
        int minutesElapsed = pveOption.getTicksElapsed() / 20 / 60;

        pveOption.getGame()
                 .warlordsPlayers()
                 .forEach(warlordsPlayer -> {
                     UUID uuid = warlordsPlayer.getUuid();
                     getPlayerRewards(uuid).setIllusionShardGain(minutesElapsed / 5);
                 });
    }

    private void storePouchRewards() {
        pveOption.getPlayerSyntheticPouch().forEach((uuid, spendableLongHashMap) -> getPlayerRewards(uuid).setSyntheticPouch(spendableLongHashMap));
        pveOption.getPlayerAspirantPouch().forEach((uuid, spendableLongHashMap) -> getPlayerRewards(uuid).setAspirantPouch(spendableLongHashMap));
    }

}
