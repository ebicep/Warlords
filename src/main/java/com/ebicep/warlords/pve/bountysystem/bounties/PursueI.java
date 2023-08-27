package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.player.ingame.WarlordsEnergyUsedEvent;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.DailyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import com.ebicep.warlords.util.java.NumberFormat;
import org.springframework.data.annotation.Transient;

import java.util.UUID;

public class PursueI extends AbstractBounty implements TracksDuringGame, DailyCost, DailyRewardSpendable1 {

    @Transient
    private int newEnergyUsed = 0;

    @Override
    public String getName() {
        return "Pursue";
    }

    @Override
    public String getDescription() {
        return "Expend " + NumberFormat.addCommaAndRound(getTarget()) + " energy in any gamemode.";
    }

    @Override
    public int getTarget() {
        return 10_000;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.PURSUE_I;
    }

    @Override
    public void reset() {
        newEnergyUsed = 0;
    }

    @Override
    public void onEnergyUsed(UUID uuid, WarlordsEnergyUsedEvent event) {
        if (event.getWarlordsEntity().getUuid().equals(uuid)) {
            newEnergyUsed += event.getEnergyUsed();
        }
    }

    @Override
    public long getNewValue() {
        return newEnergyUsed;
    }
}
