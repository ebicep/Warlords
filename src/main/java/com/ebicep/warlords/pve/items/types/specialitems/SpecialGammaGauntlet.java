package com.ebicep.warlords.pve.items.types.specialitems;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.addons.ItemAddonSpecBonus;
import com.ebicep.warlords.pve.items.statpool.ItemStatPool;
import com.ebicep.warlords.pve.items.types.BasicItem;
import com.ebicep.warlords.pve.items.types.ItemType;

import java.util.Set;

public class SpecialGammaGauntlet extends BasicItem implements ItemAddonSpecBonus {

    private Specializations spec;

    public SpecialGammaGauntlet(Specializations spec) {
        this.spec = Specializations.generateSpec(spec);
    }

    public SpecialGammaGauntlet(ItemTier tier, Specializations spec) {
        super(ItemType.GAUNTLET, tier);
        this.spec = Specializations.generateSpec(spec);
    }

    public SpecialGammaGauntlet(
            ItemTier tier,
            Set<ItemStatPool> statPool,
            Specializations spec
    ) {
        super(ItemType.GAUNTLET, tier, statPool);
        this.spec = Specializations.generateSpec(spec);
    }

    @Override
    public Specializations getSpec() {
        return spec;
    }

}
