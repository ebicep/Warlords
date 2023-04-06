package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.ItemStatPool;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public enum ItemType {

    BUCKLER("Buckler",
            ItemBuckler::new,
            ItemBuckler::new,
            ItemBuckler::new
    ),
    GAUNTLET("Gauntlet",
            ItemGauntlet::new,
            ItemGauntlet::new,
            ItemGauntlet::new
    ),
    TOME("Tome",
            ItemTome::new,
            ItemTome::new,
            ItemTome::new
    ),

    ;

    public static final ItemType[] VALUES = values();
    public final String name;
    public final Function<ItemTier, AbstractItem<?, ?>> create;
    public final BiFunction<ItemTier, Set<ItemStatPool>, AbstractItem<?, ?>> createInherited;
    public final Supplier<AbstractItem<?, ?>> clone;

    public static ItemType getRandom() {
        return VALUES[ThreadLocalRandom.current().nextInt(VALUES.length)];
    }

    ItemType(
            String name,
            Function<ItemTier, AbstractItem<?, ?>> create,
            BiFunction<ItemTier, Set<ItemStatPool>, AbstractItem<?, ?>> createInherited,
            Supplier<AbstractItem<?, ?>> clone
    ) {
        this.name = name;
        this.create = create;
        this.createInherited = createInherited;
        this.clone = clone;
    }
}
