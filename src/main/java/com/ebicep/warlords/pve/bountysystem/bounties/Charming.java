package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import org.springframework.data.annotation.Transient;

import java.util.UUID;

public class Charming extends AbstractBounty implements TracksDuringGame, DailyRewardSpendable1 {

    @Transient
    private int newUsed = 0;

    @Override
    public int getTarget() {
        return 500;
    }

    @Override
    public String getName() {
        return "Charming";
    }

    @Override
    public String getDescription() {
        return "Cast " + getTarget() + " rune abilities in any gamemode.";
    }

    @Override
    public Bounty getBounty() {
        return Bounty.CHARMING;
    }

    @Override
    public void onAbilityUsed(UUID uuid, WarlordsAbilityActivateEvent event) {
        if (!event.getWarlordsEntity().getUuid().equals(uuid)) {
            return;
        }
        newUsed++;
    }

    @Override
    public int getNewValue() {
        return newUsed;
    }

    @Override
    public void reset() {
        newUsed = 0;
    }
}
