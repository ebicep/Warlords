package com.ebicep.warlords.maps;

import com.ebicep.warlords.maps.option.*;
import com.ebicep.warlords.maps.option.PowerupOption.PowerupType;
import com.ebicep.warlords.maps.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.maps.option.marker.TeamMarker;
import com.ebicep.warlords.maps.state.PreLobbyState;
import com.ebicep.warlords.maps.state.State;
import static com.ebicep.warlords.util.GameRunnable.SECOND;
import com.ebicep.warlords.util.LocationFactory;
import java.util.*;
import javax.annotation.Nonnull;
import org.bukkit.Material;

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
            60 * SECOND,
            "",
            MapCategory.CAPTURE_THE_FLAG
    ) {
        @Override
        public List<Option> initMap(MapCategory category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = new ArrayList<>();
            options.add(new MarkerOption(
                    TeamMarker.create(Team.BLUE, Team.RED),
                    LobbyLocationMarker.create(loc.addXYZ(-86.5, 46, -33.5), Team.BLUE),
                    LobbyLocationMarker.create(loc.addXYZ(87.5, 46, 35.5, 180, 0), Team.RED)
            ));
            options.add(new PowerupOption(loc.addXYZ(-32.5, 25.5, 49.5), PowerupType.DAMAGE));
            options.add(new PowerupOption(loc.addXYZ(33.5, 25.5, -48.5), PowerupType.DAMAGE));

            options.add(new PowerupOption(loc.addXYZ(-54.5, 36.5, 24.5), PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(55.5, 36.5, -23.5), PowerupType.SPEED));

            options.add(new PowerupOption(loc.addXYZ(-0.5, 24.5, 64.5), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(1.5, 24.5, -62.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-32.5, 34.5, -43.5, -90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(33, 34.5, 45, 0, 0), Team.RED));

            options.add(new FlagCapturePointOption(loc.addXYZ(-98.5, 45.5, -17.5, -90, 0), Team.BLUE));
            options.add(new FlagSpawnPointOption(loc.addXYZ(-98.5, 45.5, -17.5, -90, 0), Team.BLUE));

            options.add(new FlagCapturePointOption(loc.addXYZ(99.5, 45.5, 17.5, 90, 0), Team.RED));
            options.add(new FlagSpawnPointOption(loc.addXYZ(99.5, 45.5, 17.5, 90, 0), Team.RED));

            options.add(new GateOption(loc, -79, 45, -29, -79, 49, -24));
            options.add(new GateOption(loc, -91, 45, -6, -86, 49, -6));
            options.add(new GateOption(loc, 79, 45, 25, 79, 49, 29));
            options.add(new GateOption(loc, 87, 45, 6, 91, 49, 6));

            options.add(new WinByPointsOption());
            options.add(new MercyWinOption());
            options.add(new DrawAfterTimeoutOption());
            options.add(new GameOvertimeOption());
            options.add(new ScoreOnEventOption.FlagCapture());
            options.add(new ScoreOnEventOption.OnKill());
            options.add(new RespawnWaveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            return options;
        }

    },
    CROSSFIRE(
            "Crossfire",
            32,
            12,
            60 * SECOND,
            "",
            MapCategory.CAPTURE_THE_FLAG
    ) {
        @Override
        public List<Option> initMap(MapCategory category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = new ArrayList<>();
            options.add(new MarkerOption(
                    TeamMarker.create(Team.BLUE, Team.RED),
                    LobbyLocationMarker.create(loc.addXYZ(215.5, 37, 109.5), Team.BLUE),
                    LobbyLocationMarker.create(loc.addXYZ(7.5, 37, 19.5, 180, 0), Team.RED),
                    MapSymmetry.SPIN.asMarker()
            ));
            options.add(new PowerupOption(loc.addXYZ(158.5, 6.5, 28.5), PowerupType.DAMAGE));
            options.add(new PowerupOption(loc.addXYZ(65.5, 6.5, 98.5), PowerupType.DAMAGE));

            options.add(new PowerupOption(loc.addXYZ(217.5, 36.5, 89.5), PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(6.5, 36.5, 39.5), PowerupType.SPEED));

            options.add(new PowerupOption(loc.addXYZ(96.5, 6.5, 108.5), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(127.5, 6.5, 19.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(133, 11.5, 130.5, 125, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(90.5, 11.5, 0.5, -45, 0), Team.RED));

            options.add(new FlagCapturePointOption(loc.addXYZ(217.5, 36.5, 126.5, 150, 0), Team.BLUE));
            options.add(new FlagSpawnPointOption(loc.addXYZ(217.5, 36.5, 126.5, 150, 0), Team.BLUE));

            options.add(new FlagCapturePointOption(loc.addXYZ(5.5, 36.5, 1.5, -25, 0), Team.RED));
            options.add(new FlagSpawnPointOption(loc.addXYZ(5.5, 36.5, 1.5, -25, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(203, 36, 119), loc.addXYZ(203, 42, 124)));
            options.add(new GateOption(loc.addXYZ(227, 36, 109), loc.addXYZ(227, 40, 115)));
            options.add(new GateOption(loc.addXYZ(19, 36, 4), loc.addXYZ(19, 40, 9)));
            options.add(new GateOption(loc.addXYZ(-3, 36, 14), loc.addXYZ(-3, 40, 18)));

            options.add(new WinByPointsOption());
            options.add(new MercyWinOption());
            options.add(new DrawAfterTimeoutOption());
            options.add(new GameOvertimeOption());
            options.add(new ScoreOnEventOption.FlagCapture());
            options.add(new ScoreOnEventOption.OnKill());
            options.add(new RespawnWaveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            return options;
        }

    },
    WARSONG(
            "Warsong Remastered",
            32,
            12,
            60 * SECOND,
            "",
            MapCategory.CAPTURE_THE_FLAG
    ) {
        @Override
        public List<Option> initMap(MapCategory category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = new ArrayList<>();
            options.add(new MarkerOption(
                    TeamMarker.create(Team.BLUE, Team.RED),
                    LobbyLocationMarker.create(loc.addXYZ(71.5, 40, -71.5, 90, 0), Team.BLUE),
                    LobbyLocationMarker.create(loc.addXYZ(73.5, 41, 213.5, -90, 0), Team.RED),
                    MapSymmetry.SPIN.asMarker()
            ));
            options.add(new PowerupOption(loc.addXYZ(102.5, 21.5, 51.5), PowerupType.DAMAGE));
            options.add(new PowerupOption(loc.addXYZ(42.5, 21.5, 92.5), PowerupType.DAMAGE));

            options.add(new PowerupOption(loc.addXYZ(63.5, 33.5, -31.5), PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(79.5, 32.5, 167.5), PowerupType.SPEED));

            options.add(new PowerupOption(loc.addXYZ(44.5, 20.5, 42.5), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(100.5, 20.5, 101.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(45.5, 29.5, 15.5, -30, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(99.5, 29.5, 128.5, 145, 0), Team.RED));

            options.add(new FlagCapturePointOption(loc.addXYZ(56.5, 39.5, -76.5), Team.BLUE));
            options.add(new FlagSpawnPointOption(loc.addXYZ(56.5, 39.5, -76.5), Team.BLUE));

            options.add(new FlagCapturePointOption(loc.addXYZ(88.5, 39.5, 218.5, 180, 0), Team.RED));
            options.add(new FlagSpawnPointOption(loc.addXYZ(88.5, 39.5, 218.5, 180, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(42, 39, -52), loc.addXYZ(47, 45, -52)));
            options.add(new GateOption(loc.addXYZ(69, 35, -52), loc.addXYZ(75, 43, -52)));
            options.add(new GateOption(loc.addXYZ(70, 36, 195), loc.addXYZ(75, 43, 195)));
            options.add(new GateOption(loc.addXYZ(97, 39, 195), loc.addXYZ(104, 45, 195)));

            options.add(new WinByPointsOption());
            options.add(new MercyWinOption());
            options.add(new DrawAfterTimeoutOption());
            options.add(new GameOvertimeOption());
            options.add(new ScoreOnEventOption.FlagCapture());
            options.add(new ScoreOnEventOption.OnKill());
            options.add(new RespawnWaveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            return options;
        }

    },
    GORGE(
            "Gorge Reforged",
            32,
            12,
            60 * SECOND,
            "",
            MapCategory.DEBUG
    ) {
        @Override
        public List<Option> initMap(MapCategory category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = new ArrayList<>();
            options.add(new MarkerOption(
                    TeamMarker.create(Team.BLUE, Team.RED),
                    LobbyLocationMarker.create(loc.addXYZ(43.5, 77, -216.5).yaw(180), Team.BLUE),
                    LobbyLocationMarker.create(loc.addXYZ(-134.5, 77, -216.5), Team.RED),
                    MapSymmetry.SPIN.asMarker()
            ));
            options.add(new PowerupOption(loc.addXYZ(-2.5, 61.5, -236.5), PowerupType.DAMAGE));
            options.add(new PowerupOption(loc.addXYZ(-88.5, 61.5, -196.5), PowerupType.DAMAGE));

            options.add(new PowerupOption(loc.addXYZ(60.5, 75.5, -224.5), PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(-151.5, 75.5, -208.5), PowerupType.SPEED));

            options.add(new PowerupOption(loc.addXYZ(-12.5, 45.5, -194.5), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(-78.5, 45.5, -238.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(5.5, 71.5, -159.5), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-96.5, 71.5, -273.5), Team.RED));

            options.add(new FlagCapturePointOption(loc.addXYZ(56.5, 82.5, -216.5), Team.BLUE));
            options.add(new FlagSpawnPointOption(loc.addXYZ(56.5, 82.5, -216.5), Team.BLUE));

            options.add(new FlagCapturePointOption(loc.addXYZ(-148.5, 82.5, -216.5), Team.RED));
            options.add(new FlagSpawnPointOption(loc.addXYZ(-148.5, 82.5, -216.5), Team.RED));

            options.add(new GateOption(loc.addXYZ(34, 76, -220), loc.addXYZ(34, 80, -213), Material.IRON_FENCE));
            options.add(new GateOption(loc.addXYZ(41, 76, -201), loc.addXYZ(41, 80, -198), Material.IRON_FENCE));
            options.add(new GateOption(loc.addXYZ(52, 76, -221), loc.addXYZ(55, 78, -221), Material.IRON_FENCE));
            options.add(new GateOption(loc.addXYZ(-125, 76, -220), loc.addXYZ(-125, 80, -213), Material.IRON_FENCE));
            options.add(new GateOption(loc.addXYZ(-132, 76, -235), loc.addXYZ(-132, 80, -232), Material.IRON_FENCE));
            options.add(new GateOption(loc.addXYZ(-146, 76, -213), loc.addXYZ(-143, 78, -213), Material.IRON_FENCE));

            options.add(new WinByPointsOption());
            options.add(new MercyWinOption());
            options.add(new DrawAfterTimeoutOption());
            options.add(new GameOvertimeOption());
            options.add(new ScoreOnEventOption.FlagCapture());
            options.add(new ScoreOnEventOption.OnKill());
            options.add(new RespawnWaveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            return options;
        }

    },
    VALLEY(
            "Valley",
            32,
            12,
            60 * SECOND,
            "",
            MapCategory.CAPTURE_THE_FLAG
    ) {
        @Override
        public List<Option> initMap(MapCategory category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = new ArrayList<>();
            options.add(new MarkerOption(
                    TeamMarker.create(Team.BLUE, Team.RED),
                    LobbyLocationMarker.create(loc.addXYZ(-22.5, 39, -83.5).yaw(180), Team.BLUE),
                    LobbyLocationMarker.create(loc.addXYZ(23.5, 39, 83.5), Team.RED)
            ));
            options.add(new PowerupOption(loc.addXYZ(5.5, 15.5, -33.5), PowerupType.DAMAGE));
            options.add(new PowerupOption(loc.addXYZ(-4.5, 15.5, 34.5), PowerupType.DAMAGE));

            options.add(new PowerupOption(loc.addXYZ(4.5, 25.5, -86.5), PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(-3.5, 25.5, 87.5), PowerupType.SPEED));

            options.add(new PowerupOption(loc.addXYZ(57.5, 15.5, 1.5), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(-56.5, 15.5, -0.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(39.5, 28.5, -97.5), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-38.5, 28.5, 97.5), Team.RED));

            options.add(new FlagCapturePointOption(loc.addXYZ(-29.5, 38.5, -88.5, -90, 0), Team.BLUE));
            options.add(new FlagSpawnPointOption(loc.addXYZ(-29.5, 38.5, -88.5, -90, 0), Team.BLUE));

            options.add(new FlagCapturePointOption(loc.addXYZ(30.5, 38.5, 89.5, 90, 0), Team.RED));
            options.add(new FlagSpawnPointOption(loc.addXYZ(30.5, 38.5, 89.5, 90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(-26, 33, -96), loc.addXYZ(-19, 40, -96)));
            options.add(new GateOption(loc.addXYZ(-28, 31, -81), loc.addXYZ(-28, 41, -75)));
            options.add(new GateOption(loc.addXYZ(20, 33, 96), loc.addXYZ(26, 42, 96)));
            options.add(new GateOption(loc.addXYZ(29, 31, 76), loc.addXYZ(29, 41, 82)));

            options.add(new WinByPointsOption());
            options.add(new MercyWinOption());
            options.add(new DrawAfterTimeoutOption());
            options.add(new GameOvertimeOption());
            options.add(new ScoreOnEventOption.FlagCapture());
            options.add(new ScoreOnEventOption.OnKill());
            options.add(new RespawnWaveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            return options;
        }

    },
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
            options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 184.5), PowerupType.DAMAGE));
            options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 188.5), PowerupType.DAMAGE));

            options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 192.5), PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 196.5), PowerupType.SPEED));

            options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 200.5), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 204.5), PowerupType.HEALING));

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
            options.add(new ScoreOnEventOption.FlagCapture());
            options.add(new ScoreOnEventOption.OnKill());
            options.add(new RespawnWaveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            return options;
        }

    };
    private final String mapName;
    private final int maxPlayers;
    private final int minPlayers;
    private final int lobbyCountdown;
    private final String mapDirPath;
    private final List<MapCategory> mapCategory;

    GameMap(@Nonnull String mapName, int maxPlayers, int minPlayers, int lobbyCountdown, @Nonnull String mapDirPath, @Nonnull MapCategory... mapCategory) {
        this.mapName = mapName;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.lobbyCountdown = lobbyCountdown;
        this.mapDirPath = mapDirPath;
        this.mapCategory = Collections.unmodifiableList(Arrays.asList(mapCategory));
    }

    /**
     * Constructs the game instance
     *
     * @param category The map category to construct (for maps with multiple
     * configurations)
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
