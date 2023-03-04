package com.ebicep.warlords.database.repositories.items.pojos;

import com.ebicep.warlords.pve.items.legacy.Items;

import java.time.Instant;
import java.util.UUID;

public class ItemEntry {

    private Items item;
    private UUID uuid = UUID.randomUUID();
    private Instant obtained = Instant.now();

    public ItemEntry(Items item) {
        this.item = item;
    }

    public Items getItem() {
        return item;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Instant getObtained() {
        return obtained;
    }

}
