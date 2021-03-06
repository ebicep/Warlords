package com.ebicep.warlords.database.repositories.player.pojos.general.classes;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabaseSpecialization;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.ArmorManager;
import com.ebicep.warlords.player.SkillBoosts;

public class DatabaseRogue extends AbstractDatabaseStatInformation implements DatabaseWarlordsClass {

    protected DatabaseSpecialization assassin = new DatabaseSpecialization(SkillBoosts.FIREBALL);
    protected DatabaseSpecialization vindicator = new DatabaseSpecialization(SkillBoosts.FROST_BOLT);
    protected DatabaseSpecialization apothecary = new DatabaseSpecialization(SkillBoosts.WATER_BOLT);
    protected ArmorManager.Helmets helmet = ArmorManager.Helmets.SIMPLE_ROGUE_HELMET;
    protected ArmorManager.ArmorSets armor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE_ROGUE;

    public DatabaseRogue() {
        super();
    }

    @Override
    public void updateCustomStats(DatabaseGameBase databaseGame, GameMode gameMode, DatabaseGamePlayerBase gamePlayer, DatabaseGamePlayerResult result, boolean isCompGame, boolean add) {
        //UPDATE SPEC EXPERIENCE
        this.experience += add ? gamePlayer.getExperienceEarnedSpec() : -gamePlayer.getExperienceEarnedSpec();
    }

    @Override
    public DatabaseSpecialization[] getSpecs() {
        return new DatabaseSpecialization[]{assassin, vindicator, apothecary};
    }

    public DatabaseSpecialization getAssassin() {
        return assassin;
    }

    public DatabaseSpecialization getVindicator() {
        return vindicator;
    }

    public DatabaseSpecialization getApothecary() {
        return apothecary;
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
