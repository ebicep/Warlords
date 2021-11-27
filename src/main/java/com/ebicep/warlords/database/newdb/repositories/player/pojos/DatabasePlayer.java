package com.ebicep.warlords.database.newdb.repositories.player.pojos;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.Settings;
import org.bukkit.Bukkit;
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
    @Field("flags_captured")
    private int flagsCaptured = 0;
    @Field("flags_returned")
    private int flagsReturned = 0;
    private long damage = 0;
    private long healing = 0;
    private long absorbed = 0;
    private DatabaseWarlordsClass mage = new DatabaseMage();
    private DatabaseWarlordsClass warrior = new DatabaseWarrior();
    private DatabaseWarlordsClass paladin = new DatabasePaladin();
    private DatabaseWarlordsClass shaman = new DatabaseShaman();
    @Field("last_spec")
    private String lastSpec = "Pyromancer";
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

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", uuid='" + uuid + '\'' +
                ", wins=" + wins +
                ", lastSpec='" + lastSpec + '\'' +
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

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
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

    public DatabaseWarlordsClass getMage() {
        return mage;
    }

    public DatabaseWarlordsClass getWarrior() {
        return warrior;
    }
    public DatabaseWarlordsClass getPaladin() {
        return paladin;
    }

    public DatabaseWarlordsClass getShaman() {
        return shaman;
    }


    public String getLastSpec() {
        return lastSpec;
    }

    public void setLastSpec(String lastSpec) {
        this.lastSpec = lastSpec;
    }
}
