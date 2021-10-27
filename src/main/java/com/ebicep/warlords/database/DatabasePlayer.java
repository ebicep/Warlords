package com.ebicep.warlords.database;

import org.bukkit.ChatColor;

import java.util.ArrayList;

public class DatabasePlayer {
    private final String name;
    private final ChatColor teamColor;
    private final String spec;
    private final ArrayList<Integer> kills;
    private final ArrayList<Integer> assists;
    private final ArrayList<Integer> deaths;
    private final ArrayList<Long> damage;
    private final ArrayList<Long> healing;
    private final ArrayList<Long> absorbed;

    public DatabasePlayer(String name, ChatColor teamColor, String spec, ArrayList<Integer> kills, ArrayList<Integer> assists, ArrayList<Integer> deaths, ArrayList<Long> damage, ArrayList<Long> healing, ArrayList<Long> absorbed) {
        this.name = name;
        this.teamColor = teamColor;
        this.spec = spec;
        this.kills = kills;
        this.assists = assists;
        this.deaths = deaths;
        this.damage = damage;
        this.healing = healing;
        this.absorbed = absorbed;
    }

    public String getColoredName() {
        return this.teamColor + this.name;
    }

    public String getSpec() {
        return spec;
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

}

