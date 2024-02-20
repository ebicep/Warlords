package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;

public class DatabasePlayerPvEEventLibraryForgottenCodexPlayerCountStats extends PvEEventLibraryForgottenCodexDatabaseStatInformation implements DatabaseWarlordsClasses<PvEEventLibraryForgottenCodexDatabaseStatInformation> {

    private DatabaseMagePvEEventLibraryForgottenCodex mage = new DatabaseMagePvEEventLibraryForgottenCodex();
    private DatabaseWarriorPvEEventLibraryForgottenCodex warrior = new DatabaseWarriorPvEEventLibraryForgottenCodex();
    private DatabasePaladinPvEEventLibraryForgottenCodex paladin = new DatabasePaladinPvEEventLibraryForgottenCodex();
    private DatabaseShamanPvEEventLibraryForgottenCodex shaman = new DatabaseShamanPvEEventLibraryForgottenCodex();
    private DatabaseRoguePvEEventLibraryForgottenCodex rogue = new DatabaseRoguePvEEventLibraryForgottenCodex();
    private DatabaseArcanistPvEEventLibraryForgottenCodex arcanist = new DatabaseArcanistPvEEventLibraryForgottenCodex();

    public DatabasePlayerPvEEventLibraryForgottenCodexPlayerCountStats() {
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
    public DatabaseBasePvEEventLibraryForgottenCodex getSpec(Specializations specializations) {
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
    public DatabaseBasePvEEventLibraryForgottenCodex getClass(Classes classes) {
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
    public DatabaseBasePvEEventLibraryForgottenCodex[] getClasses() {
        return new DatabaseBasePvEEventLibraryForgottenCodex[]{mage, warrior, paladin, shaman, rogue};
    }

    @Override
    public PvEEventLibraryForgottenCodexDatabaseStatInformation getMage() {
        return mage;
    }

    @Override
    public PvEEventLibraryForgottenCodexDatabaseStatInformation getWarrior() {
        return warrior;
    }

    @Override
    public PvEEventLibraryForgottenCodexDatabaseStatInformation getPaladin() {
        return paladin;
    }

    @Override
    public PvEEventLibraryForgottenCodexDatabaseStatInformation getShaman() {
        return shaman;
    }

    @Override
    public PvEEventLibraryForgottenCodexDatabaseStatInformation getRogue() {
        return rogue;
    }

    @Override
    public PvEEventLibraryForgottenCodexDatabaseStatInformation getArcanist() {
        return arcanist;
    }

    public void merge(DatabasePlayerPvEEventLibraryForgottenCodexPlayerCountStats other) {
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
