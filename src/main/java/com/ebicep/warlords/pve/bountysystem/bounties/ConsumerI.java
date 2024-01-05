package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.WeaponTitlePurchaseEvent;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.LifetimeCost;
import com.ebicep.warlords.pve.bountysystem.rewards.LifetimeRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksOutsideGame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Objects;

public class ConsumerI extends AbstractBounty implements TracksOutsideGame, LifetimeCost, LifetimeRewardSpendable1 {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWeaponTitlePurchase(WeaponTitlePurchaseEvent event) {
        if (!Objects.equals(event.getUUID(), uuid)) {
            return;
        }
        value++;
    }

    @Override
    public String getName() {
        return "Consumer";
    }

    @Override
    public String getDescription() {
        return "Purchase " + getTarget() + " Weapon Titles";
    }

    @Override
    public int getTarget() {
        return 6;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.CONSUMER_I;
    }


}
