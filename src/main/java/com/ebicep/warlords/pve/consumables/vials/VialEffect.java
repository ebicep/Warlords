package com.ebicep.warlords.pve.consumables.vials;

public enum VialEffect {
    INSIGNIA_GAIN("insignia_gain"),
    WEAPON_DROP_RATE("weapon_drop_rate"),
    ITEM_DROP_RATE("item_drop_rate");

    private final String activeGroup;

    VialEffect(String activeGroup) {
        this.activeGroup = activeGroup;
    }

    public String getActiveGroup() {
        return activeGroup;
    }
}
