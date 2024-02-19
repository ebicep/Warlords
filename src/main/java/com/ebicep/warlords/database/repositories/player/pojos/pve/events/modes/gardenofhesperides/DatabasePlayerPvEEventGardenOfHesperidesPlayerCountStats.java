package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;

public class DatabasePlayerPvEEventGardenOfHesperidesPlayerCountStats extends PvEEventGardenOfHesperidesDatabaseStatInformation implements DatabaseWarlordsClasses<PvEEventGardenOfHesperidesDatabaseStatInformation> {

    private DatabaseMagePvEEventGardenOfHesperides mage = new DatabaseMagePvEEventGardenOfHesperides();
    private DatabaseWarriorPvEEventGardenOfHesperides warrior = new DatabaseWarriorPvEEventGardenOfHesperides();
    private DatabasePaladinPvEEventGardenOfHesperides paladin = new DatabasePaladinPvEEventGardenOfHesperides();
    private DatabaseShamanPvEEventGardenOfHesperides shaman = new DatabaseShamanPvEEventGardenOfHesperides();
    private DatabaseRoguePvEEventGardenOfHesperides rogue = new DatabaseRoguePvEEventGardenOfHesperides();
    private DatabaseArcanistPvEEventGardenOfHesperides arcanist = new DatabaseArcanistPvEEventGardenOfHesperides();

    public DatabasePlayerPvEEventGardenOfHesperidesPlayerCountStats() {
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
        assert databaseGame instanceof DatabaseGamePvEWaveDefense;
        assert gamePlayer instanceof DatabaseGamePlayerPvEWaveDefense;

        super.updateCustomStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

        //UPDATE UNIVERSAL EXPERIENCE
        this.experience += gamePlayer.getExperienceEarnedUniversal() * multiplier;
        this.experiencePvE += gamePlayer.getExperienceEarnedUniversal() * multiplier;

        //UPDATE CLASS, SPEC
        this.getClass(Specializations.getClass(gamePlayer.getSpec())).updateCustomStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        this.getSpec(gamePlayer.getSpec()).updateCustomStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
    }

    @Override
    public DatabaseBasePvEEventGardenOfHesperides getSpec(Specializations specializations) {
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
    public DatabaseBasePvEEventGardenOfHesperides getClass(Classes classes) {
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
    public DatabaseBasePvEEventGardenOfHesperides[] getClasses() {
        return new DatabaseBasePvEEventGardenOfHesperides[]{mage, warrior, paladin, shaman, rogue};
    }

    @Override
    public PvEEventGardenOfHesperidesDatabaseStatInformation getMage() {
        return mage;
    }

    @Override
    public PvEEventGardenOfHesperidesDatabaseStatInformation getWarrior() {
        return warrior;
    }

    @Override
    public PvEEventGardenOfHesperidesDatabaseStatInformation getPaladin() {
        return paladin;
    }

    @Override
    public PvEEventGardenOfHesperidesDatabaseStatInformation getShaman() {
        return shaman;
    }

    @Override
    public PvEEventGardenOfHesperidesDatabaseStatInformation getRogue() {
        return rogue;
    }

    @Override
    public PvEEventGardenOfHesperidesDatabaseStatInformation getArcanist() {
        return arcanist;
    }

    public void merge(DatabasePlayerPvEEventGardenOfHesperidesPlayerCountStats other) {
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
