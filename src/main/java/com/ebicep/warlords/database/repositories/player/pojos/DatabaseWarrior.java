package com.ebicep.warlords.database.repositories.player.pojos;


import com.ebicep.warlords.player.ArmorManager;
import com.ebicep.warlords.player.ClassesSkillBoosts;

public class DatabaseWarrior extends DatabaseWarlordsClass {

    private DatabaseSpecialization berserker = new DatabaseSpecialization(ClassesSkillBoosts.WOUNDING_STRIKE_BERSERKER);
    private DatabaseSpecialization defender = new DatabaseSpecialization(ClassesSkillBoosts.WOUNDING_STRIKE_DEFENDER);
    private DatabaseSpecialization revenant = new DatabaseSpecialization(ClassesSkillBoosts.ORBS_OF_LIFE);
    protected ArmorManager.Helmets helmet = ArmorManager.Helmets.SIMPLE_WARRIOR_HELMET;
    protected ArmorManager.ArmorSets armor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE_WARRIOR;

    public DatabaseWarrior() {
        super();
    }

    @Override
    public DatabaseSpecialization[] getSpecs() {
        return new DatabaseSpecialization[]{berserker, defender, revenant};
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
