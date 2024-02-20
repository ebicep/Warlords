package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;

public class DatabasePlayerPvEEventLibraryArchivesGrimoiresGraveyardPlayerCountStats extends PvEEventLibraryArchivesGrimoiresGraveyardDatabaseStatInformation implements DatabaseWarlordsClasses<PvEEventLibraryArchivesGrimoiresGraveyardDatabaseStatInformation> {

    private DatabaseMagePvEEventLibraryArchivesGrimoiresGraveyard mage = new DatabaseMagePvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseWarriorPvEEventLibraryArchivesGrimoiresGraveyard warrior = new DatabaseWarriorPvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabasePaladinPvEEventLibraryArchivesGrimoiresGraveyard paladin = new DatabasePaladinPvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseShamanPvEEventLibraryArchivesGrimoiresGraveyard shaman = new DatabaseShamanPvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseRoguePvEEventLibraryArchivesGrimoiresGraveyard rogue = new DatabaseRoguePvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseArcanistPvEEventLibraryArchivesGrimoiresGraveyard arcanist = new DatabaseArcanistPvEEventLibraryArchivesGrimoiresGraveyard();

    public DatabasePlayerPvEEventLibraryArchivesGrimoiresGraveyardPlayerCountStats() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGameBase databaseGame,
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
    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard getSpec(Specializations specializations) {
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
    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard getClass(Classes classes) {
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
    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard[] getClasses() {
        return new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard[]{mage, warrior, paladin, shaman, rogue};
    }

    @Override
    public PvEEventLibraryArchivesGrimoiresGraveyardDatabaseStatInformation getMage() {
        return mage;
    }

    @Override
    public PvEEventLibraryArchivesGrimoiresGraveyardDatabaseStatInformation getWarrior() {
        return warrior;
    }

    @Override
    public PvEEventLibraryArchivesGrimoiresGraveyardDatabaseStatInformation getPaladin() {
        return paladin;
    }

    @Override
    public PvEEventLibraryArchivesGrimoiresGraveyardDatabaseStatInformation getShaman() {
        return shaman;
    }

    @Override
    public PvEEventLibraryArchivesGrimoiresGraveyardDatabaseStatInformation getRogue() {
        return rogue;
    }

    @Override
    public PvEEventLibraryArchivesGrimoiresGraveyardDatabaseStatInformation getArcanist() {
        return arcanist;
    }

    public void merge(DatabasePlayerPvEEventLibraryArchivesGrimoiresGraveyardPlayerCountStats other) {
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
