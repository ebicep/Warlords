package com.ebicep.warlords.database.newdb.repositories.games.pojos;

import com.ebicep.warlords.player.Classes;
import org.bukkit.ChatColor;

import java.util.List;

public class DatabaseGamePlayers {

    protected List<GamePlayer> blue;
    protected List<GamePlayer> red;

    public DatabaseGamePlayers(List<GamePlayer> blue, List<GamePlayer> red) {
        this.blue = blue;
        this.red = red;
    }

    public List<GamePlayer> getBlue() {
        return blue;
    }

    public List<GamePlayer> getRed() {
        return red;
    }

    public static class GamePlayer {

        private String uuid;
        private String name;
        private String spec;
        private int blocksTravelled;
        private int secondsInCombat;
        private int secondsInRespawn;
        private String xLocations;
        private String zLocations;
        private int totalKills;
        private int totalAssists;
        private int totalDeaths;
        private long totalDamage;
        private long totalHealing;
        private long totalAbsorbed;
        private List<Integer> kills;
        private List<Integer> assists;
        private List<Integer> deaths;
        private List<Long> damage;
        private List<Long> healing;
        private List<Long> absorbed;
        private int flagCaptures;
        private int flagReturns;
        private long totalDamageOnCarrier;
        private long totalHealingOnCarrier;
        private List<Long> damageOnCarrier;
        private List<Long> healingOnCarrier;
        private long experienceEarned;

        public GamePlayer() {
        }



        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSpec() {
            return spec;
        }

        public void setSpec(String spec) {
            this.spec = spec;
        }

        public int getBlocksTravelled() {
            return blocksTravelled;
        }

        public void setBlocksTravelled(int blocksTravelled) {
            this.blocksTravelled = blocksTravelled;
        }

        public int getSecondsInCombat() {
            return secondsInCombat;
        }

        public void setSecondsInCombat(int secondsInCombat) {
            this.secondsInCombat = secondsInCombat;
        }

        public int getSecondsInRespawn() {
            return secondsInRespawn;
        }

        public void setSecondsInRespawn(int secondsInRespawn) {
            this.secondsInRespawn = secondsInRespawn;
        }

        public String getxLocations() {
            return xLocations;
        }

        public void setxLocations(String xLocations) {
            this.xLocations = xLocations;
        }

        public String getzLocations() {
            return zLocations;
        }

        public void setzLocations(String zLocations) {
            this.zLocations = zLocations;
        }

        public String getKDAString() {
            return ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + getTotalKills() + ChatColor.GRAY + ":" + ChatColor.GOLD + getTotalAssists() + ChatColor.GRAY + ":" + ChatColor.RED + getTotalDeaths() + ChatColor.DARK_GRAY + "]";
        }

        public int getTotalKills() {
            return totalKills;
        }

        public void setTotalKills(int totalKills) {
            this.totalKills = totalKills;
        }

        public int getTotalAssists() {
            return totalAssists;
        }

        public void setTotalAssists(int totalAssists) {
            this.totalAssists = totalAssists;
        }

        public int getTotalDeaths() {
            return totalDeaths;
        }

        public void setTotalDeaths(int totalDeaths) {
            this.totalDeaths = totalDeaths;
        }

        public long getTotalDHP() {
            return totalDamage + totalHealing + totalAbsorbed;
        }

        public long getTotalDamage() {
            return totalDamage;
        }

        public void setTotalDamage(long totalDamage) {
            this.totalDamage = totalDamage;
        }

        public long getTotalHealing() {
            return totalHealing;
        }

        public void setTotalHealing(long totalHealing) {
            this.totalHealing = totalHealing;
        }

        public long getTotalAbsorbed() {
            return totalAbsorbed;
        }

        public void setTotalAbsorbed(long totalAbsorbed) {
            this.totalAbsorbed = totalAbsorbed;
        }

        public List<Integer> getKills() {
            return kills;
        }

        public void setKills(List<Integer> kills) {
            this.kills = kills;
        }

        public List<Integer> getAssists() {
            return assists;
        }

        public void setAssists(List<Integer> assists) {
            this.assists = assists;
        }

        public List<Integer> getDeaths() {
            return deaths;
        }

        public void setDeaths(List<Integer> deaths) {
            this.deaths = deaths;
        }

        public List<Long> getDamage() {
            return damage;
        }

        public void setDamage(List<Long> damage) {
            this.damage = damage;
        }

        public List<Long> getHealing() {
            return healing;
        }

        public void setHealing(List<Long> healing) {
            this.healing = healing;
        }

        public List<Long> getAbsorbed() {
            return absorbed;
        }

        public void setAbsorbed(List<Long> absorbed) {
            this.absorbed = absorbed;
        }

        public int getFlagCaptures() {
            return flagCaptures;
        }

        public void setFlagCaptures(int flagCaptures) {
            this.flagCaptures = flagCaptures;
        }

        public int getFlagReturns() {
            return flagReturns;
        }

        public void setFlagReturns(int flagReturns) {
            this.flagReturns = flagReturns;
        }

        public long getTotalDamageOnCarrier() {
            return totalDamageOnCarrier;
        }

        public void setTotalDamageOnCarrier(long totalDamageOnCarrier) {
            this.totalDamageOnCarrier = totalDamageOnCarrier;
        }

        public long getTotalHealingOnCarrier() {
            return totalHealingOnCarrier;
        }

        public void setTotalHealingOnCarrier(long totalHealingOnCarrier) {
            this.totalHealingOnCarrier = totalHealingOnCarrier;
        }

        public List<Long> getDamageOnCarrier() {
            return damageOnCarrier;
        }

        public void setDamageOnCarrier(List<Long> damageOnCarrier) {
            this.damageOnCarrier = damageOnCarrier;
        }

        public List<Long> getHealingOnCarrier() {
            return healingOnCarrier;
        }

        public void setHealingOnCarrier(List<Long> healingOnCarrier) {
            this.healingOnCarrier = healingOnCarrier;
        }

        public long getExperienceEarned() {
            return experienceEarned;
        }

        public void setExperienceEarned(long experienceEarned) {
            this.experienceEarned = experienceEarned;
        }
    }
}
