package com.ebicep.warlords.game;

import com.ebicep.warlords.game.option.*;
import com.ebicep.warlords.game.option.PowerupOption.PowerupType;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.state.PreLobbyState;
import com.ebicep.warlords.game.state.State;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

// MAPS:
// "Crossfire"
// "Rift"
// "Valley"
// "Warsong"
// "Gorge"
// "Debug"
public enum GameMap {
    RIFT(
            "The Rift",
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
            options.add(LobbyLocationMarker.create(loc.addXYZ(-86.5, 46, -33.5), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(87.5, 46, 35.5, 180, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(-32.5, 25.5, 49.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(33.5, 25.5, -48.5), PowerupType.ENERGY));

            options.add(new PowerupOption(loc.addXYZ(-54.5, 36.5, 24.5), PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(55.5, 36.5, -23.5), PowerupType.SPEED));

            options.add(new PowerupOption(loc.addXYZ(-0.5, 24.5, 64.5), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(1.5, 24.5, -62.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-32.5, 34.5, -43.5, -90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(33, 34.5, 45, 90, 0), Team.RED));

            options.add(new FlagCapturePointOption(loc.addXYZ(-98.5, 45.5, -17.5, -90, 0), Team.BLUE));
            options.add(new FlagSpawnPointOption(loc.addXYZ(-98.5, 45.5, -17.5, -90, 0), Team.BLUE));

            options.add(new FlagCapturePointOption(loc.addXYZ(99.5, 45.5, 17.5, 90, 0), Team.RED));
            options.add(new FlagSpawnPointOption(loc.addXYZ(99.5, 45.5, 17.5, 90, 0), Team.RED));

            options.add(new AbstractScoreOnEventOption.FlagCapture(250));

            options.add(new GateOption(loc, -79, 45, -29, -79, 49, -24));
            options.add(new GateOption(loc, -91, 45, -6, -86, 49, -6));
            options.add(new GateOption(loc, 79, 45, 25, 79, 49, 29));
            options.add(new GateOption(loc, 87, 45, 6, 91, 49, 6));

            options.add(new WinByPointsOption());
            options.add(new MercyWinOption());
            if (addons.contains(GameAddon.DOUBLE_TIME)) {
                options.add(new WinAfterTimeoutOption(1800));
            } else {
                options.add(new WinAfterTimeoutOption());
            }
            options.add(new GameOvertimeOption());
            options.add(new AbstractScoreOnEventOption.OnKill(5));
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

            options.add(new PowerupOption(loc.addXYZ(158.5, 6.5, 28.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(65.5, 6.5, 98.5), PowerupType.ENERGY));

            options.add(new PowerupOption(loc.addXYZ(217.5, 36.5, 89.5), PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(6.5, 36.5, 39.5), PowerupType.SPEED));

            options.add(new PowerupOption(loc.addXYZ(96.5, 6.5, 108.5), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(127.5, 6.5, 19.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(133, 11.5, 130.5, 125, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(91, 11.5, -2.5, -55, 0), Team.RED));

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
            if (addons.contains(GameAddon.DOUBLE_TIME)) {
                options.add(new WinAfterTimeoutOption(1800));
            } else {
                options.add(new WinAfterTimeoutOption());
            }
            options.add(new GameOvertimeOption());
            options.add(new AbstractScoreOnEventOption.FlagCapture());
            options.add(new AbstractScoreOnEventOption.OnKill());
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

            options.add(new PowerupOption(loc.addXYZ(102.5, 21.5, 51.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(42.5, 21.5, 92.5), PowerupType.ENERGY));

            options.add(new PowerupOption(loc.addXYZ(63.5, 33.5, -31.5), PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(79.5, 32.5, 167.5), PowerupType.SPEED));

            options.add(new PowerupOption(loc.addXYZ(44.5, 20.5, 42.5), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(100.5, 20.5, 101.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(45.5, 29.5, 15.5, -30, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(99.5, 29.5, 128.5, 150, 0), Team.RED));

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
            if (addons.contains(GameAddon.DOUBLE_TIME)) {
                options.add(new WinAfterTimeoutOption(1800));
            } else {
                options.add(new WinAfterTimeoutOption());
            }
            options.add(new GameOvertimeOption());
            options.add(new AbstractScoreOnEventOption.FlagCapture());
            options.add(new AbstractScoreOnEventOption.OnKill());
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
            options.add(LobbyLocationMarker.create(loc.addXYZ(-22.5, 39, -83.5, -180, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(23.5, 39, 83.5), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(5.5, 15.5, -33.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(-4.5, 15.5, 34.5), PowerupType.ENERGY));

            options.add(new PowerupOption(loc.addXYZ(4.5, 25.5, -86.5), PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(-3.5, 25.5, 87.5), PowerupType.SPEED));

            options.add(new PowerupOption(loc.addXYZ(57.5, 15.5, 1.5), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(-56.5, 15.5, -0.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(39.5, 28.5, -97.5), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-38.5, 28.5, 97.5, -180, 0), Team.RED));

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
            if (addons.contains(GameAddon.DOUBLE_TIME)) {
                options.add(new WinAfterTimeoutOption(1800));
            } else {
                options.add(new WinAfterTimeoutOption());
            }
            options.add(new GameOvertimeOption());
            options.add(new AbstractScoreOnEventOption.FlagCapture());
            options.add(new AbstractScoreOnEventOption.OnKill());
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

            options.add(new PowerupOption(loc.addXYZ(577.5, 22.5, 286.5), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(623.5, 22.5, 188.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(560.5, 32.5, 294.5, -140, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(640.5, 32.5, 180.5, 40, 0), Team.RED));

            options.add(new FlagCapturePointOption(loc.addXYZ(522.5, 37.5, 233.5, 90, 0), Team.BLUE));
            options.add(new FlagSpawnPointOption(loc.addXYZ(522.5, 37.5, 233.5, 90, 0), Team.BLUE));

            options.add(new FlagCapturePointOption(loc.addXYZ(678.5, 37.5, 241.1, -90, 0), Team.RED));
            options.add(new FlagSpawnPointOption(loc.addXYZ(678.5, 37.5, 241.1, -90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(665, 36, 228), loc.addXYZ(665, 40, 232)));
            options.add(new GateOption(loc.addXYZ(683, 36, 252), loc.addXYZ(690, 40, 252)));
            options.add(new GateOption(loc.addXYZ(663, 42, 212), loc.addXYZ(663, 46, 215)));

            options.add(new GateOption(loc.addXYZ(535, 36, 242), loc.addXYZ(535, 40, 247)));
            options.add(new GateOption(loc.addXYZ(510, 36, 222), loc.addXYZ(518, 40, 222)));
            options.add(new GateOption(loc.addXYZ(537, 42, 262), loc.addXYZ(537, 46, 259)));

            options.add(new WinByPointsOption());
            options.add(new MercyWinOption());
            if (addons.contains(GameAddon.DOUBLE_TIME)) {
                options.add(new WinAfterTimeoutOption(1800));
            } else {
                options.add(new WinAfterTimeoutOption());
            }
            options.add(new GameOvertimeOption());
            options.add(new AbstractScoreOnEventOption.FlagCapture());
            options.add(new AbstractScoreOnEventOption.OnKill());
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
            32,
            12,
            60 * SECOND,
            "",
            GameMode.TEAM_DEATHMATCH
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(63.5, 63.5, -0.5), PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(147.5, 80.5, 0.5), PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(39.5, 64.5, 57.5), PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(-20.5, 65.5, 0.5), PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(-53.5, 83.5, 62.5), PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(-55.5, 83.5, -60.5), PowerupType.SPEED));

            options.add(new PowerupOption(loc.addXYZ(71.5, 65.5, -62.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(23.5, 65.5, -50.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(100.5, 79.5, 11.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(-25.5, 64.5, 32.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(-26.5, 63.5, -35.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(23.5, 62.5, 45.5), PowerupType.ENERGY));

            options.add(new PowerupOption(loc.addXYZ(24.5, 62.5, -23.5), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(83.5, 65.5, 64.5), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(23.5, 62.5, 7), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(100.5, 79.5, -10.5), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(-16.5, 84.5, 15.5), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(-16.5, 84.5, -14.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(83, 84, 4), loc.addXYZ(83, 79, -4)));
            options.add(new GateOption(loc.addXYZ(-70, 83, 3), loc.addXYZ(727, 86, -2), Material.IRON_FENCE));

            options.add(new WinByPointsOption(1000));
            options.add(new MercyWinOption());
            if (addons.contains(GameAddon.DOUBLE_TIME)) {
                options.add(new WinAfterTimeoutOption(1800));
            } else {
                options.add(new WinAfterTimeoutOption());
            }
            options.add(new GameOvertimeOption());
            options.add(new AbstractScoreOnEventOption.OnKill(15));
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
            12,
            60 * SECOND,
            "",
            GameMode.TEAM_DEATHMATCH
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(455.5, 67.5, 423.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(604.5, 56.5, 465.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(298.5, 56.5, 430.5), PowerupType.DAMAGE));
            options.add(new PowerupOption(loc.addXYZ(425.5, 15.5, 272.5), PowerupType.DAMAGE));

            options.add(new PowerupOption(loc.addXYZ(449.5, 95.5, 600.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(183, 67, 447), loc.addXYZ(183, 64, 443)));
            options.add(new GateOption(loc.addXYZ(727, 67, 437), loc.addXYZ(727, 64, 441)));

            options.add(new WinByPointsOption(1000));
            options.add(new MercyWinOption());
            if (addons.contains(GameAddon.DOUBLE_TIME)) {
                options.add(new WinAfterTimeoutOption(1800));
            } else {
                options.add(new WinAfterTimeoutOption());
            }
            options.add(new GameOvertimeOption());
            options.add(new AbstractScoreOnEventOption.OnKill(15));
            options.add(new RespawnWaveOption());
            options.add(new RespawnProtectionOption());
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            return options;
        }

    },
    RUINS(
            "Ruins",
            32,
            12,
            60 * SECOND,
            "",
            GameMode.TEAM_DEATHMATCH
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(455.5, 67.5, 423.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(604.5, 56.5, 465.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(298.5, 56.5, 430.5), PowerupType.DAMAGE));
            options.add(new PowerupOption(loc.addXYZ(425.5, 15.5, 272.5), PowerupType.DAMAGE));

            options.add(new PowerupOption(loc.addXYZ(449.5, 95.5, 600.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(183, 67, 447), loc.addXYZ(183, 64, 443)));
            options.add(new GateOption(loc.addXYZ(727, 67, 437), loc.addXYZ(727, 64, 441)));

            options.add(new WinByPointsOption(1000));
            options.add(new MercyWinOption());
            if (addons.contains(GameAddon.DOUBLE_TIME)) {
                options.add(new WinAfterTimeoutOption(1800));
            } else {
                options.add(new WinAfterTimeoutOption());
            }
            options.add(new GameOvertimeOption());
            options.add(new AbstractScoreOnEventOption.OnKill(15));
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
            32,
            12,
            60 * SECOND,
            "",
            GameMode.TEAM_DEATHMATCH
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(455.5, 67.5, 423.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(604.5, 56.5, 465.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(298.5, 56.5, 430.5), PowerupType.DAMAGE));
            options.add(new PowerupOption(loc.addXYZ(425.5, 15.5, 272.5), PowerupType.DAMAGE));

            options.add(new PowerupOption(loc.addXYZ(449.5, 95.5, 600.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(183, 67, 447), loc.addXYZ(183, 64, 443)));
            options.add(new GateOption(loc.addXYZ(727, 67, 437), loc.addXYZ(727, 64, 441)));

            options.add(new WinByPointsOption(1000));
            options.add(new MercyWinOption());
            if (addons.contains(GameAddon.DOUBLE_TIME)) {
                options.add(new WinAfterTimeoutOption(1800));
            } else {
                options.add(new WinAfterTimeoutOption());
            }
            options.add(new GameOvertimeOption());
            options.add(new AbstractScoreOnEventOption.OnKill(15));
            options.add(new RespawnWaveOption());
            options.add(new RespawnProtectionOption());
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            return options;
        }

    },
    STORMWIND(
            "Stormwind",
            28,
            12,
            60 * SECOND,
            "",
            GameMode.TEAM_DEATHMATCH
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(455.5, 67.5, 423.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(604.5, 56.5, 465.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(298.5, 56.5, 430.5), PowerupType.DAMAGE));
            options.add(new PowerupOption(loc.addXYZ(425.5, 15.5, 272.5), PowerupType.DAMAGE));

            options.add(new PowerupOption(loc.addXYZ(449.5, 95.5, 600.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(183, 67, 447), loc.addXYZ(183, 64, 443)));
            options.add(new GateOption(loc.addXYZ(727, 67, 437), loc.addXYZ(727, 64, 441)));

            options.add(new WinByPointsOption(1000));
            options.add(new MercyWinOption());
            if (addons.contains(GameAddon.DOUBLE_TIME)) {
                options.add(new WinAfterTimeoutOption(1800));
            } else {
                options.add(new WinAfterTimeoutOption());
            }
            options.add(new GameOvertimeOption());
            options.add(new AbstractScoreOnEventOption.OnKill(15));
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
            60,
            18,
            60 * SECOND,
            "",
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

            options.add(new GateOption(loc.addXYZ(183, 67, 447), loc.addXYZ(183, 64, 443)));
            options.add(new GateOption(loc.addXYZ(727, 67, 437), loc.addXYZ(727, 64, 441)));

            options.add(new WinByPointsOption(2000));
            options.add(new MercyWinOption());
            if (addons.contains(GameAddon.DOUBLE_TIME)) {
                options.add(new WinAfterTimeoutOption(1800));
            } else {
                options.add(new WinAfterTimeoutOption());
            }
            options.add(new GameOvertimeOption());
            options.add(new AbstractScoreOnEventOption.FlagCapture());
            options.add(new AbstractScoreOnEventOption.OnKill());
            options.add(new RespawnWaveOption());
            options.add(new RespawnProtectionOption());
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            return options;
        }

    },
    DORIVEN_BASIN(
            "Doriven Basin",
            60,
            18,
            60 * SECOND,
            "",
            GameMode.INTERCEPTION
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(455.5, 67.5, 423.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(604.5, 56.5, 465.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(298.5, 56.5, 430.5), PowerupType.DAMAGE));
            options.add(new PowerupOption(loc.addXYZ(425.5, 15.5, 272.5), PowerupType.DAMAGE));

            options.add(new PowerupOption(loc.addXYZ(449.5, 95.5, 600.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(183, 67, 447), loc.addXYZ(183, 64, 443)));
            options.add(new GateOption(loc.addXYZ(727, 67, 437), loc.addXYZ(727, 64, 441)));

            options.add(new WinByPointsOption(1000));
            options.add(new MercyWinOption());
            if (addons.contains(GameAddon.DOUBLE_TIME)) {
                options.add(new WinAfterTimeoutOption(1800));
            } else {
                options.add(new WinAfterTimeoutOption());
            }
            options.add(new GameOvertimeOption());
            options.add(new AbstractScoreOnEventOption.OnKill(15));
            options.add(new RespawnWaveOption());
            options.add(new RespawnProtectionOption());
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            return options;
        }

    },
    SCORCHED(
            "Scorched",
            60,
            18,
            60 * SECOND,
            "",
            GameMode.INTERCEPTION
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(455.5, 67.5, 423.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(604.5, 56.5, 465.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(298.5, 56.5, 430.5), PowerupType.DAMAGE));
            options.add(new PowerupOption(loc.addXYZ(425.5, 15.5, 272.5), PowerupType.DAMAGE));

            options.add(new PowerupOption(loc.addXYZ(449.5, 95.5, 600.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(183, 67, 447), loc.addXYZ(183, 64, 443)));
            options.add(new GateOption(loc.addXYZ(727, 67, 437), loc.addXYZ(727, 64, 441)));

            options.add(new WinByPointsOption(1000));
            options.add(new MercyWinOption());
            if (addons.contains(GameAddon.DOUBLE_TIME)) {
                options.add(new WinAfterTimeoutOption(1800));
            } else {
                options.add(new WinAfterTimeoutOption());
            }
            options.add(new GameOvertimeOption());
            options.add(new AbstractScoreOnEventOption.OnKill(15));
            options.add(new RespawnWaveOption());
            options.add(new RespawnProtectionOption());
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            return options;
        }

    },
    PHANTOM(
            "Phantom",
            60,
            18,
            60 * SECOND,
            "",
            GameMode.INTERCEPTION
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(455.5, 67.5, 423.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(604.5, 56.5, 465.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(298.5, 56.5, 430.5), PowerupType.DAMAGE));
            options.add(new PowerupOption(loc.addXYZ(425.5, 15.5, 272.5), PowerupType.DAMAGE));

            options.add(new PowerupOption(loc.addXYZ(449.5, 95.5, 600.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(183, 67, 447), loc.addXYZ(183, 64, 443)));
            options.add(new GateOption(loc.addXYZ(727, 67, 437), loc.addXYZ(727, 64, 441)));

            options.add(new WinByPointsOption(1000));
            options.add(new MercyWinOption());
            if (addons.contains(GameAddon.DOUBLE_TIME)) {
                options.add(new WinAfterTimeoutOption(1800));
            } else {
                options.add(new WinAfterTimeoutOption());
            }
            options.add(new GameOvertimeOption());
            options.add(new AbstractScoreOnEventOption.OnKill(15));
            options.add(new RespawnWaveOption());
            options.add(new RespawnProtectionOption());
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            return options;
        }

    },
    NEOLITHIC(
            "Neolithic",
            60,
            18,
            60 * SECOND,
            "",
            GameMode.INTERCEPTION
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(455.5, 67.5, 423.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(604.5, 56.5, 465.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(298.5, 56.5, 430.5), PowerupType.DAMAGE));
            options.add(new PowerupOption(loc.addXYZ(425.5, 15.5, 272.5), PowerupType.DAMAGE));

            options.add(new PowerupOption(loc.addXYZ(449.5, 95.5, 600.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(183, 67, 447), loc.addXYZ(183, 64, 443)));
            options.add(new GateOption(loc.addXYZ(727, 67, 437), loc.addXYZ(727, 64, 441)));

            options.add(new WinByPointsOption(1000));
            options.add(new MercyWinOption());
            if (addons.contains(GameAddon.DOUBLE_TIME)) {
                options.add(new WinAfterTimeoutOption(1800));
            } else {
                options.add(new WinAfterTimeoutOption());
            }
            options.add(new GameOvertimeOption());
            options.add(new AbstractScoreOnEventOption.OnKill(15));
            options.add(new RespawnWaveOption());
            options.add(new RespawnProtectionOption());
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            return options;
        }

    },
    SUN_AND_MOON(
            "Sun and Moon",
            60,
            18,
            60 * SECOND,
            "",
            GameMode.INTERCEPTION
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(115.5, 89.5, 0.5, 90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-114.5, 89.5, 0.5, -90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(0.5, 77.5, -16.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(8.5, 73.5, -104.5), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(-7.5, 73.5, 105.5), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(28.5, 85.5, 0.5), PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(-27.5, 85.5, 0.5), PowerupType.SPEED));

            options.add(new InterceptionPointOption("Eclipse", loc.addXYZ(0.5, 85, 0.5)));
            options.add(new InterceptionPointOption("Glacier", loc.addXYZ(23.5, 62, 70.5)));
            options.add(new InterceptionPointOption("Sun", loc.addXYZ(-18.5, 67, 134.5)));
            options.add(new InterceptionPointOption("Moon", loc.addXYZ(19.5, 67, -133.5)));
            options.add(new InterceptionPointOption("Valley", loc.addXYZ(-22.5, 62, -69.5)));

            options.add(new AbstractScoreOnEventOption.OnInterceptionCapture(25));
            options.add(new AbstractScoreOnEventOption.OnInterceptionTimer(1));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(115.5, 89.5, 0.5, 90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-114.5, 89.5, 0.5, -90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(-99, 89, 4), loc.addXYZ(-99, 93, -3)));
            options.add(new GateOption(loc.addXYZ(100, 89, 4), loc.addXYZ(100, 93, -3)));

            options.add(new WinByPointsOption(1500));
            if (addons.contains(GameAddon.DOUBLE_TIME)) {
                options.add(new WinAfterTimeoutOption(2400));
            } else {
                options.add(new WinAfterTimeoutOption(1200));
            }
            options.add(new GameOvertimeOption());
            options.add(new RespawnWaveOption());
            options.add(new RespawnProtectionOption());
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            return options;
        }

    },
    DEATH_VALLEY(
            "Death Valley",
            60,
            18,
            60 * SECOND,
            "",
            GameMode.INTERCEPTION
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(455.5, 67.5, 423.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(604.5, 56.5, 465.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(298.5, 56.5, 430.5), PowerupType.DAMAGE));
            options.add(new PowerupOption(loc.addXYZ(425.5, 15.5, 272.5), PowerupType.DAMAGE));

            options.add(new PowerupOption(loc.addXYZ(449.5, 95.5, 600.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(183, 67, 447), loc.addXYZ(183, 64, 443)));
            options.add(new GateOption(loc.addXYZ(727, 67, 437), loc.addXYZ(727, 64, 441)));

            options.add(new WinByPointsOption(1000));
            options.add(new MercyWinOption());
            if (addons.contains(GameAddon.DOUBLE_TIME)) {
                options.add(new WinAfterTimeoutOption(1800));
            } else {
                options.add(new WinAfterTimeoutOption());
            }
            options.add(new GameOvertimeOption());
            options.add(new AbstractScoreOnEventOption.OnKill(15));
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
            if (addons.contains(GameAddon.DOUBLE_TIME)) {
                options.add(new WinAfterTimeoutOption(1800));
            } else {
                options.add(new WinAfterTimeoutOption());
            }
            options.add(new GameOvertimeOption());
            options.add(new AbstractScoreOnEventOption.FlagCapture());
            options.add(new AbstractScoreOnEventOption.OnKill());
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
            options.add(LobbyLocationMarker.create(loc.addXYZ(726.5, 9, 176.5).yaw(-140), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(756.5, 8, 143.5).yaw(40), Team.RED).asOption());

            options.add(new GateOption(loc, 723, 6, 173, 729, 12, 179));
            options.add(new GateOption(loc, 753, 6, 140, 759, 12, 146));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(726.5, 9, 176.5).yaw(-140), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(756.5, 8, 143.5).yaw(40), Team.RED));

            options.add(new WinByPointsOption(5));
            options.add(new MercyWinOption(3));
            options.add(new WinAfterTimeoutOption(300));
            options.add(new AbstractScoreOnEventOption.OnKill(1));
            options.add(new RespawnWaveOption(0, 1, 0));
            options.add(new GraveOption());
            options.add(new DuelsTeleportOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            return options;
        }

    }

    ;

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

    public List<GameMode> getGameModes() {
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
