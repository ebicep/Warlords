package com.ebicep.warlords.pve.weapons;

import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFair;
import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFairPlayerEntry;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.weapontypes.CommonWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.EpicWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.RareWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Function;

public enum WeaponsPvE {

    NONE("None",
            null,
            NamedTextColor.GRAY,
            null,
            null,
            null,
            0
    ),
    COMMON("Common",
            CommonWeapon.class,
            NamedTextColor.GREEN,
            new ItemStack(Material.LIME_STAINED_GLASS_PANE),
            MasterworksFair::getCommonPlayerEntries,
            Currencies.COMMON_STAR_PIECE,
            50
    ),
    RARE("Rare",
            RareWeapon.class,
            NamedTextColor.BLUE,
            new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE),
            MasterworksFair::getRarePlayerEntries,
            Currencies.RARE_STAR_PIECE,
            100
    ),
    EPIC("Epic",
            EpicWeapon.class,
            NamedTextColor.DARK_PURPLE,
            new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE),
            MasterworksFair::getEpicPlayerEntries,
            Currencies.EPIC_STAR_PIECE,
            200
    ),
    LEGENDARY("Legendary",
            AbstractLegendaryWeapon.class,
            NamedTextColor.GOLD,
            new ItemStack(Material.ORANGE_STAINED_GLASS_PANE),
            null,
            Currencies.LEGENDARY_STAR_PIECE,
            500
    );

    public static final WeaponsPvE[] VALUES = values();
    public final String name;
    public final Class<?> weaponClass;
    public final NamedTextColor textColor;
    public final ItemStack glassItem;
    public final Function<MasterworksFair, List<MasterworksFairPlayerEntry>> getPlayerEntries;
    public final Currencies starPieceCurrency;
    public final int fairyEssenceCost;

    WeaponsPvE(
            String name,
            Class<?> weaponClass,
            NamedTextColor textColor,
            ItemStack glassItem,
            Function<MasterworksFair, List<MasterworksFairPlayerEntry>> getPlayerEntries,
            Currencies starPieceCurrency,
            int fairyEssenceCost
    ) {
        this.weaponClass = weaponClass;
        this.name = name;
        this.textColor = textColor;
        this.getPlayerEntries = getPlayerEntries;
        this.glassItem = glassItem;
        this.starPieceCurrency = starPieceCurrency;
        this.fairyEssenceCost = fairyEssenceCost;
    }

    public WeaponsPvE next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    public Component getTextColoredName() {
        return Component.text(name, textColor);
    }
}
