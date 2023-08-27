package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.rewards.WeeklyRewardSpendable2;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;

import java.util.UUID;

public class Brute extends AbstractBounty implements TracksDuringGame, WeeklyRewardSpendable2 {

    private int newKills = 0;

    @Override
    public String getName() {
        return "Brute";
    }

    @Override
    public String getDescription() {
        return "Kill " + getTarget() + " enemies with melee damage in any gamemode.";
    }

    @Override
    public int getTarget() {
        return 250;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.BRUTE;
    }

    @Override
    public void reset() {
        newKills = 0;
    }

    @Override
    public void onFinalDamageHeal(UUID uuid, WarlordsDamageHealingFinalEvent event) {
        if (!event.getAttacker().getUuid().equals(uuid)) {
            return;
        }
        if (!event.isDead()) {
            return;
        }
        if (!event.getAbility().isEmpty()) {
            return;
        }
        newKills++;
    }

    @Override
    public long getNewValue() {
        return newKills;
    }
}
