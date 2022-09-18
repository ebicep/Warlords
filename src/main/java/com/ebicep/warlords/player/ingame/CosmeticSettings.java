package com.ebicep.warlords.player.ingame;

import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.player.general.Weapons;

public class CosmeticSettings {

    private Weapons weaponSkin;
    private ArmorManager.Helmets helmet;
    private ArmorManager.ArmorSets armorSet;

    public CosmeticSettings(Weapons weaponSkin, ArmorManager.Helmets helmet, ArmorManager.ArmorSets armorSet) {
        this.weaponSkin = weaponSkin;
        this.helmet = helmet;
        this.armorSet = armorSet;
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

}
