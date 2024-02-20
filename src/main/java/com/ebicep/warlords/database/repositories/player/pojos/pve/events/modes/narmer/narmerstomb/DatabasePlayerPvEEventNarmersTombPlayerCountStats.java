package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;

public class DatabasePlayerPvEEventNarmersTombPlayerCountStats extends PvEEventNarmersTombDatabaseStatInformation implements DatabaseWarlordsClasses<PvEEventNarmersTombDatabaseStatInformation> {

    private DatabaseMagePvEEventNarmersTomb mage = new DatabaseMagePvEEventNarmersTomb();
    private DatabaseWarriorPvEEventNarmersTomb warrior = new DatabaseWarriorPvEEventNarmersTomb();
    private DatabasePaladinPvEEventNarmersTomb paladin = new DatabasePaladinPvEEventNarmersTomb();
    private DatabaseShamanPvEEventNarmersTomb shaman = new DatabaseShamanPvEEventNarmersTomb();
    private DatabaseRoguePvEEventNarmersTomb rogue = new DatabaseRoguePvEEventNarmersTomb();
    private DatabaseArcanistPvEEventNarmersTomb arcanist = new DatabaseArcanistPvEEventNarmersTomb();

    public DatabasePlayerPvEEventNarmersTombPlayerCountStats() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer, DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        assert databaseGame instanceof DatabaseGamePvEWaveDefense;
        assert gamePlayer instanceof DatabaseGamePlayerPvEWaveDefense;

        super.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

        //UPDATE UNIVERSAL EXPERIENCE
        this.experience += gamePlayer.getExperienceEarnedUniversal() * multiplier;
        this.experiencePvE += gamePlayer.getExperienceEarnedUniversal() * multiplier;

        //UPDATE CLASS, SPEC
        this.getClass(Specializations.getClass(gamePlayer.getSpec())).updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        this.getSpec(gamePlayer.getSpec()).updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
    }

    @Override
    public DatabaseBasePvEEventNarmersTomb getSpec(Specializations specializations) {
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
            case CONJURER -> arcanist.getConjurer();
            case SENTINEL -> arcanist.getSentinel();
            case LUMINARY -> arcanist.getLuminary();
        };
    }

    @Override
    public DatabaseBasePvEEventNarmersTomb getClass(Classes classes) {
        return switch (classes) {
            case MAGE -> mage;
            case WARRIOR -> warrior;
            case PALADIN -> paladin;
            case SHAMAN -> shaman;
            case ROGUE -> rogue;
            case ARCANIST -> arcanist;
        };
    }

    @Override
    public DatabaseBasePvEEventNarmersTomb[] getClasses() {
        return new DatabaseBasePvEEventNarmersTomb[]{mage, warrior, paladin, shaman, rogue};
    }

    @Override
    public PvEEventNarmersTombDatabaseStatInformation getMage() {
        return mage;
    }

    @Override
    public PvEEventNarmersTombDatabaseStatInformation getWarrior() {
        return warrior;
    }

    @Override
    public PvEEventNarmersTombDatabaseStatInformation getPaladin() {
        return paladin;
    }

    @Override
    public PvEEventNarmersTombDatabaseStatInformation getShaman() {
        return shaman;
    }

    @Override
    public PvEEventNarmersTombDatabaseStatInformation getRogue() {
        return rogue;
    }

    @Override
    public PvEEventNarmersTombDatabaseStatInformation getArcanist() {
        return arcanist;
    }

    public void merge(DatabasePlayerPvEEventNarmersTombPlayerCountStats other) {
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
