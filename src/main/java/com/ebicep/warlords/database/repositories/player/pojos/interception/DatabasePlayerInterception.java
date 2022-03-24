package com.ebicep.warlords.database.repositories.player.pojos.interception;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGameInterception;
import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGamePlayersInterception;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.interception.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.Specializations;
import org.springframework.data.mongodb.core.mapping.Field;

public class DatabasePlayerInterception extends AbstractDatabaseStatInformation implements com.ebicep.warlords.database.repositories.player.pojos.DatabasePlayer {

    @Field("points_captured")
    private int pointsCaptured;
    @Field("points_defended")
    private int pointsDefended;
    @Field("total_time_played")
    private long totalTimePlayed = 0;
    private DatabaseMageInterception mage = new DatabaseMageInterception();
    private DatabaseWarriorInterception warrior = new DatabaseWarriorInterception();
    private DatabasePaladinInterception paladin = new DatabasePaladinInterception();
    private DatabaseShamanInterception shaman = new DatabaseShamanInterception();
    private DatabaseRogueInterception rogue = new DatabaseRogueInterception();

    @Override
    public void updateCustomStats(DatabaseGameBase databaseGame, GameMode gameMode, DatabaseGamePlayerBase gamePlayer, DatabaseGamePlayerResult result, boolean isCompGame, boolean add) {
        assert databaseGame instanceof DatabaseGameInterception;
        assert gamePlayer instanceof DatabaseGamePlayersInterception.DatabaseGamePlayerInterception;

        //UPDATE UNIVERSAL EXPERIENCE
        this.experience += add ? gamePlayer.getExperienceEarnedUniversal() : -gamePlayer.getExperienceEarnedUniversal();

        this.pointsCaptured += ((DatabaseGamePlayersInterception.DatabaseGamePlayerInterception) gamePlayer).getPointsCaptured();
        this.pointsDefended += ((DatabaseGamePlayersInterception.DatabaseGamePlayerInterception) gamePlayer).getPointsDefended();
        this.totalTimePlayed += 900 - ((DatabaseGameInterception) databaseGame).getTimeLeft();
        //UPDATE CLASS, SPEC
        this.getClass(Specializations.getClass(gamePlayer.getSpec())).updateStats(databaseGame, gamePlayer, add);
        this.getSpec(gamePlayer.getSpec()).updateStats(databaseGame, gamePlayer, add);
    }

    @Override
    public DatabaseBaseInterception getSpec(Specializations specializations) {
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
    public DatabaseBaseInterception getClass(Classes classes) {
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
    public DatabaseBaseInterception[] getClasses() {
        return new DatabaseBaseInterception[]{mage, warrior, paladin, shaman, rogue};
    }
}
