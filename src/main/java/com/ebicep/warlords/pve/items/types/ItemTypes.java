package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;

import java.util.UUID;
import java.util.function.BiFunction;

public enum ItemTypes {

    BUCKLER("Buckler",
            ItemBuckler::new
    ),
    GAUNTLET("Gauntlet",
            ItemGauntlet::new
    ),
    TOME("Tome",
            ItemTome::new
    ),

    ;

    public static final ItemTypes[] VALUES = values();
    public final String name;
    public final BiFunction<UUID, ItemTier, AbstractItem<?, ?, ?>> create;

    ItemTypes(String name, BiFunction<UUID, ItemTier, AbstractItem<?, ?, ?>> create) {
        this.name = name;
        this.create = create;
    }
}
