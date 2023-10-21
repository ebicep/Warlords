package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.DailyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import org.springframework.data.annotation.Transient;

import java.util.UUID;

public class CharmingI extends AbstractBounty implements TracksDuringGame, DailyCost, DailyRewardSpendable1 {

    @Transient
    private int newUsed = 0;

    @Override
    public String getName() {
        return "Charming";
    }

    @Override
    public String getDescription() {
        return "Cast " + getTarget() + " rune abilities in any gamemode.";
    }

    @Override
    public int getTarget() {
        return 500;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.CHARMING_I;
    }

    @Override
    public void reset() {
        newUsed = 0;
    }

    @Override
    public void onAbilityUsed(UUID uuid, WarlordsAbilityActivateEvent.Pre event) {
        if (!event.getWarlordsEntity().getUuid().equals(uuid)) {
            return;
        }
        newUsed++;
    }

    @Override
    public long getNewValue() {
        return newUsed;
    }
}
