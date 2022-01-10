package com.ebicep.warlords.database.repositories.player.pojos.general.classes;

import com.ebicep.warlords.database.repositories.games.GameMode;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayers;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabaseSpecialization;
import com.ebicep.warlords.player.ArmorManager;
import com.ebicep.warlords.player.ClassesSkillBoosts;

public class DatabaseShaman extends AbstractDatabaseStatInformation implements DatabaseWarlordsClass {

    private DatabaseSpecialization thunderlord = new DatabaseSpecialization(ClassesSkillBoosts.LIGHTNING_BOLT);
    private DatabaseSpecialization spiritguard = new DatabaseSpecialization(ClassesSkillBoosts.FALLEN_SOULS);
    private DatabaseSpecialization earthwarden = new DatabaseSpecialization(ClassesSkillBoosts.EARTHEN_SPIKE);
    protected ArmorManager.Helmets helmet = ArmorManager.Helmets.SIMPLE_SHAMAN_HELMET;
    protected ArmorManager.ArmorSets armor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE_SHAMAN;

    public DatabaseShaman() {
        super();
    }

    @Override
    public void updateCustomStats(GameMode gameMode, boolean isCompGame, DatabaseGame databaseGame, DatabaseGamePlayers.GamePlayer gamePlayer, boolean won, boolean add) {
        //UPDATE SPEC EXPERIENCE
        this.experience += add ? gamePlayer.getExperienceEarnedSpec() : -gamePlayer.getExperienceEarnedSpec();
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
