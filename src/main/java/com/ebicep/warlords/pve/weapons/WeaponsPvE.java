package com.ebicep.warlords.pve.weapons;

import com.ebicep.warlords.pve.weapons.weapontypes.CommonWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.EpicWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.LegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.RareWeapon;
import org.bukkit.ChatColor;

public enum WeaponsPvE {

    NONE(null, ChatColor.GRAY, "None"),
    COMMON(CommonWeapon.class, ChatColor.GREEN, "Common"),
    RARE(RareWeapon.class, ChatColor.BLUE, "Rare"),
    EPIC(EpicWeapon.class, ChatColor.DARK_PURPLE, "Epic"),
    LEGENDARY(LegendaryWeapon.class, ChatColor.GOLD, "Legendary");

    public final Class<?> weaponClass;
    public final ChatColor chatColor;
    public final String name;

    WeaponsPvE(Class<?> weaponClass, ChatColor chatColor, String name) {
        this.weaponClass = weaponClass;
        this.chatColor = chatColor;
        this.name = name;
    }

    private static final WeaponsPvE[] vals = values();

    public WeaponsPvE next() {
        return vals[(this.ordinal() + 1) % vals.length];
    }

    public String getGeneralName() {
        return chatColor + name + " Weapon";
    }

    public static WeaponsPvE getWeapon(AbstractWeapon abstractWeapon) {
        if (abstractWeapon instanceof CommonWeapon) {
            return COMMON;
        } else if (abstractWeapon instanceof RareWeapon) {
            return RARE;
        } else if (abstractWeapon instanceof EpicWeapon) {
            return EPIC;
        } else if (abstractWeapon instanceof LegendaryWeapon) {
            return LEGENDARY;
        } else {
            return NONE;
        }
    }
}
