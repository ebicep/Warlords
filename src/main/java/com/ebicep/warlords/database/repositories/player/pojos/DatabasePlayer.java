package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayers;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.ClassesGroup;
import com.ebicep.warlords.player.Settings;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Document(collection = "Players_Information")
public class DatabasePlayer {
    @Id
    private String id;

    @Indexed(unique = true)
    private String uuid = "";
    private String name = "";
    private int kills = 0;
    private int assists = 0;
    private int deaths = 0;
    private int wins = 0;
    private int losses = 0;
    private int plays = 0;
    @Field("flags_captured")
    private int flagsCaptured = 0;
    @Field("flags_returned")
    private int flagsReturned = 0;
    private long damage = 0;
    private long healing = 0;
    private long absorbed = 0;
    private DatabaseMage mage = new DatabaseMage();
    private DatabaseWarrior warrior = new DatabaseWarrior();
    private DatabasePaladin paladin = new DatabasePaladin();
    private DatabaseShaman shaman = new DatabaseShaman();
    @Field("last_spec")
    private Classes lastSpec = Classes.PYROMANCER;
    @Field("hotkeymode")
    private Settings.HotkeyMode hotkeyMode = Settings.HotkeyMode.NEW_MODE;
    @Field("particle_quality")
    private Settings.ParticleQuality particleQuality = Settings.ParticleQuality.HIGH;
    private long experience = 0;

    public DatabasePlayer() {
    }

    public DatabasePlayer(UUID uuid, String name) {
        this.uuid = uuid.toString();
        this.name = name;
    }

    public void updateStats(DatabaseGamePlayers.GamePlayer gamePlayer, boolean won, boolean add) {
        int operation = add ? 1 : -1;
        this.kills += gamePlayer.getTotalKills() * operation;
        this.assists += gamePlayer.getTotalAssists() * operation;
        this.deaths += gamePlayer.getTotalDeaths() * operation;
        if(won) {
            this.wins += operation;
        } else {
            this.losses += operation;
        }
        this.plays += operation;
        this.flagsCaptured += gamePlayer.getFlagCaptures() * operation;
        this.flagsReturned += gamePlayer.getFlagReturns() * operation;
        this.damage += gamePlayer.getTotalDamage() * operation;
        this.healing += gamePlayer.getTotalHealing() * operation;
        this.absorbed += gamePlayer.getTotalAbsorbed() * operation;
        this.experience += gamePlayer.getExperienceEarnedUniversal() * operation;
    }


    @Override
    public String toString() {
        return "DatabasePlayer{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public double getKillsPerGame() {
        return plays == 0 ? 0 : (double) kills / plays;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public double getKillsAssistsPerGame() {
        return plays == 0 ? 0 : (double) (kills + assists) / plays;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public double getDeathsPerGame() {
        return plays == 0 ? 0 : (double) deaths / plays;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getPlays() {
        return plays;
    }

    public void setPlays(int plays) {
        this.plays = plays;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

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

    public long getDHP() {
        return damage + healing + absorbed;
    }

    public long getDHPPerGame() {
        return plays == 0 ? 0 : (damage + healing + absorbed) / (wins + losses);
    }

    public long getDamage() {
        return damage;
    }

    public void setDamage(long damage) {
        this.damage = damage;
    }

    public long getHealing() {
        return healing;
    }

    public void setHealing(long healing) {
        this.healing = healing;
    }

    public long getAbsorbed() {
        return absorbed;
    }

    public void setAbsorbed(long absorbed) {
        this.absorbed = absorbed;
    }

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

    public DatabaseWarlordsClass getClass(ClassesGroup classesGroup) {
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

    public DatabaseMage getMage() {
        return mage;
    }

    public DatabaseWarrior getWarrior() {
        return warrior;
    }

    public DatabasePaladin getPaladin() {
        return paladin;
    }

    public DatabaseShaman getShaman() {
        return shaman;
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

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }
}
