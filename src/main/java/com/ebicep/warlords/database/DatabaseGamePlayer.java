package com.ebicep.warlords.database;

import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.List;

public class DatabaseGamePlayer {

    private final String uuid;
    private final String name;
    private final String spec;
    private final int blocksTravelled;
    private final int secondsInCombat;
    private final int secondsInRespawn;
    private final String xLocations;
    private final String zLocations;
    private final int totalKills;
    private final int totalAssists;
    private final int totalDeaths;
    private final long totalDamage;
    private final long totalHealing;
    private final long totalAbsorbed;
    private final List<Integer> kills;
    private final List<Integer> assists;
    private final List<Integer> deaths;
    private final List<Long> damage;
    private final List<Long> healing;
    private final List<Long> absorbed;
    private final int flagCaptures;
    private final int flagReturns;

    private final ChatColor teamColor;

    public DatabaseGamePlayer(Document document, ChatColor teamColor) {
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
        this.teamColor = teamColor;
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

    public int getFlagCaptures() {
        return flagCaptures;
    }

    public int getFlagReturns() {
        return flagReturns;
    }

    public ChatColor getTeamColor() {
        return teamColor;
    }
}