package com.ebicep.warlords.maps;

import com.ebicep.warlords.maps.option.*;
import com.ebicep.warlords.maps.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.maps.option.marker.TeamMarker;
import com.ebicep.warlords.maps.state.PreLobbyState;
import com.ebicep.warlords.maps.state.State;
import static com.ebicep.warlords.util.GameRunnable.SECOND;
import com.ebicep.warlords.util.LocationFactory;
import java.util.*;
import javax.annotation.Nonnull;
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
            32,
            12,
            30 * 20, // seconds * ticks
            "",
            MapCategory.CAPTURE_THE_FLAG,

            new Location(Bukkit.getWorld("Rift"), -32.5, 25.5, 49.5), // BLUE DAMAGE
            new Location(Bukkit.getWorld("Rift"), 33.5, 25.5, -48.5), // RED DAMAGE

            new Location(Bukkit.getWorld("Rift"), -54.5, 36.5, 24.5), // BLUE SPEED
            new Location(Bukkit.getWorld("Rift"), 55.5, 36.5, -23.5), // RED SPEED

            new Location(Bukkit.getWorld("Rift"), -0.5, 24.5, 64.5), // BLUE HEALING
            new Location(Bukkit.getWorld("Rift"), 1.5, 24.5, -62.5), // RED HEALING

            new Location(Bukkit.getWorld("Rift"), -86.5, 46, -33.5), // BLUE LOBBY SPAWN
            new Location(Bukkit.getWorld("Rift"), 87, 46, 35.5, 180, 0), // RED LOBBY SPAWN

            new LocationBuilder(new Location(Bukkit.getWorld("Rift"), -32.5, 34.5, -43.5)).yaw(-90).get(), // BLUE RESPAWN
            new LocationBuilder(new Location(Bukkit.getWorld("Rift"), 33, 34.5, 45)).yaw(90).get(), // RED RESPAWN

            new Location(Bukkit.getWorld("Rift"), -98.5, 45.5, -17.5, -90, 0), // BLUE FLAG
            new Location(Bukkit.getWorld("Rift"), 99.5, 45.5, 17.5, 90, 0), // RED FLAG

            Arrays.asList(
                    new Cuboid(Bukkit.getWorld("Rift"), -79, 45, -29, -79, 49, -24), // BLUE GATES 1
                    new Cuboid(Bukkit.getWorld("Rift"), -91, 45, -6, -86, 49, -6), // BLUE GATES 2

                    new Cuboid(Bukkit.getWorld("Rift"), 79, 45, 25, 79, 49, 29), // RED GATES 1
                    new Cuboid(Bukkit.getWorld("Rift"), 87, 45, 6, 91, 49, 6) // RED GATES 2
            )
    ),

    CROSSFIRE(
            "Crossfire",
            32,
            12,
            900 * 20, // seconds * ticks
            30 * 20, // seconds * ticks
            "",
            MapCategory.CAPTURE_THE_FLAG,

            new Location(Bukkit.getWorld("Crossfire"), 158.5, 6.5, 28.5), // BLUE DAMAGE
            new Location(Bukkit.getWorld("Crossfire"), 65.5, 6.5, 98.5), // RED DAMAGE

            new Location(Bukkit.getWorld("Crossfire"), 217.5, 36.5, 89.5), // BLUE SPEED
            new Location(Bukkit.getWorld("Crossfire"), 6.5, 36.5, 39.5), // RED SPEED

            new Location(Bukkit.getWorld("Crossfire"), 96.5, 6.5, 108.5), // BLUE HEALING
            new Location(Bukkit.getWorld("Crossfire"), 127.5, 6.5, 19.5), // RED HEALING

            new Location(Bukkit.getWorld("Crossfire"), 215.5, 37, 109.5), // BLUE LOBBY SPAWN
            new Location(Bukkit.getWorld("Crossfire"), 7.5, 37, 19.5, 180, 0), // RED LOBBY SPAWN

            new Location(Bukkit.getWorld("Crossfire"), 133, 11.5, 130.5, 125, 0), // BLUE RESPAWN
            new Location(Bukkit.getWorld("Crossfire"), 90.5, 11.5, 0.5, -45, 0), // RED RESPAWN

            new Location(Bukkit.getWorld("Crossfire"), 217.5, 36.5, 126.5, 150, 0), // BLUE FLAG
            new Location(Bukkit.getWorld("Crossfire"), 5.5, 36.5, 1.5, -25, 0), // RED FLAG

            Arrays.asList(
                    new Cuboid(Bukkit.getWorld("Crossfire"), 203, 36, 119, 203, 42, 124), // BLUE GATES 1
                    new Cuboid(Bukkit.getWorld("Crossfire"), 227, 36, 109, 227, 40, 115), // BLUE GATES 2

                    new Cuboid(Bukkit.getWorld("Crossfire"), 19, 36, 4, 19, 40, 9), // RED GATES 1
                    new Cuboid(Bukkit.getWorld("Crossfire"), -3, 36, 14, -3, 40, 18) // RED GATES 2
            )
    ),

    WARSONG(
            "Warsong Remastered",
            32,
            12,
            900 * 20,
            30 * 20,
            "",
            MapCategory.CAPTURE_THE_FLAG,

            new Location(Bukkit.getWorld("Warsong"), 102.5, 21.5, 51.5), // BLUE DAMAGE
            new Location(Bukkit.getWorld("Warsong"), 42.5, 21.5, 92.5), // RED DAMAGE

            new Location(Bukkit.getWorld("Warsong"), 63.5, 33.5, -31.5), // BLUE SPEED
            new Location(Bukkit.getWorld("Warsong"), 79.5, 32.5, 167.5), // RED SPEED

            new Location(Bukkit.getWorld("Warsong"), 44.5, 20.5, 42.5), // BLUE HEALING
            new Location(Bukkit.getWorld("Warsong"), 100.5, 20.5, 101.5), // RED HEALING

            new Location(Bukkit.getWorld("Warsong"), 71.5, 40, -71.5, 90, 0), // BLUE LOBBY SPAWN
            new Location(Bukkit.getWorld("Warsong"), 73.5, 41, 213.5, -90, 0), // RED LOBBY SPAWN

            new Location(Bukkit.getWorld("Warsong"), 45.5, 29.5, 15.5, -30, 0), // BLUE RESPAWN
            new Location(Bukkit.getWorld("Warsong"), 99.5, 29.5, 128.5, 145, 0), // RED RESPAWN

            new Location(Bukkit.getWorld("Warsong"), 56.5, 39.5, -76.5), // BLUE FLAG
            new Location(Bukkit.getWorld("Warsong"), 88.5, 39.5, 218.5, 180, 0), // RED FLAG

            Arrays.asList(
                    new Cuboid(Bukkit.getWorld("Warsong"), 42, 39, -52, 47, 45, -52), // BLUE GATES 1
                    new Cuboid(Bukkit.getWorld("Warsong"), 69, 35, -52, 75, 43, -52), // BLUE GATES 2

                    new Cuboid(Bukkit.getWorld("Warsong"), 70, 36, 195, 75, 43, 195), // RED GATES 1
                    new Cuboid(Bukkit.getWorld("Warsong"), 97, 39, 195, 104, 45, 195) // RED GATES 2
            )
    ),

    GORGE(
            "Gorge Reforged",
            32,
            12,
            900 * 20,
            30 * 20,
            "",
            MapCategory.DEBUG,

            new Location(Bukkit.getWorld("Gorge"), -2.5, 61.5, -236.5), // BLUE DAMAGE
            new Location(Bukkit.getWorld("Gorge"), -88.5, 61.5, -196.5), // RED DAMAGE

            new Location(Bukkit.getWorld("Gorge"), 60.5, 75.5, -224.5), // BLUE SPEED
            new Location(Bukkit.getWorld("Gorge"), -151.5, 75.5, -208.5), // RED SPEED

            new Location(Bukkit.getWorld("Gorge"), -12.5, 45.5, -194.5), // BLUE HEALING
            new Location(Bukkit.getWorld("Gorge"), -78.5, 45.5, -238.5), // RED HEALING

            new Location(Bukkit.getWorld("Gorge"), 43.5, 77, -216.5), // BLUE LOBBY SPAWN
            new Location(Bukkit.getWorld("Gorge"), -134.5, 77, -216.5), // RED LOBBY SPAWN

            new Location(Bukkit.getWorld("Gorge"), 5.5, 71.5, -159.5), // BLUE RESPAWN
            new Location(Bukkit.getWorld("Gorge"), -96.5, 71.5, -273.5), // RED RESPAWN

            new Location(Bukkit.getWorld("Gorge"), 56.5, 82.5, -216.5), // BLUE FLAG
            new Location(Bukkit.getWorld("Gorge"), -148.5, 82.5, -216.5), // RED FLAG

            Arrays.asList(
                    new Cuboid(Bukkit.getWorld("Gorge"), 34, 76, -220, 34, 80, -213), // BLUE GATE
                    new Cuboid(Bukkit.getWorld("Gorge"), 41, 76, -201, 41, 80, -198), // BLUE GATE 2
                    new Cuboid(Bukkit.getWorld("Gorge"), 52, 76, -221, 55, 78, -221), // BLUE GATE 3

                    new Cuboid(Bukkit.getWorld("Gorge"), -125, 76, -220, -125, 80, -213), // RED GATE
                    new Cuboid(Bukkit.getWorld("Gorge"), -132, 76, -235, -132, 80, -232), // RED GATE 2
                    new Cuboid(Bukkit.getWorld("Gorge"), -146, 76, -213, -143, 78, -213) // RED GATE 3
            )
    ),

    VALLEY(
            "Valley",
            32,
            12,
            900 * 20,
            30 * 20,
            "",
            MapCategory.CAPTURE_THE_FLAG,

            new Location(Bukkit.getWorld("Atherrough_Valley"), 5.5, 15.5, -33.5), // BLUE DAMAGE
            new Location(Bukkit.getWorld("Atherrough_Valley"), -4.5, 15.5, 34.5), // RED DAMAGE

            new Location(Bukkit.getWorld("Atherrough_Valley"), 4.5, 25.5, -86.5), // BLUE SPEED
            new Location(Bukkit.getWorld("Atherrough_Valley"), -3.5, 25.5, 87.5), // RED SPEED

            new Location(Bukkit.getWorld("Atherrough_Valley"), 57.5, 15.5, 1.5), // BLUE HEALING
            new Location(Bukkit.getWorld("Atherrough_Valley"), -56.5, 15.5, -0.5), // RED HEALING

            new LocationBuilder(new Location(Bukkit.getWorld("Atherrough_Valley"), -22.5, 39, -83.5)).yaw(180).get(), // BLUE LOBBY SPAWN
            new Location(Bukkit.getWorld("Atherrough_Valley"), 23.5, 39, 83.5), // RED LOBBY SPAWN

            new Location(Bukkit.getWorld("Atherrough_Valley"), 39.5, 28.5, -97.5), // BLUE RESPAWN
            new LocationBuilder(new Location(Bukkit.getWorld("Atherrough_Valley"), -38.5, 28.5, 97.5)).yaw(180).get(), // RED RESPAWN

            new Location(Bukkit.getWorld("Atherrough_Valley"), -29.5, 38.5, -88.5, -90, 0), // BLUE FLAG
            new Location(Bukkit.getWorld("Atherrough_Valley"), 30.5, 38.5, 89.5, 90, 0), // RED FLAG

            Arrays.asList(
                    new Cuboid(Bukkit.getWorld("Atherrough_Valley"), -26, 33, -96, -19, 40, -96), // BLUE GATES 1
                    new Cuboid(Bukkit.getWorld("Atherrough_Valley"), -28, 31, -81, -28, 41, -75), // BLUE GATES 2

                    new Cuboid(Bukkit.getWorld("Atherrough_Valley"), 20, 33, 96, 26, 42, 96), // RED GATES 1
                    new Cuboid(Bukkit.getWorld("Atherrough_Valley"), 29, 31, 76, 29, 41, 82) // RED GATES 2
            )
    ),

    DEBUG(
            "Debug",
            96,
            1,
            60 * SECOND,
            "WLDebug",
            MapCategory.DEBUG
    ) {
        @Override
        public List<Option> initMap(MapCategory category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = new ArrayList<>();
            options.add(new MarkerOption(
                    TeamMarker.create(Team.BLUE, Team.RED),
                    LobbyLocationMarker.create(loc.addXYZ(727.5, 8.5, 200.5), Team.BLUE),
                    LobbyLocationMarker.create(loc.addXYZ(727.5, 8.5, 196.5), Team.RED)
            ));
            options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 184.5), PowerupOption.PowerupType.DAMAGE));
            options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 188.5), PowerupOption.PowerupType.DAMAGE));
            
            options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 192.5), PowerupOption.PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 196.5), PowerupOption.PowerupType.SPEED));
            
            options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 200.5), PowerupOption.PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 204.5), PowerupOption.PowerupType.HEALING));
            
            options.add(SpawnpointOption.forTeam(loc.addXYZ(727.5, 8.5, 196.5), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(727.5, 8.5, 196.5), Team.RED));
            
            options.add(new FlagCapturePointOption(loc.addXYZ(703.5, 8.5, 212.5), Team.BLUE));
            options.add(new FlagSpawnPointOption(loc.addXYZ(703.5, 8.5, 212.5), Team.BLUE));
            
            options.add(new FlagCapturePointOption(loc.addXYZ(720.5, 8.5, 212.5), Team.RED));
            options.add(new FlagSpawnPointOption(loc.addXYZ(720.5, 8.5, 212.5), Team.RED));
            
            options.add(new GateOption(loc.addXYZ(713, 7, 195), loc.addXYZ(713, 10, 198)));
            
            options.add(new WinByPointsOption());
            options.add(new MercyWinOption());
            options.add(new DrawAfterTimeoutOption());
            options.add(new GameOvertimeOption());
            
            return options;
        }
        
    }
    ;

//            new Location(Bukkit.getWorld("WLDebug"), 699.5, 8.5, 184.5), // BLUE DAMAGE
//            new Location(Bukkit.getWorld("WLDebug"), 699.5, 8.5, 188.5), // RED DAMAGE
//
//            new Location(Bukkit.getWorld("WLDebug"), 699.5, 8.5, 192.5), // BLUE SPEED
//            new Location(Bukkit.getWorld("WLDebug"), 699.5, 8.5, 196.5), // RED SPEED
//
//            new Location(Bukkit.getWorld("WLDebug"), 699.5, 8.5, 200.5), // BLUE HEALING
//            new Location(Bukkit.getWorld("WLDebug"), 699.5, 8.5, 204.5), // RED HEALING
//
//            new Location(Bukkit.getWorld("WLDebug"), 727.5, 8.5, 200.5), // BLUE LOBBY SPAWN
//            new Location(Bukkit.getWorld("WLDebug"), 727.5, 8.5, 196.5), // RED LOBBY SPAWN
//
//            new Location(Bukkit.getWorld("WLDebug"), 727.5, 8.5, 196.5), // BLUE RESPAWN
//            new Location(Bukkit.getWorld("WLDebug"), 727.5, 8.5, 196.5), // RED RESPAWN
//
//            new Location(Bukkit.getWorld("WLDebug"), 703.5, 8.5, 212.5), // BLUE FLAG
//            new Location(Bukkit.getWorld("WLDebug"), 720.5, 8.5, 212.5), // RED FLAG
//
//            Arrays.asList(
//                    new Cuboid(Bukkit.getWorld("WLDebug"), 713, 7, 195, 713, 10, 198) // BLUE GATE
//            )
//    );

    private final String mapName;
    private final int maxPlayers;
    private final int minPlayers;
    private final int lobbyCountdown;
    private final String mapDirPath;
    private final List<MapCategory> mapCategory;

    GameMap(@Nonnull String mapName, int maxPlayers, int minPlayers, int lobbyCountdown, @Nonnull String mapDirPath, @Nonnull MapCategory ... mapCategory) {
        this.mapName = mapName;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.lobbyCountdown = lobbyCountdown;
        this.mapDirPath = mapDirPath;
        this.mapCategory = Collections.unmodifiableList(Arrays.asList(mapCategory));
    }
    
    /**
     * Constructs the game instance
     * @param category The map category to construct (for maps with multiple configurations)
     * @param loc The base location to construct the map
     * @param addons The used addons
     * @return The initial list of options
     */
    public abstract List<Option> initMap(MapCategory category, LocationFactory loc, EnumSet<GameAddon> addons);
    
    public State initialState(Game game) {
        return new PreLobbyState(game);
    }

    public String getMapName() {
        return mapName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getLobbyCountdown() {
        return lobbyCountdown;
    }

    public String getMapDirPath() {
        return mapDirPath;
    }

    public List<MapCategory> getCategories() {
        return mapCategory;
    }
}
