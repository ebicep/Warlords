package com.ebicep.warlords.database.repositories.player.pojos;


public class DatabaseWarrior extends DatabaseWarlordsClass {

    private DatabaseSpecialization berserker = new DatabaseSpecialization();
    private DatabaseSpecialization defender = new DatabaseSpecialization();
    private DatabaseSpecialization revenant = new DatabaseSpecialization();
//    protected ArmorManager.Helmets helmet = ArmorManager.Helmets.SIMPLE_WARRIOR_HELMET;
//    protected ArmorManager.ArmorSets armor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE_WARRIOR;

    public DatabaseWarrior() {
        super();
    }

    public DatabaseSpecialization getBerserker() {
        return berserker;
    }

    public DatabaseSpecialization getDefender() {
        return defender;
    }

    public DatabaseSpecialization getRevenant() {
        return revenant;
    }

//    public ArmorManager.Helmets getHelmet() {
//        return helmet;
//    }
//
//    public ArmorManager.ArmorSets getArmor() {
//        return armor;
//    }
}
