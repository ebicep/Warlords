package com.ebicep.warlords.database.newdb.repositories.player.pojos;

import com.ebicep.warlords.player.ArmorManager;

public class DatabaseMage extends DatabaseWarlordsClass {

    protected DatabaseSpecialization pyromancer = new DatabaseSpecialization();
    protected DatabaseSpecialization cryomancer = new DatabaseSpecialization();
    protected DatabaseSpecialization aquamancer = new DatabaseSpecialization();
    protected ArmorManager.Helmets helmet = ArmorManager.Helmets.SIMPLE_MAGE_HELMET;
    protected ArmorManager.ArmorSets armor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE_MAGE;

    public DatabaseMage() {
        super();
    }

    public DatabaseSpecialization getPyromancer() {
        return pyromancer;
    }

    public DatabaseSpecialization getCryomancer() {
        return cryomancer;
    }

    public DatabaseSpecialization getAquamancer() {
        return aquamancer;
    }

    public ArmorManager.Helmets getHelmet() {
        return helmet;
    }

    public ArmorManager.ArmorSets getArmor() {
        return armor;
    }
}
