package com.ebicep.warlords.database.repositories.player.pojos.general.classes;

import com.ebicep.warlords.database.repositories.games.GameMode;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayers;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabaseSpecialization;
import com.ebicep.warlords.player.ArmorManager;
import com.ebicep.warlords.player.ClassesSkillBoosts;

public class DatabaseWarrior extends AbstractDatabaseStatInformation implements DatabaseWarlordsClass {

    private final DatabaseSpecialization berserker = new DatabaseSpecialization(ClassesSkillBoosts.WOUNDING_STRIKE_BERSERKER);
    private final DatabaseSpecialization defender = new DatabaseSpecialization(ClassesSkillBoosts.WOUNDING_STRIKE_DEFENDER);
    private final DatabaseSpecialization revenant = new DatabaseSpecialization(ClassesSkillBoosts.ORBS_OF_LIFE);
    protected ArmorManager.Helmets helmet = ArmorManager.Helmets.SIMPLE_WARRIOR_HELMET;
    protected ArmorManager.ArmorSets armor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE_WARRIOR;

    public DatabaseWarrior() {
        super();
    }

    @Override
    public void updateCustomStats(GameMode gameMode, boolean isCompGame, DatabaseGame databaseGame, DatabaseGamePlayers.GamePlayer gamePlayer, boolean won, boolean add) {
        //UPDATE SPEC EXPERIENCE
        this.experience += add ? gamePlayer.getExperienceEarnedSpec() : -gamePlayer.getExperienceEarnedSpec();
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
