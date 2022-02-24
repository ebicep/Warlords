package com.ebicep.warlords.database.repositories.player.pojos.general.classes;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayers;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabaseSpecialization;
import com.ebicep.warlords.game.MapCategory;
import com.ebicep.warlords.player.ArmorManager;
import com.ebicep.warlords.player.ClassesSkillBoosts;
import org.bukkit.GameMode;

public class DatabaseRogue extends AbstractDatabaseStatInformation implements DatabaseWarlordsClass {

    protected DatabaseSpecialization assassin = new DatabaseSpecialization(ClassesSkillBoosts.FIREBALL);
    protected DatabaseSpecialization vindicator = new DatabaseSpecialization(ClassesSkillBoosts.FROST_BOLT);
    protected DatabaseSpecialization apothecary = new DatabaseSpecialization(ClassesSkillBoosts.WATER_BOLT);
    protected ArmorManager.Helmets helmet = ArmorManager.Helmets.SIMPLE_ROGUE_HELMET;
    protected ArmorManager.ArmorSets armor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE; //TODO change to rogue

    public DatabaseRogue() {
        super();
    }

    @Override
    public void updateCustomStats(MapCategory mapCategory, boolean isCompGame, DatabaseGame databaseGame, DatabaseGamePlayers.GamePlayer gamePlayer, boolean won, boolean add) {
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
