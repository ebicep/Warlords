package com.ebicep.warlords.game;

import com.ebicep.warlords.game.option.*;
import com.ebicep.warlords.game.option.PowerupOption.PowerupType;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.wavedefense.CurrencyOnEventOption;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.Mobs;
import com.ebicep.warlords.game.option.wavedefense.waves.SimpleWave;
import com.ebicep.warlords.game.option.wavedefense.waves.StaticWaveList;
import com.ebicep.warlords.game.state.PreLobbyState;
import com.ebicep.warlords.game.state.State;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public enum GameMap {
    RIFT(
            "The Rift",
            32,
            12,
            60 * SECOND,
            "Rift",
            3,
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
            "Crossfire",
            3,
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
            "Warsong",
            3,
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
            "Atherrough_Valley",
            3,
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
            "Aperture",
            3,
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
    GORGE(
            "Gorge Remastered",
            32,
            12,
            60 * SECOND,
            "Gorge",
            0,
            GameMode.CAPTURE_THE_FLAG
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);
            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-86.5, 46, -33.5), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(43.5, 76, -216.5, 90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(-2.5, 61.5, -236.5), PowerupType.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(-88.5, 61.5, -196.5), PowerupType.ENERGY));

            options.add(new PowerupOption(loc.addXYZ(59.5, 71.5, -232.5), PowerupType.SPEED));
            options.add(new PowerupOption(loc.addXYZ(59.5, 36.5, -23.5), PowerupType.SPEED));

            options.add(new PowerupOption(loc.addXYZ(-12.5, 45.5, -194.5), PowerupType.HEALING));
            options.add(new PowerupOption(loc.addXYZ(1.5, 24.5, -62.5), PowerupType.HEALING));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(3.5, 71.5, -159.5, 135, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(33, 34.5, 45, 90, 0), Team.RED));

            options.add(new FlagCapturePointOption(loc.addXYZ(50.5, 76.5, -199.5, 180, 0), Team.BLUE));
            options.add(new FlagSpawnPointOption(loc.addXYZ(50.5, 76.5, -199.5, 180, 0), Team.BLUE));

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
    SIEGE(
            "Siege",
            32,
            12,
            60 * SECOND,
            "Siege",
            1,
            GameMode.TEAM_DEATHMATCH
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(72.5, 65.5, -60.5), PowerupType.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(39.5, 64.5, 58.5), PowerupType.SPEED, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(66.5, 63.5, 0.5), PowerupType.SPEED, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(145.5, 80.5, 0.5), PowerupType.SPEED, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-20.5, 65.5, 0.5), PowerupType.SPEED, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-54.5, 83.5, 63.5), PowerupType.SPEED, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-25.5, 63.5, -35.5), PowerupType.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-25.5, 64.5, 32.5), PowerupType.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(100.5, 79.5, 11.5), PowerupType.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-54.5, 83.5, -62.5), PowerupType.SPEED, 45, 30));

            options.add(new PowerupOption(loc.addXYZ(23.5, 62.5, 45.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(23.5, 65.5, -50.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(23.5, 62.5, 7.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(23.5, 62.5, -23.5), 45, 45));

            options.add(new PowerupOption(loc.addXYZ(-14.5, 84.5, -14.0), PowerupType.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(-14.5, 84.5, 15.5), PowerupType.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(83.5, 65.5, 64.5), PowerupType.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(100.5, 79.5, -10.5), PowerupType.HEALING, 45, 60));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(83, 84, 4), loc.addXYZ(83, 79, -4)));
            options.add(new GateOption(loc.addXYZ(-70, 83, 3), loc.addXYZ(-70, 86, -2), Material.IRON_FENCE));

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
            32,
            12,
            60 * SECOND,
            "FalstadGate",
            1,
            GameMode.TEAM_DEATHMATCH
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(93.5, 50.5, 0.5, 90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-93.5, 50.5, 0.5, -90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(78.5, 47.5, 18.5), PowerupType.SPEED, 45, 0));
            options.add(new PowerupOption(loc.addXYZ(78.5, 47.5, -17.5), PowerupType.SPEED, 45, 0));
            options.add(new PowerupOption(loc.addXYZ(0.5, 32.5, 0.5), PowerupType.SPEED, 45, 0));
            options.add(new PowerupOption(loc.addXYZ(-77.5, 47.5, -17.5), PowerupType.SPEED, 45, 0));
            options.add(new PowerupOption(loc.addXYZ(-77.5, 47.5, 18.5), PowerupType.SPEED, 45, 0));

            options.add(new PowerupOption(loc.addXYZ(0.5, 57.5, -39.5), PowerupType.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(0.5, 34.5, 23.5), PowerupType.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(0.5, 34.5, -22.5), PowerupType.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(0.5, 57.5, 40.5), PowerupType.ENERGY, 45, 30));

            options.add(new PowerupOption(loc.addXYZ(0.5, 34.5, -32.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(0.5, 34.5, 33.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(-4.5, 47.5, 33.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(5.5, 47.5, -32.5), 45, 45));

            options.add(new PowerupOption(loc.addXYZ(0.5, 53.5, 0.5), PowerupType.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(-25.5, 32.5, 0.5), PowerupType.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(26.5, 32.5, 0.5), PowerupType.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(0.5, 47.5, 0.5), PowerupType.HEALING, 45, 60));


            options.add(SpawnpointOption.forTeam(loc.addXYZ(93.5, 50.5, 0.5, 90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-93.5, 50.5, 0.5, -90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(-85, 50, -5), loc.addXYZ(-85, 53, 6), Material.IRON_FENCE));
            options.add(new GateOption(loc.addXYZ(85, 50, -5), loc.addXYZ(85, 53, 6), Material.IRON_FENCE));

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
            "Ruins",
            1,
            GameMode.TEAM_DEATHMATCH
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-139.5, 39.5, 0.5, -90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(144.5, 37.5, 0.5, 90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(0.5, 43.5, 32.5), PowerupType.SPEED, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(0.5, 43.5, -31.5), PowerupType.SPEED, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(0.5, 38.5, 8.5), PowerupType.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(0.5, 38.5, -7.5), PowerupType.ENERGY, 45, 30));

            options.add(new PowerupOption(loc.addXYZ(14.5, 35.5, -24.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(-13.5, 35.5, 25.5), 45, 45));

            options.add(new PowerupOption(loc.addXYZ(0.5, 25.5, 0.5), PowerupType.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(122.5, 45.5, 52.5), PowerupType.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(122.5, 45.5, -51.5), PowerupType.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(-126.5, 45.5, -51.5), PowerupType.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(-126.5, 45.5, 52.5), PowerupType.HEALING, 45, 60));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-139.5, 39.5, 0.5, -90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(144.5, 37.5, 0.5, 90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(-131, 40, -4), loc.addXYZ(-131, 48, 5), Material.IRON_FENCE));
            options.add(new GateOption(loc.addXYZ(131, 37, -4), loc.addXYZ(131, 45, 5), Material.IRON_FENCE));

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
            "BlackTemple",
            1,
            GameMode.TEAM_DEATHMATCH
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-0.5, 26.5, 105.5, -180, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(3.5, 26.5, -103.5, 0, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(94.5, 27.5, 7.5), PowerupType.SPEED, 45, 0));
            options.add(new PowerupOption(loc.addXYZ(33.5, 27.5, 16.5), PowerupType.SPEED, 45, 0));
            options.add(new PowerupOption(loc.addXYZ(-29.5, 27.5, 16.5), PowerupType.SPEED, 45, 0));
            options.add(new PowerupOption(loc.addXYZ(-90.5, 27.5, -5.5), PowerupType.SPEED, 45, 0));

            options.add(new PowerupOption(loc.addXYZ(33.5, 27.5, -14.5), PowerupType.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(94.5, 27.5, -5.5), PowerupType.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-29.5, 27.5, -14.5), PowerupType.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-90.5, 27.5, 7.5), PowerupType.ENERGY, 45, 30));

            options.add(new PowerupOption(loc.addXYZ(56.5, 30.5, 0.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(-52.5, 30.5, 0.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(4.5, 31.5, -3.5), PowerupType.ENERGY, 45, 45));
            options.add(new PowerupOption(loc.addXYZ(0.5, 31.5, 6.5), PowerupType.ENERGY, 45, 45));

            options.add(new PowerupOption(loc.addXYZ(60.5, 27.5, -28.5), PowerupType.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(-55.5, 27.5, -27.5), PowerupType.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(59.5, 27.5, 27.5), PowerupType.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(-55.5, 27.5, 27.5), PowerupType.HEALING, 45, 60));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(19, 26, -93), loc.addXYZ(19, 30, -102), Material.IRON_FENCE));

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
            "Stormwind",
            1,
            GameMode.TEAM_DEATHMATCH
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(8, 59, 109.5, 180, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(8, 59, -94.5, 0, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(3.5, 61.5, 3.5), PowerupType.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(12.5, 61.5, 12.5), PowerupType.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(76.5, 59.5, 56.5), PowerupType.SPEED, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(72.5, 56.5, -47.5), PowerupType.SPEED, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-56.5, 56.5, 63.5), PowerupType.SPEED, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-60.5, 59.5, -40.5), PowerupType.SPEED, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(83.5, 57.5, -2.5), PowerupType.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-67.5, 57.5, 18.5), PowerupType.ENERGY, 45, 30));

            options.add(new PowerupOption(loc.addXYZ(-28.5, 54.5, -27.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(-28.5, 54.5, 43.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(44.5, 54.5, -27.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(44.5, 54.5, 43.5), 45, 45));

            options.add(new PowerupOption(loc.addXYZ(93.5, 54.5, -41.5), PowerupType.ENERGY, 45, 45));
            options.add(new PowerupOption(loc.addXYZ(-80.5, 59.5, -28.5), PowerupType.ENERGY, 45, 45));
            options.add(new PowerupOption(loc.addXYZ(-77.5, 54.5, 57.5), PowerupType.ENERGY, 45, 45));
            options.add(new PowerupOption(loc.addXYZ(96.5, 59.5, 44.5), PowerupType.HEALING, 45, 45));

            options.add(new PowerupOption(loc.addXYZ(46.5, 56.5, 3.5), PowerupType.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(75.5, 54.5, -54.5), PowerupType.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(-30.5, 56.5, 12.5), PowerupType.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(-59.5, 54.5, 70.5), PowerupType.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(-47.5, 54.5, -16.5), PowerupType.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(63.5, 54.5, 32.5), PowerupType.HEALING, 45, 60));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(8, 59, 109.5, 180, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(8, 59, -94.5, 0, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(2, 54, 89), loc.addXYZ(14, 58, 89), Material.IRON_FENCE));
            options.add(new GateOption(loc.addXYZ(14, 54, -74), loc.addXYZ(2, 58, -74), Material.IRON_FENCE));

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
            "Arathi",
            3,
            GameMode.INTERCEPTION
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(173.5, 67, 426.5), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(736.5, 67, 450.5).yaw(-180), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(599.5, 56.5, 465.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(449.5, 95.5, 600.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(298.5, 56.5, 430.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(425.5, 15.5, 272.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(455.5, 67.5, 423.5), 45, 30));

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
            "DorivenBasin",
            1,
            GameMode.INTERCEPTION
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(116.5, 55.5, -12.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-116.5, 53.5, -11.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(87.5, 84.5, -101.5), PowerupType.HEALING, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-136.5, 84.5, 93.5), PowerupType.HEALING, 45, 30));

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
            "Scorched",
            0,
            GameMode.INTERCEPTION
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(113.5, 55.5, 5.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-112.5, 55.5, 5.5), 45, 30));

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
            "Phantom",
            1,
            GameMode.INTERCEPTION
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(-60.5, 78.5, -94.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-31.5, 66.5, 84.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(18.5, 76.5, 0.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(33.5, 66.5, -84.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(61.5, 78.5, 95.5), 45, 30));

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
            "Neolithic",
            1,
            GameMode.INTERCEPTION
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(58.5, 62.5, 63.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(14.5, 54.5, -11.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(72.5, 46.5, -90.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-59.5, 55.5, -88.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-114.5, 81.5, 115.5), 45, 30));

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
            "SunAndMoon",
            1,
            GameMode.INTERCEPTION
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(115.5, 89.5, 0.5, 90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-114.5, 89.5, 0.5, -90, 0), Team.RED).asOption());


            options.add(new PowerupOption(loc.addXYZ(0.5, 76.5, 31.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(47.5, 65.5, 78.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-7.5, 73.5, 105.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-50.5, 67.5, -84.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(8.5, 73.5, -104.5), 45, 30));


            options.add(new InterceptionPointOption("Eclipse", loc.addXYZ(0.5, 85, 0.5)));
            options.add(new InterceptionPointOption("Glacier", loc.addXYZ(23.5, 62, 70.5)));
            options.add(new InterceptionPointOption("Sun", loc.addXYZ(-18.5, 67, 134.5)));
            options.add(new InterceptionPointOption("Moon", loc.addXYZ(19.5, 67, -133.5)));
            options.add(new InterceptionPointOption("Valley", loc.addXYZ(-22.5, 62, -69.5)));

            options.add(new AbstractScoreOnEventOption.OnInterceptionCapture(25));
            options.add(new AbstractScoreOnEventOption.OnInterceptionTimer(1));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(115.5, 89.5, 0.5, 90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-114.5, 89.5, 0.5, -90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(-100, 89, 4), loc.addXYZ(-100, 93, -3)));
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
            "DeathValley",
            1,
            GameMode.INTERCEPTION
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(-41.5, 19.5, 4.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(65.5, 7.5, 56.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-40.5, 4.5, 67.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-179.5, 14.5, 59.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-67.5, 4.5, -121.5), 45, 30));

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
    HEAVEN_WILL(
            "Heaven's Will",
            2,
            2,
            60 * SECOND,
            "Heaven",
            1,
            GameMode.DUEL
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(726.5, 9, 176.5).yaw(-140), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(756.5, 8, 143.5).yaw(40), Team.RED).asOption());

            options.add(new GateOption(loc, 723, 6, 173, 729, 13, 179));
            options.add(new GateOption(loc, 753, 6, 140, 759, 13, 146));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(726.5, 9, 176.5).yaw(-140), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(756.5, 8, 143.5).yaw(40), Team.RED));

            options.add(new WinByPointsOption(5));
            options.add(new MercyWinOption(3, 5));
            options.add(new WinAfterTimeoutOption(300));
            options.add(new AbstractScoreOnEventOption.OnKill(1));
            options.add(new RespawnWaveOption(1, 2, 3));
            options.add(new GraveOption());
            options.add(new DuelsTeleportOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            return options;
        }

    },
    ILLUSION_RIFT(
            "Illusion Rift",
            4,
            1,
            60 * SECOND,
            "IllusionRift",
            10,
            GameMode.WAVE_DEFENSE
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);
            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(7.5, 22, 0.5), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(7.5, 22, 0.5), Team.RED).asOption());

            options.add(SpawnpointOption.forTeam(loc.addXYZ(7.5, 22, 0.5), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-9.5, 22, 0.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(7.5, 22, 0.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-17.5, 22, -4.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(6.5, 22, -7.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(8.5, 22, 6.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-6.5, 22, -6.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(0.5, 22, 0.5), Team.RED));

            options.add(new PowerupOption(loc.addXYZ(16.5, 24.5, 17.5), PowerupType.COOLDOWN, 30, 180, 30));
            options.add(new PowerupOption(loc.addXYZ(-15.5, 24.5, -18.5), PowerupType.HEALING, 5, 90, 30));

            options.add(new RespawnWaveOption(1, 20, 30));
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            options.add(new CurrencyOnEventOption(500));
            options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                    .add(1, new SimpleWave(12, 10 * SECOND, null)
                            //basic
                            .add(0.9, Mobs.BASIC_ZOMBIE)
                            .add(0.08, Mobs.BASIC_SKELETON)
                            .add(0.1, Mobs.BASIC_PIG_ZOMBIE)
                            .add(0.1, Mobs.BASIC_SLIME)
                            .add(0.05, Mobs.SPIDER)
                            .add(0.01, Mobs.WOLF)
                            //elite
                            .add(0.01, Mobs.ELITE_ZOMBIE)
                            .add(0.01, Mobs.ELITE_SKELETON)
                            .add(0, Mobs.ELITE_PIG_ZOMBIE)
                            .add(0, Mobs.MAGMA_CUBE)
                            .add(0.01, Mobs.IRON_GOLEM)
                            .add(0, Mobs.BLAZE)
                            //envoy
                            .add(0, Mobs.ENVOY_ZOMBIE)
                            .add(0, Mobs.ENVOY_SKELETON)
                            .add(0, Mobs.ENVOY_PIG_ZOMBIE)
                            //void
                            .add(0, Mobs.VOID_ZOMBIE)
                    )
                    .add(5, new SimpleWave(1, 10 * SECOND, "Boss", MobTier.BOSS)
                            .add(Mobs.BOLTARO)
                    )
                    .add(6, new SimpleWave(15, 10 * SECOND, null)
                            //basic
                            .add(0.8, Mobs.BASIC_ZOMBIE)
                            .add(0.1, Mobs.BASIC_SKELETON)
                            .add(0.1, Mobs.BASIC_PIG_ZOMBIE)
                            .add(0.1, Mobs.BASIC_SLIME)
                            .add(0.05, Mobs.SPIDER)
                            .add(0.01, Mobs.WOLF)
                            //elite
                            .add(0.15, Mobs.ELITE_ZOMBIE)
                            .add(0.01, Mobs.ELITE_SKELETON)
                            .add(0.05, Mobs.ELITE_PIG_ZOMBIE)
                            .add(0, Mobs.MAGMA_CUBE)
                            .add(0.05, Mobs.IRON_GOLEM)
                            .add(0.005, Mobs.BLAZE)
                            //envoy
                            .add(0, Mobs.ENVOY_ZOMBIE)
                            .add(0, Mobs.ENVOY_SKELETON)
                            .add(0, Mobs.ENVOY_PIG_ZOMBIE)
                            //void
                            .add(0, Mobs.VOID_ZOMBIE)
                    )
                    .add(10, new SimpleWave(1, 10 * SECOND, "Boss", MobTier.BOSS)
                            .add(Mobs.GHOULCALLER)
                    )
                    .add(11, new SimpleWave(18, 10 * SECOND, null)
                            //basic
                            .add(0.7, Mobs.BASIC_ZOMBIE)
                            .add(0.1, Mobs.BASIC_SKELETON)
                            .add(0.25, Mobs.BASIC_PIG_ZOMBIE)
                            .add(0.25, Mobs.BASIC_SLIME)
                            .add(0.25, Mobs.SPIDER)
                            .add(0.01, Mobs.WOLF)
                            //elite
                            .add(0.2, Mobs.ELITE_ZOMBIE)
                            .add(0.05, Mobs.ELITE_SKELETON)
                            .add(0.1, Mobs.ELITE_PIG_ZOMBIE)
                            .add(0.02, Mobs.MAGMA_CUBE)
                            .add(0.02, Mobs.IRON_GOLEM)
                            .add(0.01, Mobs.BLAZE)
                            .add(0.02, Mobs.WITCH)
                            //envoy
                            .add(0.01, Mobs.ENVOY_ZOMBIE)
                            .add(0, Mobs.ENVOY_SKELETON)
                            .add(0, Mobs.ENVOY_PIG_ZOMBIE)
                            //void
                            .add(0, Mobs.VOID_ZOMBIE)
                    )
                    .add(15, new SimpleWave(1, 10 * SECOND, "Boss", MobTier.BOSS)
                            .add(Mobs.NARMER)
                    )
                    .add(16, new SimpleWave(20, 10 * SECOND, null)
                            //basic
                            .add(0.5, Mobs.BASIC_ZOMBIE)
                            .add(0.2, Mobs.BASIC_SKELETON)
                            .add(0, Mobs.BASIC_PIG_ZOMBIE)
                            .add(0.15, Mobs.BASIC_SLIME)
                            .add(0.5, Mobs.SPIDER)
                            .add(0.05, Mobs.WOLF)
                            //elite
                            .add(0.35, Mobs.ELITE_ZOMBIE)
                            .add(0.1, Mobs.ELITE_SKELETON)
                            .add(0.1, Mobs.ELITE_PIG_ZOMBIE)
                            .add(0.02, Mobs.MAGMA_CUBE)
                            .add(0.05, Mobs.IRON_GOLEM)
                            .add(0.04, Mobs.BLAZE)
                            .add(0.04, Mobs.WITCH)
                            //envoy
                            .add(0.02, Mobs.ENVOY_ZOMBIE)
                            .add(0.02, Mobs.ENVOY_SKELETON)
                            .add(0.01, Mobs.ENVOY_PIG_ZOMBIE)
                            //void
                            .add(0.03, Mobs.VOID_ZOMBIE)
                            .add(0.01, Mobs.VOID_SKELETON)
                            .add(0.03, Mobs.EXILED_ZOMBIE_LAVA)
                    )
                    .add(20, new SimpleWave(1, 10 * SECOND, "Boss", MobTier.BOSS)
                            .add(Mobs.MITHRA)
                    )
                    .add(21, new SimpleWave(25, 10 * SECOND, null)
                            //basic
                            .add(0.4, Mobs.BASIC_ZOMBIE)
                            .add(0, Mobs.BASIC_SKELETON)
                            .add(0, Mobs.BASIC_PIG_ZOMBIE)
                            .add(0.2, Mobs.BASIC_SLIME)
                            .add(0.2, Mobs.SPIDER)
                            .add(0.1, Mobs.WOLF)
                            //elite
                            .add(0.5, Mobs.ELITE_ZOMBIE)
                            .add(0.1, Mobs.ELITE_SKELETON)
                            .add(0.2, Mobs.ELITE_PIG_ZOMBIE)
                            .add(0.02, Mobs.MAGMA_CUBE)
                            .add(0.15, Mobs.IRON_GOLEM)
                            .add(0.06, Mobs.BLAZE)
                            .add(0.06, Mobs.WITCH)
                            //envoy
                            .add(0.05, Mobs.ENVOY_ZOMBIE)
                            .add(0.05, Mobs.ENVOY_SKELETON)
                            .add(0.01, Mobs.ENVOY_PIG_ZOMBIE)
                            //elite
                            .add(0.04, Mobs.VOID_ZOMBIE)
                            .add(0.04, Mobs.VOID_SKELETON)
                            .add(0.04, Mobs.EXILED_ZOMBIE_LAVA)
                            .add(0.02, Mobs.EXILED_ZOMBIE_RIFT)
                    )
                    .add(25, new SimpleWave(1, 10 * SECOND, "Boss", MobTier.BOSS)
                            .add(Mobs.ZENITH)
                    ),
                    DifficultyIndex.NORMAL
            ));

            return options;
        }

    },
    ILLUSION_APERTURE(
            "Illusion Aperture",
            4,
            1,
            60 * SECOND,
            "IllusionAperture",
            1,
            GameMode.WAVE_DEFENSE
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);
            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(584.5, 18, 235.5), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(584.5, 18, 235.5), Team.RED).asOption());

            options.add(SpawnpointOption.forTeam(loc.addXYZ(614.5, 18, 227.5), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(617.5, 18, 240.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(608, 17, 250.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(595.5, 17, 255.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(584.5, 18, 235.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(593.5, 19, 242.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(606.5, 19, 232.5), Team.RED));

            options.add(new RespawnWaveOption(0, 30, 30));
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            options.add(new CurrencyOnEventOption(250));
            options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                    .add(1, new SimpleWave(12, 5 * SECOND, null)
                            //basic
                            .add(0.9, Mobs.BASIC_ZOMBIE)
                            .add(0.08, Mobs.BASIC_SKELETON)
                            .add(0.1, Mobs.BASIC_PIG_ZOMBIE)
                            .add(0.1, Mobs.BASIC_SLIME)
                            .add(0.05, Mobs.SPIDER)
                            .add(0.01, Mobs.WOLF)
                            //elite
                            .add(0.01, Mobs.ELITE_ZOMBIE)
                            .add(0.01, Mobs.ELITE_SKELETON)
                            .add(0, Mobs.ELITE_PIG_ZOMBIE)
                            .add(0, Mobs.MAGMA_CUBE)
                            .add(0.01, Mobs.IRON_GOLEM)
                            .add(0, Mobs.BLAZE)
                            //envoy
                            .add(0, Mobs.ENVOY_ZOMBIE)
                            .add(0, Mobs.ENVOY_SKELETON)
                            .add(0, Mobs.ENVOY_PIG_ZOMBIE)
                            //void
                            .add(0, Mobs.VOID_ZOMBIE)
                    )
                    .add(5, new SimpleWave(1, 10 * SECOND, "Boss", MobTier.BOSS)
                            .add(Mobs.BOLTARO)
                    )
                    .add(6, new SimpleWave(15, 10 * SECOND, null)
                            //basic
                            .add(0.8, Mobs.BASIC_ZOMBIE)
                            .add(0.1, Mobs.BASIC_SKELETON)
                            .add(0.1, Mobs.BASIC_PIG_ZOMBIE)
                            .add(0.1, Mobs.BASIC_SLIME)
                            .add(0.05, Mobs.SPIDER)
                            .add(0.01, Mobs.WOLF)
                            //elite
                            .add(0.15, Mobs.ELITE_ZOMBIE)
                            .add(0.01, Mobs.ELITE_SKELETON)
                            .add(0.05, Mobs.ELITE_PIG_ZOMBIE)
                            .add(0, Mobs.MAGMA_CUBE)
                            .add(0.05, Mobs.IRON_GOLEM)
                            .add(0, Mobs.BLAZE)
                            //envoy
                            .add(0, Mobs.ENVOY_ZOMBIE)
                            .add(0, Mobs.ENVOY_SKELETON)
                            .add(0, Mobs.ENVOY_PIG_ZOMBIE)
                            //void
                            .add(0, Mobs.VOID_ZOMBIE)
                    )
                    .add(10, new SimpleWave(1, 10 * SECOND, "Boss", MobTier.BOSS)
                            .add(Mobs.GHOULCALLER)
                    )
                    .add(11, new SimpleWave(18, 10 * SECOND, null)
                            //basic
                            .add(0.7, Mobs.BASIC_ZOMBIE)
                            .add(0.1, Mobs.BASIC_SKELETON)
                            .add(0.25, Mobs.BASIC_PIG_ZOMBIE)
                            .add(0.25, Mobs.BASIC_SLIME)
                            .add(0.25, Mobs.SPIDER)
                            .add(0.01, Mobs.WOLF)
                            //elite
                            .add(0.2, Mobs.ELITE_ZOMBIE)
                            .add(0.05, Mobs.ELITE_SKELETON)
                            .add(0.1, Mobs.ELITE_PIG_ZOMBIE)
                            .add(0.02, Mobs.MAGMA_CUBE)
                            .add(0.02, Mobs.IRON_GOLEM)
                            .add(0.01, Mobs.BLAZE)
                            .add(0.01, Mobs.WITCH)
                            //envoy
                            .add(0.01, Mobs.ENVOY_ZOMBIE)
                            .add(0, Mobs.ENVOY_SKELETON)
                            .add(0, Mobs.ENVOY_PIG_ZOMBIE)
                            //void
                            .add(0, Mobs.VOID_ZOMBIE)
                    )
                    .add(15, new SimpleWave(1, 10 * SECOND, "Boss", MobTier.BOSS)
                            .add(Mobs.NARMER)
                    )
                    .add(16, new SimpleWave(20, 10 * SECOND, null)
                            //basic
                            .add(0.6, Mobs.BASIC_ZOMBIE)
                            .add(0.2, Mobs.BASIC_SKELETON)
                            .add(0, Mobs.BASIC_PIG_ZOMBIE)
                            .add(0.15, Mobs.BASIC_SLIME)
                            .add(0.5, Mobs.SPIDER)
                            .add(0, Mobs.WOLF)
                            //elite
                            .add(0.3, Mobs.ELITE_ZOMBIE)
                            .add(0.1, Mobs.ELITE_SKELETON)
                            .add(0.1, Mobs.ELITE_PIG_ZOMBIE)
                            .add(0.02, Mobs.MAGMA_CUBE)
                            .add(0.05, Mobs.IRON_GOLEM)
                            .add(0.01, Mobs.BLAZE)
                            .add(0.01, Mobs.WITCH)
                            //envoy
                            .add(0.02, Mobs.ENVOY_ZOMBIE)
                            .add(0.02, Mobs.ENVOY_SKELETON)
                            .add(0.01, Mobs.ENVOY_PIG_ZOMBIE)
                            //void
                            .add(0.03, Mobs.VOID_ZOMBIE)
                    )
                    .add(20, new SimpleWave(2, 10 * SECOND, "Boss", MobTier.BOSS)
                            .add(Mobs.PHYSIRA)
                            .add(Mobs.MITHRA)
                    )
                    .add(21, new SimpleWave(25, 10 * SECOND, null)
                            //basic
                            .add(0.5, Mobs.BASIC_ZOMBIE)
                            .add(0, Mobs.BASIC_SKELETON)
                            .add(0, Mobs.BASIC_PIG_ZOMBIE)
                            .add(0.2, Mobs.BASIC_SLIME)
                            .add(0.2, Mobs.SPIDER)
                            .add(0.1, Mobs.WOLF)
                            //elite
                            .add(0.4, Mobs.ELITE_ZOMBIE)
                            .add(0.1, Mobs.ELITE_SKELETON)
                            .add(0.2, Mobs.ELITE_PIG_ZOMBIE)
                            .add(0.02, Mobs.MAGMA_CUBE)
                            .add(0.15, Mobs.IRON_GOLEM)
                            .add(0.02, Mobs.BLAZE)
                            .add(0.02, Mobs.WITCH)
                            //envoy
                            .add(0.05, Mobs.ENVOY_ZOMBIE)
                            .add(0.05, Mobs.ENVOY_SKELETON)
                            .add(0.01, Mobs.ENVOY_PIG_ZOMBIE)
                            //elite
                            .add(0.04, Mobs.VOID_ZOMBIE)
                    )
                    .add(25, new SimpleWave(1, 10 * SECOND, "Boss", MobTier.BOSS)
                            .add(Mobs.ZENITH)
                    ),
                    DifficultyIndex.HARD
            ));

            return options;
        }

    },
    ILLUSION_CROSSFIRE(
            "Illusion Crossfire",
            4,
            1,
            60 * SECOND,
            "IllusionCrossfire",
            1,
            GameMode.WAVE_DEFENSE
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);
            options.add(LobbyLocationMarker.create(loc.addXYZ(111.5, 9, 65.5), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(111.5, 9, 65.5), Team.RED).asOption());

            options.add(SpawnpointOption.forTeam(loc.addXYZ(111.5, 9, 65.5), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(126.5, 8, 74.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(96.5, 9, 50.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(90, 5, 75), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(135.5, 5, 50.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(125.5, 9, 60.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(101.5, 9, 67.5), Team.RED));

            options.add(new RespawnWaveOption());
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            options.add(new CurrencyOnEventOption(500));
            options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                    .add(1, new SimpleWave(15, 5 * SECOND, null)
                            //basic
                            .add(0.85, Mobs.BASIC_ZOMBIE)
                            .add(0.08, Mobs.BASIC_SKELETON)
                            .add(0.1, Mobs.BASIC_PIG_ZOMBIE)
                            .add(0.1, Mobs.BASIC_SLIME)
                            .add(0.05, Mobs.SPIDER)
                            .add(0.01, Mobs.WOLF)
                            //elite
                            .add(0.1, Mobs.ELITE_ZOMBIE)
                            .add(0, Mobs.ELITE_SKELETON)
                            .add(0, Mobs.ELITE_PIG_ZOMBIE)
                            .add(0, Mobs.MAGMA_CUBE)
                            .add(0.01, Mobs.IRON_GOLEM)
                            .add(0.01, Mobs.BLAZE)
                            .add(0.01, Mobs.WITCH)
                            //envoy
                            .add(0, Mobs.ENVOY_ZOMBIE)
                            .add(0, Mobs.ENVOY_SKELETON)
                            .add(0, Mobs.ENVOY_PIG_ZOMBIE)
                            //void
                            .add(0, Mobs.VOID_ZOMBIE)
                    )
                    .add(20, new SimpleWave(1, 10 * SECOND, "Boss", MobTier.BOSS)
                            .add(Mobs.BOLTARO)
                    )
                    .add(21, new SimpleWave(20, 10 * SECOND, null)
                            //basic
                            .add(0.8, Mobs.BASIC_ZOMBIE)
                            .add(0.1, Mobs.BASIC_SKELETON)
                            .add(0.1, Mobs.BASIC_PIG_ZOMBIE)
                            .add(0.1, Mobs.BASIC_SLIME)
                            .add(0.05, Mobs.SPIDER)
                            .add(0.01, Mobs.WOLF)
                            //elite
                            .add(0.3, Mobs.ELITE_ZOMBIE)
                            .add(0.01, Mobs.ELITE_SKELETON)
                            .add(0.05, Mobs.ELITE_PIG_ZOMBIE)
                            .add(0, Mobs.MAGMA_CUBE)
                            .add(0, Mobs.IRON_GOLEM)
                            .add(0.02, Mobs.BLAZE)
                            .add(0.02, Mobs.WITCH)
                            //envoy
                            .add(0, Mobs.ENVOY_ZOMBIE)
                            .add(0, Mobs.ENVOY_SKELETON)
                            .add(0, Mobs.ENVOY_PIG_ZOMBIE)
                            //void
                            .add(0, Mobs.VOID_ZOMBIE)
                    )
                    .add(40, new SimpleWave(1, 10 * SECOND, "Boss", MobTier.BOSS)
                            .add(Mobs.GHOULCALLER)
                    )
                    .add(41, new SimpleWave(25, 10 * SECOND, null)
                            //basic
                            .add(0.7, Mobs.BASIC_ZOMBIE)
                            .add(0.1, Mobs.BASIC_SKELETON)
                            .add(0.25, Mobs.BASIC_PIG_ZOMBIE)
                            .add(0.25, Mobs.BASIC_SLIME)
                            .add(0.25, Mobs.SPIDER)
                            .add(0.01, Mobs.WOLF)
                            //elite
                            .add(0.15, Mobs.ELITE_ZOMBIE)
                            .add(0.05, Mobs.ELITE_SKELETON)
                            .add(0.1, Mobs.ELITE_PIG_ZOMBIE)
                            .add(0.02, Mobs.MAGMA_CUBE)
                            .add(0.02, Mobs.IRON_GOLEM)
                            .add(0.01, Mobs.BLAZE)
                            .add(0.01, Mobs.WITCH)
                            //envoy
                            .add(0.01, Mobs.ENVOY_ZOMBIE)
                            .add(0, Mobs.ENVOY_SKELETON)
                            .add(0, Mobs.ENVOY_PIG_ZOMBIE)
                            //void
                            .add(0, Mobs.VOID_ZOMBIE)
                    )
                    .add(60, new SimpleWave(1, 10 * SECOND, "Boss", MobTier.BOSS)
                            .add(Mobs.NARMER)
                    )
                    .add(61, new SimpleWave(28, 10 * SECOND, null)
                            //basic
                            .add(0.5, Mobs.BASIC_ZOMBIE)
                            .add(0.2, Mobs.BASIC_SKELETON)
                            .add(0, Mobs.BASIC_PIG_ZOMBIE)
                            .add(0.15, Mobs.BASIC_SLIME)
                            .add(0.5, Mobs.SPIDER)
                            .add(0, Mobs.WOLF)
                            //elite
                            .add(0.35, Mobs.ELITE_ZOMBIE)
                            .add(0.1, Mobs.ELITE_SKELETON)
                            .add(0.1, Mobs.ELITE_PIG_ZOMBIE)
                            .add(0.02, Mobs.MAGMA_CUBE)
                            .add(0.05, Mobs.IRON_GOLEM)
                            .add(0.01, Mobs.BLAZE)
                            .add(0.01, Mobs.WITCH)
                            //envoy
                            .add(0.02, Mobs.ENVOY_ZOMBIE)
                            .add(0.02, Mobs.ENVOY_SKELETON)
                            .add(0.01, Mobs.ENVOY_PIG_ZOMBIE)
                            //void
                            .add(0.01, Mobs.VOID_ZOMBIE)
                            .add(0.02, Mobs.EXILED_ZOMBIE_LAVA)
                            .add(0.01, Mobs.EXILED_ZOMBIE_RIFT)
                    )
                    .add(80, new SimpleWave(2, 10 * SECOND, "Boss", MobTier.BOSS)
                            .add(Mobs.MITHRA)
                    )
                    .add(81, new SimpleWave(30, 10 * SECOND, null)
                            //basic
                            .add(0.5, Mobs.BASIC_ZOMBIE)
                            .add(0, Mobs.BASIC_SKELETON)
                            .add(0, Mobs.BASIC_PIG_ZOMBIE)
                            .add(0.2, Mobs.BASIC_SLIME)
                            .add(0.2, Mobs.SPIDER)
                            .add(0.1, Mobs.WOLF)
                            //elite
                            .add(0.4, Mobs.ELITE_ZOMBIE)
                            .add(0.1, Mobs.ELITE_SKELETON)
                            .add(0.2, Mobs.ELITE_PIG_ZOMBIE)
                            .add(0.02, Mobs.MAGMA_CUBE)
                            .add(0.15, Mobs.IRON_GOLEM)
                            .add(0.02, Mobs.BLAZE)
                            .add(0.02, Mobs.WITCH)
                            //envoy
                            .add(0.05, Mobs.ENVOY_ZOMBIE)
                            .add(0.05, Mobs.ENVOY_SKELETON)
                            .add(0.01, Mobs.ENVOY_PIG_ZOMBIE)
                            //elite
                            .add(0.04, Mobs.VOID_ZOMBIE)
                            .add(0.06, Mobs.EXILED_ZOMBIE_LAVA)
                            .add(0.04, Mobs.EXILED_ZOMBIE_RIFT)
                    )
                    .add(100, new SimpleWave(1, 10 * SECOND, "Boss", MobTier.BOSS)
                            .add(Mobs.ZENITH)
                    )
                    .add(101, new SimpleWave(36, 10 * SECOND, null)
                            //basic
                            .add(0.2, Mobs.BASIC_ZOMBIE)
                            .add(0, Mobs.BASIC_SKELETON)
                            .add(0.1, Mobs.BASIC_PIG_ZOMBIE)
                            .add(0.2, Mobs.BASIC_SLIME)
                            .add(0.3, Mobs.SPIDER)
                            .add(0.15, Mobs.WOLF)
                            //elite
                            .add(0.7, Mobs.ELITE_ZOMBIE)
                            .add(0.2, Mobs.ELITE_SKELETON)
                            .add(0.2, Mobs.ELITE_PIG_ZOMBIE)
                            .add(0.1, Mobs.MAGMA_CUBE)
                            .add(0.2, Mobs.IRON_GOLEM)
                            .add(0.02, Mobs.BLAZE)
                            .add(0.03, Mobs.WITCH)
                            //envoy
                            .add(0.07, Mobs.ENVOY_ZOMBIE)
                            .add(0.05, Mobs.ENVOY_SKELETON)
                            .add(0.01, Mobs.ENVOY_PIG_ZOMBIE)
                            //elite
                            .add(0.06, Mobs.VOID_ZOMBIE)
                            .add(0.06, Mobs.EXILED_ZOMBIE_LAVA)
                            .add(0.06, Mobs.EXILED_ZOMBIE_RIFT)
                    )
                    .add(120, new SimpleWave(40, 10 * SECOND, null)
                            //basic
                            .add(0.2, Mobs.BASIC_ZOMBIE)
                            .add(0, Mobs.BASIC_SKELETON)
                            .add(0.1, Mobs.BASIC_PIG_ZOMBIE)
                            .add(0.2, Mobs.BASIC_SLIME)
                            .add(0.3, Mobs.SPIDER)
                            .add(0.15, Mobs.WOLF)
                            //elite
                            .add(0.7, Mobs.ELITE_ZOMBIE)
                            .add(0.3, Mobs.ELITE_SKELETON)
                            .add(0.2, Mobs.ELITE_PIG_ZOMBIE)
                            .add(0.1, Mobs.MAGMA_CUBE)
                            .add(0.2, Mobs.IRON_GOLEM)
                            .add(0.1, Mobs.BLAZE)
                            .add(0.1, Mobs.WITCH)
                            //envoy
                            .add(0.09, Mobs.ENVOY_ZOMBIE)
                            .add(0.05, Mobs.ENVOY_SKELETON)
                            .add(0.02, Mobs.ENVOY_PIG_ZOMBIE)
                            //elite
                            .add(0.08, Mobs.VOID_ZOMBIE)
                            .add(0.08, Mobs.EXILED_ZOMBIE_LAVA)
                            .add(0.08, Mobs.EXILED_ZOMBIE_RIFT)
                    )
                    .add(140, new SimpleWave(45, 10 * SECOND, null)
                            //basic
                            .add(0.1, Mobs.BASIC_ZOMBIE)
                            .add(0, Mobs.BASIC_SKELETON)
                            .add(0.1, Mobs.BASIC_PIG_ZOMBIE)
                            .add(0.3, Mobs.BASIC_SLIME)
                            .add(0.3, Mobs.SPIDER)
                            .add(0.15, Mobs.WOLF)
                            //elite
                            .add(0.8, Mobs.ELITE_ZOMBIE)
                            .add(0.3, Mobs.ELITE_SKELETON)
                            .add(0.2, Mobs.ELITE_PIG_ZOMBIE)
                            .add(0.12, Mobs.MAGMA_CUBE)
                            .add(0.2, Mobs.IRON_GOLEM)
                            .add(0.15, Mobs.BLAZE)
                            .add(0.15, Mobs.WITCH)
                            //envoy
                            .add(0.1, Mobs.ENVOY_ZOMBIE)
                            .add(0.05, Mobs.ENVOY_SKELETON)
                            .add(0.03, Mobs.ENVOY_PIG_ZOMBIE)
                            //elite
                            .add(0.12, Mobs.VOID_ZOMBIE)
                            .add(0.12, Mobs.EXILED_ZOMBIE_LAVA)
                            .add(0.12, Mobs.EXILED_ZOMBIE_RIFT)
                    ),
                    DifficultyIndex.ENDLESS
            ));

            return options;
        }
    },
    /*FROZEN_DIVIDE(
            "Frozen Divide",
            4,
            1,
            60 * SECOND,
            "FrozenDivide",
            0,
            GameMode.WAVE_DEFENSE
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);
            options.add(LobbyLocationMarker.create(loc.addXYZ(111.5, 9, 65.5), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(111.5, 9, 65.5), Team.RED).asOption());

            options.add(SpawnpointOption.forTeam(loc.addXYZ(111.5, 9, 65.5), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(126.5, 8, 74.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(96.5, 9, 50.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(90, 5, 75), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(135.5, 5, 50.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(125.5, 9, 60.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(101.5, 9, 67.5), Team.RED));

            options.add(new RespawnWaveOption());
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            options.add(new CurrencyOnEventOption(250));
            options.add(new WaveDefenseOption(Team.RED, new StaticWaveList(), DifficultyIndex.NORMAL)); //TODO difficulty

            return options;
        }
    },*/
    DEBUG(
            "Practice",
            300,
            1,
            60 * SECOND,
            "WLDebug",
            3,
            GameMode.DEBUG
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(727.5, 8.5, 200.5), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(727.5, 8.5, 196.5), Team.RED).asOption());

            options.add(new DummySpawnOption(loc.addXYZ(720.5, 7, 206.5), Team.RED));
            options.add(new DummySpawnOption(loc.addXYZ(703.5, 7, 206.5), Team.BLUE));

            options.add(new PowerupOption(loc.addXYZ(713.5, 8.5, 209.5), PowerupType.SELF_DAMAGE, 0, 5, 5));
            options.add(new PowerupOption(loc.addXYZ(710.5, 8.5, 209.5), PowerupType.SELF_HEAL, 0, 5, 5));

            options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 188.5), PowerupType.DAMAGE, 30, 5, 5));
            options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 192.5), PowerupType.ENERGY, 30, 5, 5));
            options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 196.5), PowerupType.SPEED, 10, 5, 5));
            options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 200.5), PowerupType.HEALING, 10, 5, 5));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(727.5, 8.5, 196.5), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(727.5, 8.5, 196.5), Team.RED));

            options.add(new FlagCapturePointOption(loc.addXYZ(703.5, 8.5, 212.5), Team.BLUE));
            options.add(new FlagSpawnPointOption(loc.addXYZ(703.5, 8.5, 212.5), Team.BLUE));

            options.add(new FlagCapturePointOption(loc.addXYZ(720.5, 8.5, 212.5), Team.RED));
            options.add(new FlagSpawnPointOption(loc.addXYZ(720.5, 8.5, 212.5), Team.RED));

            options.add(new GateOption(loc.addXYZ(713, 7, 195), loc.addXYZ(713, 10, 198)));

            options.add(new WinByPointsOption(3000));
            options.add(new MercyWinOption(3000, 5));
            if (addons.contains(GameAddon.DOUBLE_TIME)) {
                options.add(new WinAfterTimeoutOption(3600));
            } else {
                options.add(new WinAfterTimeoutOption(1800));
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
    TUTORIAL_MAP(
            "Tutorial",
            1,
            1,
            10 * SECOND,
            "Tutorial",
            1,
            GameMode.TUTORIAL
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(726.5, 9, 176.5).yaw(-140), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(756.5, 8, 143.5).yaw(40), Team.RED).asOption());

            options.add(SpawnpointOption.forTeam(loc.addXYZ(726.5, 9, 176.5).yaw(-140), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(756.5, 8, 143.5).yaw(40), Team.RED));

            options.add(new AbstractScoreOnEventOption.OnKill(5));
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            return options;
        }

    },

    ;

    public static final GameMap[] VALUES = values();

    public static GameMap getGameMap(String mapName) {
        for (GameMap value : GameMap.VALUES) {
            if (value.mapName.equalsIgnoreCase(mapName)) {
                return value;
            }
        }
        return null;
    }

    /**
     * <p>Each map instance presents a game server, every map can hold up to 3 games at once.</p>
     * <p>Adding a new map must start with -0 at the end and increment from there on out.</p>
     * <p>Adding additional game servers will require a config update in @see MultiWorld Plugin</p>
     *
     * @param gameManager The game manager to add gameholders to.
     */
    public static void addGameHolders(GameManager gameManager) {
        for (GameMap map : VALUES) {
            for (int i = 0; i < map.numberOfMaps; i++) {
                gameManager.addGameHolder(map.fileName + "-" + i, map);
            }
        }
    }

    private final String mapName;
    private final int maxPlayers;
    private final int minPlayers;
    private final int lobbyCountdown;
    private final String fileName;
    private final int numberOfMaps;
    private final List<GameMode> gameMode;

    GameMap(
            @Nonnull String mapName,
            int maxPlayers,
            int minPlayers,
            int lobbyCountdown,
            String fileName,
            int numberOfMaps,
            @Nonnull GameMode... gameMode
    ) {
        this.mapName = mapName;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.lobbyCountdown = lobbyCountdown;
        this.fileName = fileName;
        this.numberOfMaps = numberOfMaps;
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

    public List<GameMode> getGameModes() {
        return gameMode;
    }
}
