package com.ebicep.warlords.database.newdb.repositories.player.pojos;

import com.ebicep.warlords.player.ArmorManager;

public class DatabasePaladin extends DatabaseWarlordsClass {

    private DatabaseSpecialization avenger = new DatabaseSpecialization();
    private DatabaseSpecialization crusader = new DatabaseSpecialization();
    private DatabaseSpecialization protector = new DatabaseSpecialization();
    protected ArmorManager.Helmets helmet = ArmorManager.Helmets.SIMPLE_PALADIN_HELMET;
    protected ArmorManager.ArmorSets armor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE_PALADIN;

    public DatabasePaladin() {
        super();
    }

    public DatabaseSpecialization getAvenger() {
        return avenger;
    }

    public DatabaseSpecialization getCrusader() {
        return crusader;
    }

    public DatabaseSpecialization getProtector() {
        return protector;
    }

    public ArmorManager.Helmets getHelmet() {
        return helmet;
    }

    public ArmorManager.ArmorSets getArmor() {
        return armor;
    }
}
