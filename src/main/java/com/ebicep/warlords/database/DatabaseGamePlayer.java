package com.ebicep.warlords.database;

import org.bson.BsonArray;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.ArrayList;

public class DatabaseGamePlayer {

    private final String uuid;
    private final String name;
    private final String spec;
    private final int blocksTravelled;
    private final int secondsInCombat;
    private final int secondsInRespawn;
    private final String xLocations;
    private final String zLocations;
    private final ArrayList<Integer> kills;
    private final ArrayList<Integer> assists;
    private final ArrayList<Integer> deaths;
    private final ArrayList<Long> damage;
    private final ArrayList<Long> healing;
    private final ArrayList<Long> absorbed;
    private final int flagCaptures;
    private final int flagReturns;

    private final ChatColor teamColor;

    public DatabaseGamePlayer(Document document, ChatColor teamColor) {
        this.uuid = (String) document.get("uuid");
        this.name = (String) document.get("name");
        this.spec = (String) document.get("spec");
        this.blocksTravelled = (int) document.get("blocks_travelled");
        this.secondsInCombat = (int) document.get("seconds_in_combat");
        this.secondsInRespawn = (int) document.get("seconds_in_respawn");
        this.xLocations = (String) document.get("x_locations");
        this.zLocations = (String) document.get("z_locations");
        this.kills = (ArrayList<Integer>) document.get("kills");
        this.assists = (ArrayList<Integer>) document.get("assists");
        this.deaths = (ArrayList<Integer>) document.get("deaths");
        this.damage = (ArrayList<Long>) document.get("damage");
        this.healing = (ArrayList<Long>) document.get("healing");
        this.absorbed = (ArrayList<Long>) document.get("absorbed");
        this.flagCaptures = (int) document.get("flag_captures");
        this.flagReturns = (int) document.get("flag_returns");
        this.teamColor = teamColor;
    }

    public DatabaseGamePlayer(String uuid, String name, String spec, int blocksTravelled, int secondsInCombat, int secondsInRespawn, String xLocations, String zLocations, ArrayList<Integer> kills, ArrayList<Integer> assists, ArrayList<Integer> deaths, ArrayList<Long> damage, ArrayList<Long> healing, ArrayList<Long> absorbed, int flagCaptures, int flagReturns, ChatColor teamColor) {
        this.uuid = uuid;
        this.name = name;
        this.spec = spec;
        this.blocksTravelled = blocksTravelled;
        this.secondsInCombat = secondsInCombat;
        this.secondsInRespawn = secondsInRespawn;
        this.xLocations = xLocations;
        this.zLocations = zLocations;
        this.kills = kills;
        this.assists = assists;
        this.deaths = deaths;
        this.damage = damage;
        this.healing = healing;
        this.absorbed = absorbed;
        this.flagCaptures = flagCaptures;
        this.flagReturns = flagReturns;
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

    public ArrayList<Integer> getKills() {
        return kills;
    }

    public ArrayList<Integer> getAssists() {
        return assists;
    }

    public ArrayList<Integer> getDeaths() {
        return deaths;
    }

    public ArrayList<Long> getDamage() {
        return damage;
    }

    public ArrayList<Long> getHealing() {
        return healing;
    }

    public ArrayList<Long> getAbsorbed() {
        return absorbed;
    }

    public String getKDA() {
        return ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + getTotalKills() + ChatColor.GRAY + ":" + ChatColor.GOLD + getTotalAssists() + ChatColor.GRAY + ":" + ChatColor.RED + getTotalDeaths() + ChatColor.DARK_GRAY + "]";
    }

    public int getTotalKills() {
        return kills.stream().reduce(0, Integer::sum);
    }

    public int getTotalAssists() {
        return assists.stream().reduce(0, Integer::sum);
    }

    public int getTotalDeaths() {
        return deaths.stream().reduce(0, Integer::sum);
    }

    public Long getTotalDamage() {
        return damage.stream().reduce(0L, Long::sum);
    }

    public Long getTotalHealing() {
        return healing.stream().reduce(0L, Long::sum);
    }

    public Long getTotalAbsorbed() {
        return absorbed.stream().reduce(0L, Long::sum);
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