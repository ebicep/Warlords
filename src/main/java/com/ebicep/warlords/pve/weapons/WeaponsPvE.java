package com.ebicep.warlords.pve.weapons;

import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFair;
import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFairPlayerEntry;
import com.ebicep.warlords.pve.weapons.weapontypes.CommonWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.EpicWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.LegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.RareWeapon;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Function;

public enum WeaponsPvE {

    NONE("None", null, ChatColor.GRAY, null, null),
    COMMON("Common", CommonWeapon.class, ChatColor.GREEN, MasterworksFair::getCommonPlayerEntries, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5)),
    RARE("Rare", RareWeapon.class, ChatColor.BLUE, MasterworksFair::getRarePlayerEntries, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 3)),
    EPIC("Epic", EpicWeapon.class, ChatColor.DARK_PURPLE, MasterworksFair::getEpicPlayerEntries, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 2)),
    LEGENDARY("Legendary", LegendaryWeapon.class, ChatColor.GOLD, null, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1));

    public final String name;
    public final Class<?> weaponClass;
    public final ChatColor chatColor;
    public final Function<MasterworksFair, List<MasterworksFairPlayerEntry>> getPlayerEntries;
    public final ItemStack glassItem;

    WeaponsPvE(String name, Class<?> weaponClass, ChatColor chatColor, Function<MasterworksFair, List<MasterworksFairPlayerEntry>> getPlayerEntries, ItemStack glassItem) {
        this.weaponClass = weaponClass;
        this.chatColor = chatColor;
        this.name = name;
        this.getPlayerEntries = getPlayerEntries;
        this.glassItem = glassItem;
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
