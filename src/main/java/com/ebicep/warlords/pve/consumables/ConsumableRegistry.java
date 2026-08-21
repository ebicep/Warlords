package com.ebicep.warlords.pve.consumables;

import com.ebicep.warlords.pve.consumables.vials.Vial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ConsumableRegistry {

    private static final Map<String, Consumable> BY_ID = new LinkedHashMap<>();
    private static final List<Consumable> VALUES;

    static {
        for (Vial vial : Vial.VALUES) {
            BY_ID.put(vial.getId(), vial);
        }
        BY_ID.put(FairyEssencePouch.INSTANCE.getId(), FairyEssencePouch.INSTANCE);
        VALUES = Collections.unmodifiableList(new ArrayList<>(BY_ID.values()));
    }

    private ConsumableRegistry() {
    }

    public static Consumable get(String id) {
        return BY_ID.get(id);
    }

    public static List<Consumable> values() {
        return VALUES;
    }
}
