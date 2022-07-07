package com.ebicep.warlords.pve.weapons;

import com.ebicep.warlords.pve.weapons.weapontypes.CommonWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.EpicWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.LegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.RareWeapon;
import org.bukkit.ChatColor;

public enum WeaponsPvE {

    NONE("None", null, ChatColor.GRAY),
    COMMON("Common", CommonWeapon.class, ChatColor.GREEN),
    RARE("Rare", RareWeapon.class, ChatColor.BLUE),
    EPIC("Epic", EpicWeapon.class, ChatColor.DARK_PURPLE),
    LEGENDARY("Legendary", LegendaryWeapon.class, ChatColor.GOLD);

    public final String name;
    public final Class<?> weaponClass;
    public final ChatColor chatColor;

    WeaponsPvE(String name, Class<?> weaponClass, ChatColor chatColor) {
        this.weaponClass = weaponClass;
        this.chatColor = chatColor;
        this.name = name;
    }

    private static final WeaponsPvE[] vals = values();

    public WeaponsPvE next() {
        return vals[(this.ordinal() + 1) % vals.length];
    }

    public static WeaponsPvE getWeapon(AbstractWeapon abstractWeapon) {
        for (WeaponsPvE value : values()) {
            if (value.weaponClass == abstractWeapon.getClass()) {
                return value;
            }
        }
        return NONE;
    }
}
