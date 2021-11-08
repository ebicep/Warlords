package com.ebicep.warlords.database;

import org.bukkit.Location;

public class Leaderboard {

    private final String title;
    private final Location location;

    public Leaderboard(String title, Location location) {
        this.title = title;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public Location getLocation() {
        return location;
    }
}
