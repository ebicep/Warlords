package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.LifetimeCost;
import com.ebicep.warlords.pve.bountysystem.rewards.LifetimeRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksOutsideGame;
import com.ebicep.warlords.pve.weapons.events.WeaponUpgradeEvent;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Objects;

public class RefinedI extends AbstractBounty implements TracksOutsideGame, LifetimeCost, LifetimeRewardSpendable1 {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWeaponUpgrade(WeaponUpgradeEvent event) {
        if (!Objects.equals(event.getUUID(), uuid)) {
            return;
        }
        if (!(event.getWeapon() instanceof AbstractLegendaryWeapon weapon)) {
            return;
        }
        if (weapon.getUpgradeLevel() == 4) {
            value++;
        }
    }

    @Override
    public String getName() {
        return "Refined";
    }

    @Override
    public String getDescription() {
        return "Upgrade a Legendary Weapon to 4/4";
    }

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.REFINED_I;
    }


}
