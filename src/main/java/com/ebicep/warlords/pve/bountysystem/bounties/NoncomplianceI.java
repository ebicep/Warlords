package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.WeeklyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.WeeklyRewardSpendable2;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Objects;

public class NoncomplianceI extends AbstractBounty implements TracksDuringGame, WeeklyCost, WeeklyRewardSpendable2 {

    private int newKills = 0;

    @Override
    public String getName() {
        return "Separation";
    }

    @Override
    public String getDescription() {
        return "Kill " + getTarget() + " enemies with knockback damage in any gamemode.";
    }

    @Override
    public int getTarget() {
        return 500;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.NONCOMPLIANCE_I;
    }

    @Override
    public void reset() {
        newKills = 0;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFinalDamageHeal(WarlordsDamageHealingFinalEvent event) {
        if (!Objects.equals(event.getAttacker().getUuid(), uuid)) {
            return;
        }
        if (!event.isDead()) {
            return;
        }
        if (!Utils.isKnockbackAbility(event.getAbility())) {
            return;
        }
        newKills++;
    }

    @Override
    public long getNewValue() {
        return newKills;
    }
}
