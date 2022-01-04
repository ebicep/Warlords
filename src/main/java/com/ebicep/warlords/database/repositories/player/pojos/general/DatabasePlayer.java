package com.ebicep.warlords.database.repositories.player.pojos.general;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.ClassesGroup;
import com.ebicep.warlords.player.Settings;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "Test")
public class DatabasePlayer extends AbstractDatabaseStatInformation implements com.ebicep.warlords.database.repositories.player.pojos.DatabasePlayer {

    @Id
    private String id;

    @Indexed(unique = true)
    private String uuid = "";
    private String name = "";
    private DatabaseMage mage = new DatabaseMage();
    private DatabaseWarrior warrior = new DatabaseWarrior();
    private DatabasePaladin paladin = new DatabasePaladin();
    private DatabaseShaman shaman = new DatabaseShaman();
    @Field("comp_stats")
    private DatabasePlayerCompStats compStats = new DatabasePlayerCompStats();
    @Field("public_queue_stats")
    private DatabasePlayerPubStats pubStats = new DatabasePlayerPubStats();
    @Field("last_spec")
    private Classes lastSpec = Classes.PYROMANCER;
    @Field("hotkeymode")
    private Settings.HotkeyMode hotkeyMode = Settings.HotkeyMode.NEW_MODE;
    @Field("particle_quality")
    private Settings.ParticleQuality particleQuality = Settings.ParticleQuality.HIGH;

    public DatabasePlayer() {
    }

    public DatabasePlayer(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public DatabaseSpecialization getSpec(Classes classes) {
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
    public AbstractDatabaseStatInformation[] getClasses() {
        return new AbstractDatabaseStatInformation[]{mage, warrior, paladin, shaman};
    }

    //    public AbstractDatabaseWarlordsClass getClass(AbstractDatabaseWarlordsClass databaseWarlordsClass) {
//        for (AbstractDatabaseWarlordsClass aClass : getClasses()) {
//            if (databaseWarlordsClass.getClass().equals(aClass.getClass())) {
//                return aClass;
//            }
//        }
//        return null;
//    }

    @Override
    public String toString() {
        return "DatabasePlayer{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

//    public void updateStats(GameMode gameMode, boolean isCompGame, DatabaseGamePlayers.GamePlayer gamePlayer, boolean won, boolean add) {
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
//        this.damage += gamePlayer.getTotalDamage() * operation;
//        this.healing += gamePlayer.getTotalHealing() * operation;
//        this.absorbed += gamePlayer.getTotalAbsorbed() * operation;
//        this.experience += gamePlayer.getExperienceEarnedUniversal() * operation;
//        if(isCompGame) {
//            switch (gameMode) {
//                case CAPTURE_THE_FLAG:
//                    compStats.updateStats(gamePlayer, won, add);
//                    break;
//            }
//        } else {
//
//        }
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public DatabaseMage getMage() {
        return mage;
    }

    public void setMage(DatabaseMage mage) {
        this.mage = mage;
    }

    public DatabaseWarrior getWarrior() {
        return warrior;
    }

    public void setWarrior(DatabaseWarrior warrior) {
        this.warrior = warrior;
    }

    public DatabasePaladin getPaladin() {
        return paladin;
    }

    public void setPaladin(DatabasePaladin paladin) {
        this.paladin = paladin;
    }

    public DatabaseShaman getShaman() {
        return shaman;
    }

    public void setShaman(DatabaseShaman shaman) {
        this.shaman = shaman;
    }

    public DatabasePlayerCompStats getCompStats() {
        return compStats;
    }

    public void setCompStats(DatabasePlayerCompStats compStats) {
        this.compStats = compStats;
    }

    public DatabasePlayerPubStats getPubStats() {
        return pubStats;
    }

    public void setPubStats(DatabasePlayerPubStats pubStats) {
        this.pubStats = pubStats;
    }

    public Classes getLastSpec() {
        return lastSpec;
    }

    public void setLastSpec(Classes lastSpec) {
        this.lastSpec = lastSpec;
    }

    public Settings.HotkeyMode getHotkeyMode() {
        return hotkeyMode;
    }

    public void setHotkeyMode(Settings.HotkeyMode hotkeyMode) {
        this.hotkeyMode = hotkeyMode;
    }

    public Settings.ParticleQuality getParticleQuality() {
        return particleQuality;
    }

    public void setParticleQuality(Settings.ParticleQuality particleQuality) {
        this.particleQuality = particleQuality;
    }

}
