package com.ebicep.warlords.database.repositories.player.pojos.ctf;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.classses.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;

public class DatabasePlayerCTF extends CTFDatabaseStatInformation implements com.ebicep.warlords.database.repositories.player.pojos.DatabasePlayer {

    private DatabaseMageCTF mage = new DatabaseMageCTF();
    private DatabaseWarriorCTF warrior = new DatabaseWarriorCTF();
    private DatabasePaladinCTF paladin = new DatabasePaladinCTF();
    private DatabaseShamanCTF shaman = new DatabaseShamanCTF();
    private DatabaseRogueCTF rogue = new DatabaseRogueCTF();

    @Override
    public void updateCustomStats(
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier
    ) {
        super.updateCustomStats(databaseGame, gameMode, gamePlayer, result, multiplier);

        //UPDATE UNIVERSAL EXPERIENCE
        this.experience += gamePlayer.getExperienceEarnedUniversal() * multiplier;

        //UPDATE CLASS, SPEC
        this.getClass(Specializations.getClass(gamePlayer.getSpec())).updateStats(databaseGame, gamePlayer, multiplier);
        this.getSpec(gamePlayer.getSpec()).updateStats(databaseGame, gamePlayer, multiplier);
    }

    @Override
    public DatabaseBaseCTF getSpec(Specializations specializations) {
        switch (specializations) {
            case PYROMANCER:
                return mage.getPyromancer();
            case CRYOMANCER:
                return mage.getCryomancer();
            case AQUAMANCER:
                return mage.getAquamancer();
            case BERSERKER:
                return warrior.getBerserker();
            case DEFENDER:
                return warrior.getDefender();
            case REVENANT:
                return warrior.getRevenant();
            case AVENGER:
                return paladin.getAvenger();
            case CRUSADER:
                return paladin.getCrusader();
            case PROTECTOR:
                return paladin.getProtector();
            case THUNDERLORD:
                return shaman.getThunderlord();
            case SPIRITGUARD:
                return shaman.getSpiritguard();
            case EARTHWARDEN:
                return shaman.getEarthwarden();
            case ASSASSIN:
                return rogue.getAssassin();
            case VINDICATOR:
                return rogue.getVindicator();
            case APOTHECARY:
                return rogue.getApothecary();
        }
        return null;
    }

    @Override
    public DatabaseBaseCTF getClass(Classes classes) {
        switch (classes) {
            case MAGE:
                return mage;
            case WARRIOR:
                return warrior;
            case PALADIN:
                return paladin;
            case SHAMAN:
                return shaman;
            case ROGUE:
                return rogue;
        }
        return null;
    }

    @Override
    public DatabaseBaseCTF[] getClasses() {
        return new DatabaseBaseCTF[]{mage, warrior, paladin, shaman, rogue};
    }

    public DatabaseMageCTF getMage() {
        return mage;
    }

    public DatabaseWarriorCTF getWarrior() {
        return warrior;
    }

    public DatabasePaladinCTF getPaladin() {
        return paladin;
    }

    public DatabaseShamanCTF getShaman() {
        return shaman;
    }

    public DatabaseRogueCTF getRogue() {
        return rogue;
    }
}
