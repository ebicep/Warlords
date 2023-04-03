package com.ebicep.warlords.game.option.pve.onslaught;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsLegendFragmentGainEvent;
import com.ebicep.warlords.game.option.pve.rewards.CoinGainOption;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import org.bukkit.Bukkit;

import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class OnslaughtRewards extends PveRewards<OnslaughtOption> {

    public OnslaughtRewards(OnslaughtOption pveOption) {
        super(pveOption);
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

}
