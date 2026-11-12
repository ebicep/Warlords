package com.ebicep.warlords.player.ingame;

import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.general.armorcustomization.ArmorCustomizationProfile;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class CosmeticSettings {

    private Weapons weaponSkin;
    private ArmorManager.Helmets helmet;
    private ArmorManager.ArmorSets armorSet;
    @Nullable
    private ArmorCustomizationProfile armorCustomization;
    private boolean pvp;
    private boolean supporter;

    public CosmeticSettings(Weapons weaponSkin, ArmorManager.Helmets helmet, ArmorManager.ArmorSets armorSet) {
        this(weaponSkin, helmet, armorSet, null, false, false);
    }

    public CosmeticSettings(Weapons weaponSkin, ArmorManager.Helmets helmet, ArmorManager.ArmorSets armorSet,
                            @Nullable ArmorCustomizationProfile armorCustomization, boolean pvp, boolean supporter) {
        this.weaponSkin = weaponSkin;
        this.helmet = helmet;
        this.armorSet = armorSet;
        this.armorCustomization = armorCustomization;
        this.pvp = pvp;
        this.supporter = supporter;
    }

    public Weapons getWeaponSkin() {
        return weaponSkin;
    }

    public void setWeaponSkin(Weapons weaponSkin) {
        this.weaponSkin = weaponSkin;
    }

    public ArmorManager.Helmets getHelmet() {
        return helmet;
    }

    public void setHelmet(ArmorManager.Helmets helmet) {
        this.helmet = helmet;
    }

    public ArmorManager.ArmorSets getArmorSet() {
        return armorSet;
    }

    public void setArmorSet(ArmorManager.ArmorSets armorSet) {
        this.armorSet = armorSet;
    }

    @Nullable
    public ArmorCustomizationProfile getArmorCustomization() {
        return armorCustomization;
    }

    public boolean isPvp() {
        return pvp;
    }

    public boolean isSupporter() {
        return supporter;
    }

    public ItemStack[] getArmor(Team team) {
        return ArmorManager.getArmor(helmet, armorSet, team, armorCustomization, pvp, supporter);
    }

}
