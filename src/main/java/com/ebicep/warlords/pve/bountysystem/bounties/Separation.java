package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.rewards.WeeklyRewardSpendable2;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import com.ebicep.warlords.util.warlords.Utils;

import java.util.UUID;

public class Separation extends AbstractBounty implements TracksDuringGame, WeeklyRewardSpendable2 {

    private int newKills = 0;

    @Override
    public int getTarget() {
        return 500;
    }

    @Override
    public String getName() {
        return "Separation";
    }

    @Override
    public String getDescription() {
        return "Kill " + getTarget() + " enemies with projectiles in any gamemode.";
    }

    @Override
    public Bounty getBounty() {
        return Bounty.SEPARATION;
    }

    @Override
    public void onFinalDamageHeal(UUID uuid, WarlordsDamageHealingFinalEvent event) {
        if (!event.getAttacker().getUuid().equals(uuid)) {
            return;
        }
        if (!event.isDead()) {
            return;
        }
        if (!Utils.isProjectile(event.getAbility())) {
            return;
        }
        newKills++;
    }

    @Override
    public void reset() {
        newKills = 0;
    }

    @Override
    public long getNewValue() {
        return newKills;
    }
}
