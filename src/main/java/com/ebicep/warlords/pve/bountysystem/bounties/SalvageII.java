package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.player.WeaponSalvageEvent;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.DailyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksOutsideGame;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Objects;

public class SalvageII extends AbstractBounty implements TracksOutsideGame, DailyCost, DailyRewardSpendable1 {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWeaponSalvagePost(WeaponSalvageEvent.Post event) {
        if (!Objects.equals(event.getUUID(), uuid)) {
            return;
        }
        if (event.getWeapon().getRarity() == WeaponsPvE.RARE) {
            value++;
        }
    }

    @Override
    public String getName() {
        return "Salvage";
    }

    @Override
    public String getDescription() {
        return "Salvage " + getTarget() + " Rare weapons.";
    }

    @Override
    public int getTarget() {
        return 5;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.SALVAGE_II;
    }


}
