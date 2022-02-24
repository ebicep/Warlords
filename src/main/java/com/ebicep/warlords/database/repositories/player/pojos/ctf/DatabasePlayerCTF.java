package com.ebicep.warlords.database.repositories.player.pojos.ctf;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayers;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.classses.*;
import com.ebicep.warlords.game.MapCategory;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.ClassesGroup;
import org.bukkit.GameMode;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "Players_Information")
public class DatabasePlayerCTF extends AbstractDatabaseStatInformation implements com.ebicep.warlords.database.repositories.player.pojos.DatabasePlayer {

    @Field("flags_captured")
    private int flagsCaptured = 0;
    @Field("flags_returned")
    private int flagsReturned = 0;
    @Field("total_blocks_travelled")
    private long totalBlocksTravelled = 0;
    @Field("most_blocks_travelled")
    private long mostBlocksTravelled = 0;
    @Field("total_time_in_respawn")
    private long totalTimeInRespawn = 0;
    @Field("total_time_played")
    private long totalTimePlayed = 0;
    private DatabaseMageCTF mage = new DatabaseMageCTF();
    private DatabaseWarriorCTF warrior = new DatabaseWarriorCTF();
    private DatabasePaladinCTF paladin = new DatabasePaladinCTF();
    private DatabaseShamanCTF shaman = new DatabaseShamanCTF();
    private DatabaseRogueCTF rogue = new DatabaseRogueCTF();

    public DatabasePlayerCTF() {

    }

    @Override
    public void updateCustomStats(MapCategory mapCategory, boolean isCompGame, DatabaseGame databaseGame, DatabaseGamePlayers.GamePlayer gamePlayer, boolean won, boolean add) {
        //UPDATE UNIVERSAL EXPERIENCE
        this.experience += add ? gamePlayer.getExperienceEarnedUniversal() : -gamePlayer.getExperienceEarnedUniversal();

        this.flagsCaptured += gamePlayer.getFlagCaptures();
        this.flagsReturned += gamePlayer.getFlagReturns();
        this.totalBlocksTravelled += gamePlayer.getBlocksTravelled();
        if (this.mostBlocksTravelled < gamePlayer.getBlocksTravelled()) {
            this.mostBlocksTravelled = gamePlayer.getBlocksTravelled();
        }
        this.totalTimeInRespawn += gamePlayer.getSecondsInRespawn();
        this.totalTimePlayed += 900 - databaseGame.getTimeLeft();
        //UPDATE CLASS, SPEC
        this.getClass(Classes.getClassesGroup(gamePlayer.getSpec())).updateStats(mapCategory, isCompGame, databaseGame, gamePlayer, won, add);
        this.getSpec(gamePlayer.getSpec()).updateStats(mapCategory, isCompGame, databaseGame, gamePlayer, won, add);
    }

    @Override
    public DatabaseBaseCTF getSpec(Classes classes) {
        switch (classes) {
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
    public DatabaseBaseCTF getClass(ClassesGroup classesGroup) {
        switch (classesGroup) {
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

    //    public DatabaseWarlordsClassCTF getClass(DatabaseWarlordsClassCTF databaseWarlordsClassCTF) {
//        for (DatabaseWarlordsClassCTF aClass : getClasses()) {
//            if (databaseWarlordsClassCTF.getClass().equals(aClass.getClass())) {
//                return aClass;
//            }
//        }
//        return null;
//    }

    public int getFlagsCaptured() {
        return flagsCaptured;
    }

    public void setFlagsCaptured(int flagsCaptured) {
        this.flagsCaptured = flagsCaptured;
    }

    public int getFlagsReturned() {
        return flagsReturned;
    }

    public void setFlagsReturned(int flagsReturned) {
        this.flagsReturned = flagsReturned;
    }

    public long getTotalBlocksTravelled() {
        return totalBlocksTravelled;
    }

    public void addTotalBlocksTravelled(long totalBlocksTravelled) {
        this.totalBlocksTravelled += totalBlocksTravelled;
    }

    public long getMostBlocksTravelled() {
        return mostBlocksTravelled;
    }

    public void setMostBlocksTravelled(long mostBlocksTravelled) {
        this.mostBlocksTravelled = mostBlocksTravelled;
    }

    public long getTotalTimeInRespawn() {
        return totalTimeInRespawn;
    }

    public void addTotalTimeInRespawn(long totalTimeInRespawn) {
        this.totalTimeInRespawn += totalTimeInRespawn;
    }

    public long getTotalTimePlayed() {
        return totalTimePlayed;
    }

    public void addTotalTimePlayed(long totalTimePlayed) {
        this.totalTimePlayed += totalTimePlayed;
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
