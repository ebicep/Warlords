package com.ebicep.warlords.maps;

import org.bukkit.Bukkit;
import org.bukkit.Location;

    // MAPS:
    // "Crossfire"
    // "Rift"
    // "Valley"
    // "Warsong"
    // "Gorge"
    // "Debug"

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

                new Location(Bukkit.getWorld("Rift"), -86.5, 45.5, -33.5), // BLUE LOBBY SPAWN
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

                new Location(Bukkit.getWorld("Crossfire"), 215.5, 36.5, 109.5), // BLUE LOBBY SPAWN
                new Location(Bukkit.getWorld("Crossfire"), 7.5, 36.5, 19.5), // RED LOBBY SPAWN

                new Location(Bukkit.getWorld("Crossfire"), 133, 11.5, 130.5), // BLUE RESPAWN
                new Location(Bukkit.getWorld("Crossfire"), 90.5, 11.5, 0.5), // RED RESPAWN

                new Location(Bukkit.getWorld("Crossfire"), 217.5, 36.5, 126.5), // BLUE FLAG
                new Location(Bukkit.getWorld("Crossfire"), 5.5, 36.5, 1.5) // RED FLAG
        ),

        WARSONG(
                "Warsong",
                24,
                1,
                900 * 20,
                30 * 20,
                "",

                new Location(Bukkit.getWorld("Warsong"), 112.5, 21.5, 51.5), // BLUE DAMAGE
                new Location(Bukkit.getWorld("Warsong"), 43.5, 16.5, 89.5), // RED DAMAGE

                new Location(Bukkit.getWorld("Warsong"), 63.5, 34.5, -46.5), // BLUE SPEED
                new Location(Bukkit.getWorld("Warsong"), 81.5, 35.5, 185.5), // RED SPEED

                new Location(Bukkit.getWorld("Warsong"), 42.5, 17.5, 60.5), // BLUE HEALING
                new Location(Bukkit.getWorld("Warsong"), 120.5, 22.5, 90.5), // RED HEALING

                new Location(Bukkit.getWorld("Warsong"), 56.5, 39.5, -94.5), // BLUE LOBBY SPAWN
                new Location(Bukkit.getWorld("Warsong"), 88.5, 40.5, 232.5), // RED LOBBY SPAWN

                new Location(Bukkit.getWorld("Warsong"), 38.5, 29.5, 14.5), // BLUE RESPAWN
                new Location(Bukkit.getWorld("Warsong"), 119.5, 29.5, 124.5), // RED RESPAWN

                new Location(Bukkit.getWorld("Warsong"), 56.5, 39.5, -102.5), // BLUE FLAG
                new Location(Bukkit.getWorld("Warsong"), 88.5, 40.5, 241.5) // RED FLAG
        ),

        GORGE(
                "Gorge",
                24,
                1,
                900 * 20,
                30 * 20,
                "",

                new Location(Bukkit.getWorld("Gorge"), 36.5, 26.5, -36.5), // BLUE DAMAGE
                new Location(Bukkit.getWorld("Gorge"), -35.5, 26.5, 37.5), // RED DAMAGE

                // NO SPEED ON GORGE (YET)
                new Location(Bukkit.getWorld("Gorge"), 999, 34.5, 999), // BLUE SPEED
                new Location(Bukkit.getWorld("Gorge"), 999, 35.5, 999), // RED SPEED
                // NO SPEED ON GORGE (YET)

                // ONLY 1 HEALING
                new Location(Bukkit.getWorld("Gorge"), 0.5, 15.5, 0.5), // BLUE HEALING
                new Location(Bukkit.getWorld("Gorge"), 999, 22.5, 999), // RED HEALING

                new Location(Bukkit.getWorld("Gorge"), 84.5, 41.5, 0.5), // BLUE LOBBY SPAWN
                new Location(Bukkit.getWorld("Gorge"), -85.5, 41.5, 0.5), // RED LOBBY SPAWN

                new Location(Bukkit.getWorld("Gorge"), 65.5, 42.5, -36.5), // BLUE RESPAWN
                new Location(Bukkit.getWorld("Gorge"), -65.5, 42.5, 35.5), // RED RESPAWN

                new Location(Bukkit.getWorld("Gorge"), 99.5, 47.5, 0.5), // BLUE FLAG
                new Location(Bukkit.getWorld("Gorge"), -99.5, 47.5, 0.5) // RED FLAG
        ),

        VALLEY(
                "Valley",
                32,
                1,
                900 * 20,
                30 * 20,
                "",

                new Location(Bukkit.getWorld("Atherrough_Valley"), 5.5, 15.5, -33.5), // BLUE DAMAGE
                new Location(Bukkit.getWorld("Atherrough_Valley"), -4.5, 15.5, 34.5), // RED DAMAGE

                new Location(Bukkit.getWorld("Atherrough_Valley"), 4.5, 25.5, -86.5), // BLUE SPEED
                new Location(Bukkit.getWorld("Atherrough_Valley"), -3.5, 25.5, 87.5), // RED SPEED

                new Location(Bukkit.getWorld("Atherrough_Valley"), 57.5, 15.5, 1.5), // BLUE HEALING
                new Location(Bukkit.getWorld("Atherrough_Valley"), -56.5, 15.5, -0.5), // RED HEALING

                new Location(Bukkit.getWorld("Atherrough_Valley"), -22.5, 38.5, -83.5), // BLUE LOBBY SPAWN
                new Location(Bukkit.getWorld("Atherrough_Valley"), 23.5, 38.5, 83.5), // RED LOBBY SPAWN

                new Location(Bukkit.getWorld("Atherrough_Valley"), 39.5, 28.5, -97.5), // BLUE RESPAWN
                new Location(Bukkit.getWorld("Atherrough_Valley"), -38.5, 28.5, 97.5), // RED RESPAWN

                new Location(Bukkit.getWorld("Atherrough_Valley"), -29.5, 38.5, -88.5), // BLUE FLAG
                new Location(Bukkit.getWorld("Atherrough_Valley"), 30.5, 38.5, 89.5) // RED FLAG
        ),

        DEBUG(
                "Debug",
                96,
                1,
                1800 * 20,
                30 * 20,
                "",

                new Location(Bukkit.getWorld("WLDebug"), 699.5, 8.5, 184.5), // BLUE DAMAGE
                new Location(Bukkit.getWorld("WLDebug"), 699.5, 8.5, 188.5), // RED DAMAGE

                new Location(Bukkit.getWorld("WLDebug"), 699.5, 8.5, 192.5), // BLUE SPEED
                new Location(Bukkit.getWorld("WLDebug"), 699.5, 8.5, 196.5), // RED SPEED

                new Location(Bukkit.getWorld("WLDebug"), 699.5, 8.5, 200.5), // BLUE HEALING
                new Location(Bukkit.getWorld("WLDebug"), 699.5, 8.5, 204.5), // RED HEALING

                new Location(Bukkit.getWorld("WLDebug"), 727.5, 8.5, 200.5), // BLUE LOBBY SPAWN
                new Location(Bukkit.getWorld("WLDebug"), 727.5, 8.5, 196.5), // RED LOBBY SPAWN

                new Location(Bukkit.getWorld("WLDebug"), 727.5, 8.5, 196.5), // BLUE RESPAWN
                new Location(Bukkit.getWorld("WLDebug"), 727.5, 8.5, 196.5), // RED RESPAWN

                new Location(Bukkit.getWorld("WLDebug"), 703.5, 8.5, 212.5), // BLUE FLAG
                new Location(Bukkit.getWorld("WLDebug"), 720.5, 8.5, 212.5) // RED FLAG
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

