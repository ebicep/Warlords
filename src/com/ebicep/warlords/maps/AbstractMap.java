package com.ebicep.warlords.maps;

import org.bukkit.Location;

import javax.sound.sampled.FloatControl;

public class AbstractMap {

    private String mapName;
    private int maxPlayers = 32;
    private int minPlayers = -1;
    private int gameTimer;
    private int countdownTimer;
    private Location damagePowerupBlue;
    private Location damagePowerupRed;
    private Location speedPowerupBlue;
    private Location speedPowerupRed;
    private String mapDirPath = "";

        // TODO: seperate startgame and wrap into the absract map to warp all players to game lobbies (ill do this)
        // TODO: scoreboard updates
        // TODO: powerup locations once those are done
        // TODO: map cleanup

    public AbstractMap(String mapName, int maxPlayers, int minPlayers, int time, int countdown, String mapPath, Location damagePowerupBlue, Location damagePowerupRed,
                       Location speedPowerupBlue, Location speedPowerupRed) {

        this.mapName = mapName;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.gameTimer = time;
        this.countdownTimer = countdown;
        this.mapDirPath = mapPath;
        this.damagePowerupBlue = damagePowerupBlue;
        this.damagePowerupRed = damagePowerupRed;
        this.speedPowerupBlue = speedPowerupBlue;
        this.speedPowerupRed = speedPowerupRed;
    }

    public void onLoad() {

        // wip
    }
}
