package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.player.ingame.WarlordsEnergyUsedEvent;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import com.ebicep.warlords.util.java.NumberFormat;
import org.springframework.data.annotation.Transient;

import java.util.UUID;

public class Pursue extends AbstractBounty implements TracksDuringGame, DailyRewardSpendable1 {

    @Transient
    private int newEnergyUsed = 0;

    @Override
    public int getTarget() {
        return 10_000;
    }

    @Override
    public String getName() {
        return "Pursue";
    }

    @Override
    public String getDescription() {
        return "Expend " + NumberFormat.addCommaAndRound(getTarget()) + " energy in any gamemode.";
    }

    @Override
    public Bounty getBounty() {
        return Bounty.PURSUE;
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

    @Override
    public void reset() {
        newEnergyUsed = 0;
    }
}
