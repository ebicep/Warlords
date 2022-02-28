package com.ebicep.warlords.game;

import com.ebicep.warlords.game.option.*;
import com.ebicep.warlords.game.option.PowerupOption.PowerupType;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.state.PreLobbyState;
import com.ebicep.warlords.game.state.State;
import com.ebicep.warlords.util.LocationFactory;

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
            GameMode.CAPTURE_THE_FLAG,
            GameMode.INTERCEPTION
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
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

            switch (category) {
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
            "Illusion Rift",
            11,
            9,
            60 * SECOND,
            "",
            GameMode.SIMULATION_TRIAL
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
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

            options.add(new WinByPointsOption(400));
            options.add(new MercyWinOption());
            options.add(new WinAfterTimeoutOption());
            options.add(new ScoreOnEventOption.FlagReturn());
            options.add(new ScoreOnEventOption.FlagHolding(2));
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
            GameMode.CAPTURE_THE_FLAG
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
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
    SIMULATION_CROSSFIRE(
            "Illusion Crossfire",
            11,
            9,
            60 * SECOND,
            "",
            GameMode.SIMULATION_TRIAL
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(115.5, 6, 23.5), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(214.5, 36, 116.5), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(158.5, 6.5, 28.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(217.5, 36.5, 89.5), PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(96.5, 6.5, 108.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(133, 11.5, 130.5, 125, 0), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(90.5, 11.5, 0.5, -45, 0), Team.BLUE));

            options.add(new FlagSpawnPointOption(loc.addXYZ(225.5, 44.5, 93.5), Team.BLUE));

            options.add(new GateOption(loc, 110, 4, 18, 120, 11, 28));
            options.add(new GateOption(loc, 209, 34, 111, 219, 42, 121));

            options.add(new WinByPointsOption(400));
            options.add(new MercyWinOption());
            options.add(new WinAfterTimeoutOption());
            options.add(new ScoreOnEventOption.FlagReturn());
            options.add(new ScoreOnEventOption.FlagHolding(2));
            options.add(new SimulationTeleportOption());
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
            GameMode.CAPTURE_THE_FLAG
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
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
    VALLEY(
            "Atherrough Valley",
            32,
            12,
            60 * SECOND,
            "",
            GameMode.CAPTURE_THE_FLAG
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
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
    APERTURE(
            "Aperture",
            32,
            12,
            60 * SECOND,
            "",
            GameMode.CAPTURE_THE_FLAG
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(513.5, 37.5, 243.5, -90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(687.5, 37.5, 231.5, 90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(608.5, 16.5, 280.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(592.5, 16.5, 194.5), PowerupType.ENERGY));

            options.add(new PowerupOption(loc.addXYZ(551.5, 31.5, 217.5), PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(649.5, 31.5, 257.5), PowerupType.SPEED));

            options.add(new PowerupOption(loc.addXYZ(577.5, 21.5, 286.5), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(623.5, 21.5, 188.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(560.5, 32.5, 294.5, -140, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(640.5, 32.5, 180.5, 50, 0), Team.RED));

            options.add(new FlagCapturePointOption(loc.addXYZ(522.5, 37.5, 233.5, 90, 0), Team.BLUE));
            options.add(new FlagSpawnPointOption(loc.addXYZ(522.5, 37.5, 233.5, 90, 0), Team.BLUE));

            options.add(new FlagCapturePointOption(loc.addXYZ(678.5, 37.5, 241.1, -90, 0), Team.RED));
            options.add(new FlagSpawnPointOption(loc.addXYZ(678.5, 37.5, 241.1, -90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(665, 36, 228), loc.addXYZ(665, 40, 232)));
            options.add(new GateOption(loc.addXYZ(683, 36, 252), loc.addXYZ(690, 40, 252)));

            options.add(new GateOption(loc.addXYZ(535, 36, 242), loc.addXYZ(535, 40, 247)));
            options.add(new GateOption(loc.addXYZ(510, 36, 222), loc.addXYZ(518, 40, 222)));

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
    ARATHI(
            "Arathi",
            48,
            16,
            60 * SECOND,
            "",
            GameMode.CAPTURE_THE_FLAG,
            GameMode.INTERCEPTION
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(173.5, 67, 426.5), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(736.5, 67, 450.5).yaw(-180), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(455.5, 67.5, 423.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(604.5, 56.5, 465.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(298.5, 56.5, 430.5), PowerupType.DAMAGE));
            options.add(new PowerupOption(loc.addXYZ(425.5, 15.5, 272.5), PowerupType.DAMAGE));

            options.add(new PowerupOption(loc.addXYZ(449.5, 95.5, 600.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(173.5, 67, 426.5), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(736.5, 67, 450.5, -180, 0), Team.RED));

            options.add(new FlagCapturePointOption(loc.addXYZ(162.5, 70.5, 445.5, -90, 0), Team.BLUE));
            options.add(new FlagSpawnPointOption(loc.addXYZ(162.5, 70.5, 445.5, -90, 0), Team.BLUE));

            options.add(new FlagCapturePointOption(loc.addXYZ(749.5, 70.5, 439.5, 90, 0), Team.RED));
            options.add(new FlagSpawnPointOption(loc.addXYZ(749.5, 70.5, 439.5, 90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(183, 67, 447), loc.addXYZ(183, 64, 443)));
            options.add(new GateOption(loc.addXYZ(727, 67, 437), loc.addXYZ(727, 64, 441)));

            options.add(new WinByPointsOption(2000));
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
            GameMode.DEBUG
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
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
    HEAVEN_WILL(
            "Heaven's Will",
            2,
            2,
            60 * SECOND,
            "",
            GameMode.DUEL
            ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
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
    private final List<GameMode> gameMode;

    GameMap(@Nonnull String mapName, int maxPlayers, int minPlayers, int lobbyCountdown, @Nonnull String mapDirPath, @Nonnull GameMode... gameMode) {
        this.mapName = mapName;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.lobbyCountdown = lobbyCountdown;
        this.mapDirPath = mapDirPath;
        this.gameMode = Collections.unmodifiableList(Arrays.asList(gameMode));
    }

    /**
     * Constructs the game instance
     *
     * @param category The map category to construct (for maps with multiple
     *                 configurations)
     * @param loc      The base location to construct the map
     * @param addons   The used addons
     * @return The initial list of options
     */
    public abstract List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons);

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

    public List<GameMode> getCategories() {
        return gameMode;
    }

    public static GameMap getGameMap(String mapName) {
        for (GameMap value : GameMap.values()) {
            if (value.mapName.equalsIgnoreCase(mapName)) {
                return value;
            }
        }
        return null;
    }
}
