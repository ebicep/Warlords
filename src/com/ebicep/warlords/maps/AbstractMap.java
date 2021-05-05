package com.ebicep.warlords.maps;

import org.bukkit.Location;

public class AbstractMap {

    private String mapName;
    private int maxPlayers = 32;
    private int minPlayers = -1;
    private int gameTimer;
    private int countdownTimer;
    private String mapDirPath = "";

    public AbstractMap(String mapName, int maxPlayers, int minPlayers, int time, int countdown, String mapPath) {
        this.mapName = mapName;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.gameTimer = time;
        this.countdownTimer = countdown;
        this.mapDirPath = mapPath;
    }

    public void onLoad() {

        // wip
    }
}
