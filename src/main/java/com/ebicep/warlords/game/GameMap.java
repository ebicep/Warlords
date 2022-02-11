package com.ebicep.warlords.game;

import com.ebicep.warlords.game.option.*;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.PowerupOption.PowerupType;
import com.ebicep.warlords.game.state.PreLobbyState;
import com.ebicep.warlords.game.state.State;
import com.ebicep.warlords.util.LocationFactory;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.util.GameRunnable.SECOND;

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
            MapCategory.CAPTURE_THE_FLAG,
            MapCategory.INTERCEPTION
    ) {
        @Override
        public List<Option> initMap(MapCategory category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);
            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-86.5, 46, -33.5), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(87.5, 46, 35.5, 180, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(-32.5, 25.5, 49.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(33.5, 25.5, -48.5), PowerupType.ENERGY));

            options.add(new PowerupOption(loc.addXYZ(-54.5, 36.5, 24.5), PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(55.5, 36.5, -23.5), PowerupType.SPEED));

            options.add(new PowerupOption(loc.addXYZ(-0.5, 24.5, 64.5), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(1.5, 24.5, -62.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-32.5, 34.5, -43.5, -90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(33, 34.5, 45, 0, 0), Team.RED));

            switch(category) {
                case CAPTURE_THE_FLAG:
                    options.add(new FlagCapturePointOption(loc.addXYZ(-98.5, 45.5, -17.5, -90, 0), Team.BLUE));
                    options.add(new FlagSpawnPointOption(loc.addXYZ(-98.5, 45.5, -17.5, -90, 0), Team.BLUE));

                    options.add(new FlagCapturePointOption(loc.addXYZ(99.5, 45.5, 17.5, 90, 0), Team.RED));
                    options.add(new FlagSpawnPointOption(loc.addXYZ(99.5, 45.5, 17.5, 90, 0), Team.RED));

                    options.add(new ScoreOnEventOption.FlagCapture(250));
                    break;
                case INTERCEPTION:
                    options.add(new InterceptionPointOption("Middle #1", loc.addXYZ(13.5, 23, -26.5)));
                    options.add(new InterceptionPointOption("Middle #2", loc.addXYZ(-13.5, 23, 26.5)));
                    options.add(new InterceptionPointOption("Stairs #1", loc.addXYZ(-44.5, 34, 22.5)));
                    options.add(new InterceptionPointOption("Stairs #2", loc.addXYZ(44.5, 34, -20.5)));

                    options.add(new ScoreOnEventOption.OnInterceptionCapture(25));
                    options.add(new ScoreOnEventOption.OnInterceptionTimer(1));
                    break;
                default:
                    throw new IllegalStateException("Not prepared to handle category " + category);
            }

            options.add(new GateOption(loc, -79, 45, -29, -79, 49, -24));
            options.add(new GateOption(loc, -91, 45, -6, -86, 49, -6));
            options.add(new GateOption(loc, 79, 45, 25, 79, 49, 29));
            options.add(new GateOption(loc, 87, 45, 6, 91, 49, 6));

            options.add(new WinByPointsOption());
            options.add(new MercyWinOption());
            options.add(new WinAfterTimeoutOption());
            options.add(new GameOvertimeOption());
            options.add(new ScoreOnEventOption.OnKill(5));
            options.add(new RespawnWaveOption());
            options.add(new RespawnProtectionOption());
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            return options;
        }

    },
    SIMULATION_RIFT(
            "Simulated Rift",
            12,
            9,
            60 * SECOND,
            "",
            MapCategory.SIMULATION_TRIAL
    ) {
        @Override
        public List<Option> initMap(MapCategory category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);
            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-14.5, 22.5, -0.5, -90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(44.5, 41.5, 62.5, 160, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(33.5, 25.5, -48.5), PowerupType.ENERGY));

            options.add(new PowerupOption(loc.addXYZ(55.5, 36.5, -23.5), PowerupType.SPEED));

            options.add(new PowerupOption(loc.addXYZ(1.5, 24.5, -62.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-32.5, 34.5, -43.5, -90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(33, 34.5, 45, 0, 0), Team.RED));

            options.add(new FlagSpawnPointOption(loc.addXYZ(61.5, 46.5, 56.5, 140, 0), Team.BLUE));

            options.add(new GateOption(loc, -9, 19, 5, -19, 26, -5));
            options.add(new GateOption(loc, 39, 39, 57, 49, 49, 67));

            options.add(new WinByPointsOption(300));
            options.add(new MercyWinOption());
            options.add(new WinAfterTimeoutOption());
            options.add(new ScoreOnEventOption.FlagReturn());
            options.add(new ScoreOnEventOption.FlagHolding());
            options.add(new SimulationTeleportOption());
            options.add(new RespawnWaveOption());
            options.add(new RespawnProtectionOption());
            options.add(new GraveOption());

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
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(215.5, 37, 109.5), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(7.5, 37, 19.5, 180, 0), Team.RED).asOption());
            options.add(MapSymmetry.SPIN.asOption());

            options.add(new PowerupOption(loc.addXYZ(158.5, 6.5, 28.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(65.5, 6.5, 98.5), PowerupType.ENERGY));

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
            options.add(new WinAfterTimeoutOption());
            options.add(new GameOvertimeOption());
            options.add(new ScoreOnEventOption.FlagCapture());
            options.add(new ScoreOnEventOption.OnKill());
            options.add(new RespawnWaveOption());
            options.add(new RespawnProtectionOption());
            options.add(new GraveOption());

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
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(71.5, 40, -71.5, 90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(73.5, 41, 213.5, -90, 0), Team.RED).asOption());
            options.add(MapSymmetry.SPIN.asOption());

            options.add(new PowerupOption(loc.addXYZ(102.5, 21.5, 51.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(42.5, 21.5, 92.5), PowerupType.ENERGY));

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
            options.add(new WinAfterTimeoutOption());
            options.add(new GameOvertimeOption());
            options.add(new ScoreOnEventOption.FlagCapture());
            options.add(new ScoreOnEventOption.OnKill());
            options.add(new RespawnWaveOption());
            options.add(new RespawnProtectionOption());
            options.add(new GraveOption());

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
            new MapCategory[0]
    ) {
        @Override
        public List<Option> initMap(MapCategory category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(43.5, 77, -216.5).yaw(180), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-134.5, 77, -216.5), Team.RED).asOption());
            options.add(MapSymmetry.SPIN.asOption());

            options.add(new PowerupOption(loc.addXYZ(-2.5, 61.5, -236.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(-88.5, 61.5, -196.5), PowerupType.ENERGY));

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
            options.add(new WinAfterTimeoutOption());
            options.add(new GameOvertimeOption());
            options.add(new ScoreOnEventOption.FlagCapture());
            options.add(new ScoreOnEventOption.OnKill());
            options.add(new RespawnWaveOption());
            options.add(new RespawnProtectionOption());
            options.add(new GraveOption());

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
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-22.5, 39, -83.5).yaw(180), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(23.5, 39, 83.5), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(5.5, 15.5, -33.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(-4.5, 15.5, 34.5), PowerupType.ENERGY));

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
            options.add(new WinAfterTimeoutOption());
            options.add(new GameOvertimeOption());
            options.add(new ScoreOnEventOption.FlagCapture());
            options.add(new ScoreOnEventOption.OnKill());
            options.add(new RespawnWaveOption());
            options.add(new RespawnProtectionOption());
            options.add(new GraveOption());

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
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(727.5, 8.5, 200.5), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(727.5, 8.5, 196.5), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 184.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 188.5), PowerupType.ENERGY));

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
            options.add(new WinAfterTimeoutOption());
            options.add(new GameOvertimeOption());
            options.add(new ScoreOnEventOption.FlagCapture());
            options.add(new ScoreOnEventOption.OnKill());
            options.add(new RespawnWaveOption());
            options.add(new RespawnProtectionOption());
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            return options;
        }

    },
    SIEGE(
            "Siege",
            28,
            10,
            60 * SECOND,
            "",
            MapCategory.TEAM_DEATHMATCH
    ) {
        @Override
        public List<Option> initMap(MapCategory category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);
            options.add(new MarkerOption(
                    TeamMarker.create(Team.BLUE, Team.RED),
                    LobbyLocationMarker.create(loc.addXYZ(-22.5, 39, -83.5).yaw(180), Team.BLUE),
                    LobbyLocationMarker.create(loc.addXYZ(23.5, 39, 83.5), Team.RED)
            ));
            options.add(new PowerupOption(loc.addXYZ(5.5, 15.5, -33.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(-4.5, 15.5, 34.5), PowerupType.ENERGY));

            options.add(new PowerupOption(loc.addXYZ(4.5, 25.5, -86.5), PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(-3.5, 25.5, 87.5), PowerupType.SPEED));

            options.add(new PowerupOption(loc.addXYZ(57.5, 15.5, 1.5), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(-56.5, 15.5, -0.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(39.5, 28.5, -97.5), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-38.5, 28.5, 97.5), Team.RED));

            options.add(new GateOption(loc.addXYZ(-26, 33, -96), loc.addXYZ(-19, 40, -96)));
            options.add(new GateOption(loc.addXYZ(-28, 31, -81), loc.addXYZ(-28, 41, -75)));
            options.add(new GateOption(loc.addXYZ(20, 33, 96), loc.addXYZ(26, 42, 96)));
            options.add(new GateOption(loc.addXYZ(29, 31, 76), loc.addXYZ(29, 41, 82)));

            options.add(new WinByPointsOption());
            options.add(new MercyWinOption());
            options.add(new WinAfterTimeoutOption());
            options.add(new ScoreOnEventOption.OnKill(15));
            options.add(new RespawnWaveOption());
            options.add(new RespawnProtectionOption());
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            return options;
        }

    },
    FALSTAD_GATE(
            "Falstad Gate",
            28,
            10,
            60 * SECOND,
            "",
            MapCategory.TEAM_DEATHMATCH
    ) {
        @Override
        public List<Option> initMap(MapCategory category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);
            options.add(new MarkerOption(
                    TeamMarker.create(Team.BLUE, Team.RED),
                    LobbyLocationMarker.create(loc.addXYZ(-22.5, 39, -83.5).yaw(180), Team.BLUE),
                    LobbyLocationMarker.create(loc.addXYZ(23.5, 39, 83.5), Team.RED)
            ));
            options.add(new PowerupOption(loc.addXYZ(5.5, 15.5, -33.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(-4.5, 15.5, 34.5), PowerupType.ENERGY));

            options.add(new PowerupOption(loc.addXYZ(4.5, 25.5, -86.5), PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(-3.5, 25.5, 87.5), PowerupType.SPEED));

            options.add(new PowerupOption(loc.addXYZ(57.5, 15.5, 1.5), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(-56.5, 15.5, -0.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(39.5, 28.5, -97.5), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-38.5, 28.5, 97.5), Team.RED));

            options.add(new GateOption(loc.addXYZ(-26, 33, -96), loc.addXYZ(-19, 40, -96)));
            options.add(new GateOption(loc.addXYZ(-28, 31, -81), loc.addXYZ(-28, 41, -75)));
            options.add(new GateOption(loc.addXYZ(20, 33, 96), loc.addXYZ(26, 42, 96)));
            options.add(new GateOption(loc.addXYZ(29, 31, 76), loc.addXYZ(29, 41, 82)));

            options.add(new WinByPointsOption());
            options.add(new MercyWinOption());
            options.add(new WinAfterTimeoutOption());
            options.add(new ScoreOnEventOption.OnKill(15));
            options.add(new RespawnWaveOption());
            options.add(new RespawnProtectionOption());
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            return options;
        }

    },
    BLACK_TEMPLE(
            "Black Temple",
            28,
            10,
            60 * SECOND,
            "",
            MapCategory.TEAM_DEATHMATCH
    ) {
        @Override
        public List<Option> initMap(MapCategory category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);
            options.add(new MarkerOption(
                    TeamMarker.create(Team.BLUE, Team.RED),
                    LobbyLocationMarker.create(loc.addXYZ(-22.5, 39, -83.5).yaw(180), Team.BLUE),
                    LobbyLocationMarker.create(loc.addXYZ(23.5, 39, 83.5), Team.RED)
            ));
            options.add(new PowerupOption(loc.addXYZ(5.5, 15.5, -33.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(-4.5, 15.5, 34.5), PowerupType.ENERGY));

            options.add(new PowerupOption(loc.addXYZ(4.5, 25.5, -86.5), PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(-3.5, 25.5, 87.5), PowerupType.SPEED));

            options.add(new PowerupOption(loc.addXYZ(57.5, 15.5, 1.5), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(-56.5, 15.5, -0.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(39.5, 28.5, -97.5), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-38.5, 28.5, 97.5), Team.RED));

            options.add(new GateOption(loc.addXYZ(-26, 33, -96), loc.addXYZ(-19, 40, -96)));
            options.add(new GateOption(loc.addXYZ(-28, 31, -81), loc.addXYZ(-28, 41, -75)));
            options.add(new GateOption(loc.addXYZ(20, 33, 96), loc.addXYZ(26, 42, 96)));
            options.add(new GateOption(loc.addXYZ(29, 31, 76), loc.addXYZ(29, 41, 82)));

            options.add(new WinByPointsOption());
            options.add(new MercyWinOption());
            options.add(new WinAfterTimeoutOption());
            options.add(new ScoreOnEventOption.OnKill(15));
            options.add(new RespawnWaveOption());
            options.add(new RespawnProtectionOption());
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            return options;
        }

    },
    HEAVEN_WILL(
            "Heaven's Will",
            2,
            2,
            60 * SECOND,
            "",
            MapCategory.DUEL
            ) {
        @Override
        public List<Option> initMap(MapCategory category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(726.5, 8, 176.5).yaw(-140), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(756.5, 7, 143.5).yaw(40), Team.RED).asOption());
            
            options.add(new GateOption(loc, 723, 5, 173, 729, 11, 179));
            options.add(new GateOption(loc, 753, 4, 140, 759, 10, 146));

            options.add(new PowerupOption(loc.addXYZ(758.5, 12.5, 169.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(717.5, 10.5, 147.5), PowerupType.ENERGY));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(726.5, 8, 176.5).yaw(-140), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(756.5, 7, 143.5).yaw(40), Team.RED));

            //options.add(new GateOption(loc.addXYZ(-26, 33, -96), loc.addXYZ(-19, 40, -96)));
            //options.add(new GateOption(loc.addXYZ(20, 33, 96), loc.addXYZ(26, 42, 96)));

            options.add(new WinByPointsOption(5));
            options.add(new WinAfterTimeoutOption());
            options.add(new ScoreOnEventOption.OnKill(1));
            options.add(new RespawnWaveOption(0, 1, 0));
            options.add(new GraveOption());
            options.add(new DuelsTeleportOption());

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
