package com.ebicep.warlords.maps;

import org.bukkit.Location;

public class Map {

    protected String mapName;
    protected int maxPlayers;
    protected int minPlayers;
    protected int gameTimerInSeconds;
    protected int countdownTimerInSeconds;
    protected Location damagePowerupBlue;
    protected Location damagePowerupRed;
    protected Location speedPowerupBlue;
    protected Location speedPowerupRed;
    protected Location healingPowerupBlue;
    protected Location healingPowerupRed;
    protected Location blueLobbySpawnPoint;
    protected Location redLobbySpawnPoint;
    protected Location blueRespawn;
    protected Location redRespawn;
    protected Location blueFlag;
    protected Location redFlag;
    protected String mapDirPath = "";

        // TODO: seperate startgame and wrap into the absract map to warp all players to game lobbies (ill do this)
        // TODO: scoreboard updates

    public Map(String mapName, int maxPlayers, int minPlayers, int gameTime, int countdown, String mapPath, Location damagePowerupBlue, Location damagePowerupRed,
                       Location speedPowerupBlue, Location speedPowerupRed, Location healingPowerupBlue, Location healingPowerupRed, Location blueSpawnPoint, Location redSpawnPoint, Location blueRespawn, Location redRespawn, Location blueFlag, Location redFlag) {

        this.mapName = mapName;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.gameTimerInSeconds = gameTime;
        this.countdownTimerInSeconds = countdown;
        this.mapDirPath = mapPath;
        this.damagePowerupBlue = damagePowerupBlue;
        this.damagePowerupRed = damagePowerupRed;
        this.speedPowerupBlue = speedPowerupBlue;
        this.speedPowerupRed = speedPowerupRed;
        this.healingPowerupBlue = healingPowerupBlue;
        this.healingPowerupRed = healingPowerupRed;
        this.blueLobbySpawnPoint = blueSpawnPoint;
        this.redLobbySpawnPoint = redSpawnPoint;
        this.blueRespawn = blueRespawn;
        this.redRespawn = redRespawn;
        this.blueFlag = blueFlag;
        this.redFlag = redFlag;
    }

    public Map() {

    }

    public String getMapName() { return mapName; }
    public int getMaxPlayers() { return maxPlayers; }
    public int getMinPlayers() { return minPlayers; }
    public int getGameTimerInSeconds() { return gameTimerInSeconds; }
    public int getCountdownTimerInSeconds() { return countdownTimerInSeconds; }
    public String getMapDirPath() { return mapDirPath; }

    public Location getDamagePowerupBlue() { return damagePowerupBlue; }
    public Location getDamagePowerupRed() { return damagePowerupRed; }
    public Location getSpeedPowerupBlue() { return speedPowerupBlue; }
    public Location getSpeedPowerupRed() { return speedPowerupRed; }
    public Location getHealingPowerupBlue() { return healingPowerupBlue; }
    public Location getHealingPowerupRed() { return healingPowerupRed; }
    public Location getBlueLobbySpawnPoint() { return blueLobbySpawnPoint; }
    public Location getRedLobbySpawnPoint() { return redLobbySpawnPoint; }
    public Location getBlueRespawn() { return blueRespawn; }
    public Location getRedRespawn() { return redRespawn; }
    public Location getBlueFlag() { return blueFlag; }
    public Location getRedFlag() { return redFlag; }
}
