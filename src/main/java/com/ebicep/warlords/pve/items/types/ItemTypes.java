package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;

public enum ItemTypes {

    BUCKLER("Buckler",
            ItemBuckler::new,
            ItemBuckler::new
    ),
    GAUNTLET("Gauntlet",
            ItemGauntlet::new,
            ItemGauntlet::new
    ),
    TOME("Tome",
            ItemTome::new,
            ItemTome::new
    ),

    ;

    public static final ItemTypes[] VALUES = values();
    public final String name;
    public final Function<ItemTier, AbstractItem<?, ?, ?>> create;
    public final Supplier<AbstractItem<?, ?, ?>> clone;

    public static ItemTypes getRandom() {
        return VALUES[ThreadLocalRandom.current().nextInt(VALUES.length)];
    }

    ItemTypes(String name, Function<ItemTier, AbstractItem<?, ?, ?>> create, Supplier<AbstractItem<?, ?, ?>> clone) {
        this.name = name;
        this.create = create;
        this.clone = clone;
    }
}
