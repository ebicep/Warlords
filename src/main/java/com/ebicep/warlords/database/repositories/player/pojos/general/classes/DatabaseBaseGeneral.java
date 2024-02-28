package com.ebicep.warlords.database.repositories.player.pojos.general.classes;

import com.ebicep.warlords.player.general.ArmorManager;

public abstract class DatabaseBaseGeneral {

    protected ArmorManager.Helmets helmet;
    protected ArmorManager.ArmorSets armor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE;

    public DatabaseBaseGeneral() {
    }

    public DatabaseBaseGeneral(ArmorManager.Helmets helmet) {
        this.helmet = helmet;
    }

    public ArmorManager.Helmets getHelmet() {
        return helmet;
    }

    public ArmorManager.ArmorSets getArmor() {
        return armor;
    }

    public void setHelmet(ArmorManager.Helmets helmet) {
        this.helmet = helmet;
    }

    public void setArmor(ArmorManager.ArmorSets armor) {
        this.armor = armor;
    }
}
