package com.ebicep.warlords.database.repositories.player.pojos.pve;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.pve.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;

public class DatabasePlayerPvEPlayerCountStats extends PvEDatabaseStatInformation implements DatabaseWarlordsClasses<PvEDatabaseStatInformation> {

    private DatabaseMagePvE mage = new DatabaseMagePvE();
    private DatabaseWarriorPvE warrior = new DatabaseWarriorPvE();
    private DatabasePaladinPvE paladin = new DatabasePaladinPvE();
    private DatabaseShamanPvE shaman = new DatabaseShamanPvE();
    private DatabaseRoguePvE rogue = new DatabaseRoguePvE();
    private DatabaseDruidPvE druid = new DatabaseDruidPvE();

    public DatabasePlayerPvEPlayerCountStats() {
    }

    @Override
    public void updateCustomStats(
            com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer databasePlayer, DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        assert gamePlayer instanceof DatabaseGamePlayerPvEBase;

        super.updateCustomStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

        //UPDATE UNIVERSAL EXPERIENCE
        this.experience += gamePlayer.getExperienceEarnedUniversal() * multiplier;

        //UPDATE CLASS, SPEC
        this.getClass(Specializations.getClass(gamePlayer.getSpec())).updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        this.getSpec(gamePlayer.getSpec()).updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
    }

    @Override
    public PvEDatabaseStatInformation getSpec(Specializations specializations) {
        return switch (specializations) {
            case PYROMANCER -> mage.getPyromancer();
            case CRYOMANCER -> mage.getCryomancer();
            case AQUAMANCER -> mage.getAquamancer();
            case BERSERKER -> warrior.getBerserker();
            case DEFENDER -> warrior.getDefender();
            case REVENANT -> warrior.getRevenant();
            case AVENGER -> paladin.getAvenger();
            case CRUSADER -> paladin.getCrusader();
            case PROTECTOR -> paladin.getProtector();
            case THUNDERLORD -> shaman.getThunderlord();
            case SPIRITGUARD -> shaman.getSpiritguard();
            case EARTHWARDEN -> shaman.getEarthwarden();
            case ASSASSIN -> rogue.getAssassin();
            case VINDICATOR -> rogue.getVindicator();
            case APOTHECARY -> rogue.getApothecary();
            case CONJURER -> druid.getConjurer();
            case GUARDIAN -> druid.getGuardian();
            case PRIEST -> druid.getPriest();
        };
    }

    @Override
    public PvEDatabaseStatInformation getClass(Classes classes) {
        return switch (classes) {
            case MAGE -> mage;
            case WARRIOR -> warrior;
            case PALADIN -> paladin;
            case SHAMAN -> shaman;
            case ROGUE -> rogue;
            case DRUID -> druid;
        };
    }

    @Override
    public DatabaseBasePvE[] getClasses() {
        return new DatabaseBasePvE[]{mage, warrior, paladin, shaman, rogue};
    }

    @Override
    public PvEDatabaseStatInformation getMage() {
        return mage;
    }

    @Override
    public PvEDatabaseStatInformation getWarrior() {
        return warrior;
    }

    @Override
    public PvEDatabaseStatInformation getPaladin() {
        return paladin;
    }

    @Override
    public PvEDatabaseStatInformation getShaman() {
        return shaman;
    }

    @Override
    public PvEDatabaseStatInformation getRogue() {
        return rogue;
    }

    public void merge(DatabasePlayerPvEPlayerCountStats other) {
        super.merge(other);
        mage.merge(other.mage);
        warrior.merge(other.warrior);
        paladin.merge(other.paladin);
        shaman.merge(other.shaman);
        rogue.merge(other.rogue);
        for (Classes value : Classes.VALUES) {
            this.getClass(value).merge(other.getClass(value));
        }
        for (Specializations value : Specializations.VALUES) {
            this.getSpec(value).merge(other.getSpec(value));
        }
    }

}
