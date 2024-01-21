package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.LifetimeCost;
import com.ebicep.warlords.pve.bountysystem.rewards.LifetimeRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksOutsideGame;
import com.ebicep.warlords.pve.weapons.events.LegendaryWeaponCraftEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Objects;

public class ArtisanI extends AbstractBounty implements TracksOutsideGame, LifetimeCost, LifetimeRewardSpendable1 {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLegendaryWeaponCraft(LegendaryWeaponCraftEvent event) {
        if (!Objects.equals(event.getUUID(), uuid)) {
            return;
        }
        value++;
    }

    @Override
    public String getName() {
        return "Artisan";
    }

    @Override
    public String getDescription() {
        return "Craft " + getTarget() + " Legendary Weapons.";
    }

    @Override
    public int getTarget() {
        return 3;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.ARTISAN_I;
    }


}
