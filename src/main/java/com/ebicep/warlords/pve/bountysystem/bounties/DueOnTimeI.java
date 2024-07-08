package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.WeeklyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.WeeklyRewardSpendable2;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Objects;

public class DueOnTimeI extends AbstractBounty implements TracksDuringGame, WeeklyCost, WeeklyRewardSpendable2 {

    private int newKills = 0;

    @Override
    public String getName() {
        return "Due on Time";
    }

    @Override
    public String getDescription() {
        return "Kill " + getTarget() + " enemies with Damage Over Time effects in any gamemode.";
    }

    @Override
    public int getTarget() {
        return 500;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.DUE_ON_TIME_I;
    }

    @Override
    public void reset() {
        newKills = 0;
    }

    @Override
    public long getNewValue() {
        return newKills;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFinalDamageHeal(WarlordsDamageHealingFinalEvent event) {
        if (!Objects.equals(event.getAttacker().getUuid(), uuid)) {
            return;
        }
        if (!event.isDead()) {
            return;
        }
        if (!event.getInstanceFlags().contains(InstanceFlags.DOT)) {
            return;
        }
        newKills++;
    }
}
