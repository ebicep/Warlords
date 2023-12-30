package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.player.WeaponSalvageEvent;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.DailyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable4;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksOutsideGame;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Objects;

public class SalvageIII extends AbstractBounty implements TracksOutsideGame, DailyCost, DailyRewardSpendable4 {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWeaponSalvagePost(WeaponSalvageEvent.Post event) {
        if (!Objects.equals(event.getUUID(), uuid)) {
            return;
        }
        if (event.getWeapon().getRarity() == WeaponsPvE.EPIC) {
            value++;
        }
    }

    @Override
    public String getName() {
        return "Salvage";
    }

    @Override
    public String getDescription() {
        return "Salvage an Epic weapon.";
    }

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.SALVAGE_III;
    }


}
