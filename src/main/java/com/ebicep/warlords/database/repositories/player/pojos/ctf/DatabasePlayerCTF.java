package com.ebicep.warlords.database.repositories.player.pojos.ctf;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.ClassesGroup;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "Players_Information")
public class DatabasePlayerCTF extends AbstractDatabaseStatInformation implements com.ebicep.warlords.database.repositories.player.pojos.DatabasePlayer {

    @Field("flags_captured")
    private int flagsCaptured = 0;
    @Field("flags_returned")
    private int flagsReturned = 0;
    private DatabaseMageCTF mage = new DatabaseMageCTF();
    private DatabaseWarriorCTF warrior = new DatabaseWarriorCTF();
    private DatabasePaladinCTF paladin = new DatabasePaladinCTF();
    private DatabaseShamanCTF shaman = new DatabaseShamanCTF();

    public DatabasePlayerCTF() {
    }

    @Override
    public DatabaseSpecializationCTF getSpec(Classes classes) {
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
        }
        return null;
    }

    @Override
    public AbstractDatabaseStatInformation getClass(ClassesGroup classesGroup) {
        switch (classesGroup) {
            case MAGE:
                return mage;
            case WARRIOR:
                return warrior;
            case PALADIN:
                return paladin;
            case SHAMAN:
                return shaman;
        }
        return null;
    }

    @Override
    public AbstractDatabaseWarlordsClassCTF[] getClasses() {
        return new AbstractDatabaseWarlordsClassCTF[]{mage, warrior, paladin, shaman};
    }

//    public void updateStats(DatabaseGamePlayers.GamePlayer gamePlayer, boolean won, boolean add) {
//        int operation = add ? 1 : -1;
//        this.kills += gamePlayer.getTotalKills() * operation;
//        this.assists += gamePlayer.getTotalAssists() * operation;
//        this.deaths += gamePlayer.getTotalDeaths() * operation;
//        if (won) {
//            this.wins += operation;
//        } else {
//            this.losses += operation;
//        }
//        this.plays += operation;
//        this.flagsCaptured += gamePlayer.getFlagCaptures() * operation;
//        this.flagsReturned += gamePlayer.getFlagReturns() * operation;
//        this.damage += gamePlayer.getTotalDamage() * operation;
//        this.healing += gamePlayer.getTotalHealing() * operation;
//        this.absorbed += gamePlayer.getTotalAbsorbed() * operation;
//        this.experience += gamePlayer.getExperienceEarnedUniversal() * operation;
//
//    }

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

}
