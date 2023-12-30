package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.DailyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.springframework.data.annotation.Transient;

import java.util.Objects;

public class LuckyI extends AbstractBounty implements TracksDuringGame, DailyCost, DailyRewardSpendable1 {

    @Transient
    private int newCrits = 0;

    @Override
    public String getName() {
        return "Lucky";
    }

    @Override
    public String getDescription() {
        return "Deal " + getTarget() + " critical heals/hits in any gamemode.";
    }

    @Override
    public int getTarget() {
        return 1000;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.LUCKY_I;
    }

    @Override
    public void reset() {
        newCrits = 0;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFinalDamageHeal(WarlordsDamageHealingFinalEvent event) {
        if (!Objects.equals(event.getAttacker().getUuid(), uuid)) {
            return;
        }
        if (event.isCrit()) {
            newCrits++;
        }
    }

    @Override
    public long getNewValue() {
        return newCrits;
    }
}
