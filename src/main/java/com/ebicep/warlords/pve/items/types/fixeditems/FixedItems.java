package com.ebicep.warlords.pve.items.types.fixeditems;

import com.ebicep.warlords.pve.items.types.AbstractFixedItem;

import java.util.function.Supplier;

public enum FixedItems {

    SHAWL_OF_MITHRA(ShawlOfMithra::new),
    SPIDER_GAUNTLET(SpiderGauntlet::new),

    ;

    public final Supplier<AbstractFixedItem> create;

    FixedItems(Supplier<AbstractFixedItem> create) {
        this.create = create;
    }
}
