package com.ebicep.warlords.maps;

import org.bukkit.Bukkit;
import org.bukkit.Location;

    // MAPS:
    // "Crossfire"
    // "Rift"
    // "Atherrough_Valley"
    // "Warsong"
    // "Gorge"

    public enum GameMap {
        RIFT(
                "Rift",
                24,
                1,
                900 * 20, // seconds * ticks
                30 * 20, // seconds * ticks
                "",

                new Location(Bukkit.getWorld("Rift"), -32.5, 25.5, 49.5), // BLUE DAMAGE
                new Location(Bukkit.getWorld("Rift"), 33.5, 25.5, -48.5), // RED DAMAGE

                new Location(Bukkit.getWorld("Rift"), -54.5, 36.5, 24.5), // BLUE SPEED
                new Location(Bukkit.getWorld("Rift"), 55.5, 36.5, -23.5), // RED SPEED

                new Location(Bukkit.getWorld("Rift"), -0.5, 24.5, 64.5), // BLUE HEALING
                new Location(Bukkit.getWorld("Rift"), 1.5, 24.5, -62.5), // RED HEALING

                new Location(Bukkit.getWorld("Rift"), -86.5, 45.5, -33.5),// BLUE LOBBY SPAWN
                new Location(Bukkit.getWorld("Rift"), 87, 45.5, 35.5), // RED LOBBY SPAWN

                new Location(Bukkit.getWorld("Rift"), -32.5, 34.5, -43.5), // BLUE RESPAWN
                new Location(Bukkit.getWorld("Rift"), 34.5, 34.5, 42.5), // RED RESPAWN

                new Location(Bukkit.getWorld("Rift"), -98.5, 45.5, -17.5), // BLUE FLAG
                new Location(Bukkit.getWorld("Rift"), 99.5, 45.5, 17.5) // RED FLAG
        ),

        CROSSFIRE(
                "Crossfire",
                24,
                1,
                900 * 20, // seconds * ticks
                30 * 20, // seconds * ticks
                "",

                new Location(Bukkit.getWorld("Crossfire"), 158.5, 6.5, 28.5), // BLUE DAMAGE
                new Location(Bukkit.getWorld("Crossfire"), 65.5, 6.5, 97.5), // RED DAMAGE

                new Location(Bukkit.getWorld("Crossfire"), 217.5, 36.5, 89.5), // BLUE SPEED
                new Location(Bukkit.getWorld("Crossfire"), 6.5, 36.5, 39.5), // RED SPEED

                new Location(Bukkit.getWorld("Crossfire"), 96.5, 6.5, 108.5), // BLUE HEALING
                new Location(Bukkit.getWorld("Crossfire"), 126.5, 6.5, 19.5), // RED HEALING

                new Location(Bukkit.getWorld("Crossfire"), 215.5, 36.5, 109.5),// BLUE LOBBY SPAWN
                new Location(Bukkit.getWorld("Crossfire"), 7.5, 36.5, 19.5), // RED LOBBY SPAWN

                new Location(Bukkit.getWorld("Crossfire"), 133, 11.5, 130.5), // BLUE RESPAWN
                new Location(Bukkit.getWorld("Crossfire"), 90.5, 11.5, 0.5), // RED RESPAWN

                new Location(Bukkit.getWorld("Crossfire"), 217.5, 36.5, 126.5), // BLUE FLAG
                new Location(Bukkit.getWorld("Crossfire"), 5.5, 36.5, 1.5) // RED FLAG
        );

        protected String mapName;
        protected int maxPlayers;
        protected int minPlayers;
        protected int gameTimerInTicks;
        protected int countdownTimerInTicks;
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

         GameMap(String mapName, int maxPlayers, int minPlayers, int gameTime, int countdown, String mapPath, Location damagePowerupBlue, Location damagePowerupRed,
                   Location speedPowerupBlue, Location speedPowerupRed, Location healingPowerupBlue, Location healingPowerupRed, Location blueSpawnPoint, Location redSpawnPoint, Location blueRespawn, Location redRespawn, Location blueFlag, Location redFlag) {

            this.mapName = mapName;
            this.maxPlayers = maxPlayers;
            this.minPlayers = minPlayers;
            this.gameTimerInTicks = gameTime;
            this.countdownTimerInTicks = countdown;
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

        public String getMapName() { return mapName; }
        public int getMaxPlayers() { return maxPlayers; }
        public int getMinPlayers() { return minPlayers; }
        public int getGameTimerInTicks() { return gameTimerInTicks; }
        public int getCountdownTimerInTicks() { return countdownTimerInTicks; }
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

