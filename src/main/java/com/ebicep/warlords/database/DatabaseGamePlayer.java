package com.ebicep.warlords.database;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class DatabaseGamePlayer {

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

    private ChatColor teamColor;

    public DatabaseGamePlayer(Document document, ChatColor teamColor) {
        try {
            this.uuid = document.getString("uuid");
            this.name = document.getString("name");
            this.spec = document.getString("spec");
            this.blocksTravelled = document.getInteger("blocks_travelled");
            this.secondsInCombat = document.getInteger("seconds_in_combat");
            this.secondsInRespawn = document.getInteger("seconds_in_respawn");
            this.xLocations = document.getString("x_locations");
            this.zLocations = document.getString("z_locations");
            this.totalKills = document.getInteger("total_kills");
            this.totalAssists = document.getInteger("total_assists");
            this.totalDeaths = document.getInteger("total_deaths");
            this.totalDamage = document.getLong("total_damage");
            this.totalHealing = document.getLong("total_healing");
            this.totalAbsorbed = document.getLong("total_absorbed");
            this.kills = document.getList("kills", Integer.class);
            this.assists = document.getList("assists", Integer.class);
            this.deaths = document.getList("deaths", Integer.class);
            this.damage = document.getList("damage", Long.class);
            this.healing = document.getList("healing", Long.class);
            this.absorbed = document.getList("absorbed", Long.class);
            this.flagCaptures = document.getInteger("flag_captures");
            this.flagReturns = document.getInteger("flag_returns");

            this.totalDamageOnCarrier = (long) document.getOrDefault("total_damage_on_carrier", 0L);
            this.totalHealingOnCarrier = (long) document.getOrDefault("total_healing_on_carrier", 0L);
            this.damageOnCarrier = (List<Long>) document.getOrDefault("damage_on_carrier", new ArrayList<Long>());
            this.healingOnCarrier = (List<Long>) document.getOrDefault("healing_on_carrier", new ArrayList<Long>());

            this.experienceEarned = (long) document.getOrDefault("experience_earned", 0L);

            this.teamColor = teamColor;
        } catch (Exception e) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] DatabaseGamePlayer constructor ERROR");
            e.printStackTrace();
        }
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getColoredName() {
        return this.teamColor + this.name;
    }

    public String getSpec() {
        return spec;
    }

    public int getBlocksTravelled() {
        return blocksTravelled;
    }

    public int getSecondsInCombat() {
        return secondsInCombat;
    }

    public int getSecondsInRespawn() {
        return secondsInRespawn;
    }

    public String getxLocations() {
        return xLocations;
    }

    public String getzLocations() {
        return zLocations;
    }

    public List<Integer> getKills() {
        return kills;
    }

    public List<Integer> getAssists() {
        return assists;
    }

    public List<Integer> getDeaths() {
        return deaths;
    }

    public List<Long> getDamage() {
        return damage;
    }

    public List<Long> getHealing() {
        return healing;
    }

    public List<Long> getAbsorbed() {
        return absorbed;
    }

    public String getKDA() {
        return ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + getTotalKills() + ChatColor.GRAY + ":" + ChatColor.GOLD + getTotalAssists() + ChatColor.GRAY + ":" + ChatColor.RED + getTotalDeaths() + ChatColor.DARK_GRAY + "]";
    }

    public int getTotalKills() {
        return totalKills;
    }

    public int getTotalAssists() {
        return totalAssists;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public long getTotalDamage() {
        return totalDamage;
    }

    public long getTotalHealing() {
        return totalHealing;
    }

    public long getTotalAbsorbed() {
        return totalAbsorbed;
    }

    public Long getTotalDHP() {
        return getTotalDamage() + getTotalHealing() + getTotalAbsorbed();
    }

    public int getFlagCaptures() {
        return flagCaptures;
    }

    public int getFlagReturns() {
        return flagReturns;
    }

    public long getTotalDamageOnCarrier() {
        return totalDamageOnCarrier;
    }

    public long getTotalHealingOnCarrier() {
        return totalHealingOnCarrier;
    }

    public List<Long> getDamageOnCarrier() {
        return damageOnCarrier;
    }

    public List<Long> getHealingOnCarrier() {
        return healingOnCarrier;
    }

    public long getExperienceEarned() {
        return experienceEarned;
    }

    public ChatColor getTeamColor() {
        return teamColor;
    }
}