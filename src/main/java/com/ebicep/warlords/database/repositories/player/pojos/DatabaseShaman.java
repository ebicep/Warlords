package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.player.ArmorManager;

public class DatabaseShaman extends DatabaseWarlordsClass {

    private DatabaseSpecialization thunderlord = new DatabaseSpecialization();
    private DatabaseSpecialization spiritguard = new DatabaseSpecialization();
    private DatabaseSpecialization earthwarden = new DatabaseSpecialization();
    protected ArmorManager.Helmets helmet = ArmorManager.Helmets.SIMPLE_SHAMAN_HELMET;
    protected ArmorManager.ArmorSets armor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE_SHAMAN;

    public DatabaseShaman() {
        super();
    }

    @Override
    public DatabaseSpecialization[] getSpecs() {
        return new DatabaseSpecialization[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseSpecialization getThunderlord() {
        return thunderlord;
    }

    public DatabaseSpecialization getSpiritguard() {
        return spiritguard;
    }

    public DatabaseSpecialization getEarthwarden() {
        return earthwarden;
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
