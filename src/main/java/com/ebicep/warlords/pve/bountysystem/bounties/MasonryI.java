package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.LifetimeCost;
import com.ebicep.warlords.pve.bountysystem.rewards.LifetimeRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksOutsideGame;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.types.AbstractItem;

public class MasonryI extends AbstractBounty implements TracksOutsideGame, LifetimeCost, LifetimeRewardSpendable1 {

    @Override
    public void onItemCraft(AbstractItem item) {
        if (item.getTier() == ItemTier.OMEGA) {
            value++;
        }
    }

    @Override
    public String getName() {
        return "Masonry";
    }

    @Override
    public String getDescription() {
        return "Craft " + getTarget() + " Omega item.";
    }

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.MASONRY_I;
    }


}
