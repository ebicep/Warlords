package com.ebicep.warlords.game;

import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.*;
import com.ebicep.warlords.game.option.PowerupOption.PowerUp;
import com.ebicep.warlords.game.option.cuboid.AbstractCuboidOption;
import com.ebicep.warlords.game.option.cuboid.BoundingBoxOption;
import com.ebicep.warlords.game.option.cuboid.GateOption;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.option.payload.PayloadOption;
import com.ebicep.warlords.game.option.payload.PayloadSpawns;
import com.ebicep.warlords.game.option.pve.CurrencyOnEventOption;
import com.ebicep.warlords.game.option.pve.ItemOption;
import com.ebicep.warlords.game.option.pve.ReadyUpOption;
import com.ebicep.warlords.game.option.pve.onslaught.OnslaughtOption;
import com.ebicep.warlords.game.option.pve.rewards.CoinGainOption;
import com.ebicep.warlords.game.option.pve.treasurehunt.DungeonRoomMarker;
import com.ebicep.warlords.game.option.pve.treasurehunt.RoomType;
import com.ebicep.warlords.game.option.pve.treasurehunt.TreasureHuntOption;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.pve.wavedefense.WinByMaxWaveClearOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.EventPointsOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.SafeZoneOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffectOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.*;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.FixedWave;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.SimpleWave;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.StaticWaveList;
import com.ebicep.warlords.game.option.pvp.DuelsTeleportOption;
import com.ebicep.warlords.game.option.pvp.FlagCapturePointOption;
import com.ebicep.warlords.game.option.pvp.FlagSpawnPointOption;
import com.ebicep.warlords.game.option.pvp.GameOvertimeOption;
import com.ebicep.warlords.game.option.pvp.interception.InterceptionPointOption;
import com.ebicep.warlords.game.option.pvp.siege.SiegeOption;
import com.ebicep.warlords.game.option.raid.RaidOption;
import com.ebicep.warlords.game.option.respawn.RespawnProtectionOption;
import com.ebicep.warlords.game.option.respawn.RespawnWaveOption;
import com.ebicep.warlords.game.option.win.MercyWinOption;
import com.ebicep.warlords.game.option.win.WinAfterTimeoutOption;
import com.ebicep.warlords.game.option.win.WinByAllDeathOption;
import com.ebicep.warlords.game.option.win.WinByPointsOption;
import com.ebicep.warlords.game.state.PreLobbyState;
import com.ebicep.warlords.game.state.State;
import com.ebicep.warlords.game.state.SyncTimerState;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import com.ebicep.warlords.util.java.Pair;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public enum GameMap {

    MAIN_LOBBY("MainLobby",
            32,
            1,
            0,
            "MainLobby",
            1,
            GameMode.LOBBY
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-60.5, 60, 83, 113, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-60.5, 60, 83, 113, 0), Team.RED).asOption());

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-60.5, 60, 83, 113, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-60.5, 60, 83, 113, 0), Team.RED));

//            LocationBuilder dummySpawnLocation = loc.addXYZ(-78, 60, 78); TODO
//            Collection<Location> dummyWanderLocations = Arrays.asList(
//                    loc.addXYZ(-64.5, 60, 78.5),
//                    loc.addXYZ(-73.5, 60, 80.5),
//                    loc.addXYZ(-71.5, 60, 70.5),
//                    loc.addXYZ(-83, 60, 75),
//                    loc.addXYZ(-89.5, 60, 80.5),
//                    loc.addXYZ(-84, 60, 84.5)
//            );
//            for (int i = 0; i < 4; i++) {
//                options.add(new DummySpawnOption(dummyWanderLocations, i % 2 == 0 ? Team.BLUE : Team.RED, warlordsNPC -> {
//                    NPC npc = warlordsNPC.getNpc();
//                    Waypoints waypoints = npc.getOrAddTrait(Waypoints.class);
//                    waypoints.setWaypointProvider("wander");
//                    WanderWaypointProvider provider = (WanderWaypointProvider) waypoints.getCurrentProvider();
//                    provider.addRegionCentres(dummyWanderLocations);
//                    provider.setXYRange(10, 0);
//                }));
//            }
            options.add(new DummySpawnOption(loc.addXYZ(-65.5, 60, 71.5, 42.5f, 0), Team.BLUE));
            options.add(new DummySpawnOption(loc.addXYZ(-80.5, 60, 73, -32.5f, 0), Team.RED));
            options.add(new DummySpawnOption(loc.addXYZ(-88, 60, 84, -114.5f, 0), Team.BLUE));
            options.add(new DummySpawnOption(loc.addXYZ(-72, 60, 87, 164.5f, 0), Team.RED));

            options.add(new LobbyGameOption());

            return options;
        }

        @Override
        public State initialState(Game game) {
            return new SyncTimerState(game);
        }
    },
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

            options.add(new PowerupOption(loc.addXYZ(-32.5, 25.5, 49.5), PowerUp.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(33.5, 25.5, -48.5), PowerUp.ENERGY));

            options.add(new PowerupOption(loc.addXYZ(-54.5, 36.5, 24.5), PowerUp.SPEED));
            options.add(new PowerupOption(loc.addXYZ(55.5, 36.5, -23.5), PowerUp.SPEED));

            options.add(new PowerupOption(loc.addXYZ(-0.5, 24.5, 64.5), PowerUp.HEALING));
            options.add(new PowerupOption(loc.addXYZ(1.5, 24.5, -62.5), PowerUp.HEALING));

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
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

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

            options.add(new PowerupOption(loc.addXYZ(158.5, 6.5, 28.5), PowerUp.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(65.5, 6.5, 98.5), PowerUp.ENERGY));

            options.add(new PowerupOption(loc.addXYZ(217.5, 36.5, 89.5), PowerUp.SPEED));
            options.add(new PowerupOption(loc.addXYZ(6.5, 36.5, 39.5), PowerUp.SPEED));

            options.add(new PowerupOption(loc.addXYZ(96.5, 6.5, 108.5), PowerUp.HEALING));
            options.add(new PowerupOption(loc.addXYZ(127.5, 6.5, 19.5), PowerUp.HEALING));

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
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

            return options;
        }

    },
    WARSONG(
            "Warsong Remastered",
            32,
            12,
            60 * SECOND,
            "Warsong",
            2,
            GameMode.CAPTURE_THE_FLAG
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(71.5, 40, -71.5, 90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(73.5, 41, 213.5, -90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(102.5, 21.5, 51.5), PowerUp.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(42.5, 21.5, 92.5), PowerUp.ENERGY));

            options.add(new PowerupOption(loc.addXYZ(63.5, 33.5, -31.5), PowerUp.SPEED));
            options.add(new PowerupOption(loc.addXYZ(79.5, 32.5, 167.5), PowerUp.SPEED));

            options.add(new PowerupOption(loc.addXYZ(44.5, 20.5, 42.5), PowerUp.HEALING));
            options.add(new PowerupOption(loc.addXYZ(100.5, 20.5, 101.5), PowerUp.HEALING));

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
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

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

            options.add(new PowerupOption(loc.addXYZ(5.5, 15.5, -33.5), PowerUp.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(-4.5, 15.5, 34.5), PowerUp.ENERGY));

            options.add(new PowerupOption(loc.addXYZ(4.5, 25.5, -86.5), PowerUp.SPEED));
            options.add(new PowerupOption(loc.addXYZ(-3.5, 25.5, 87.5), PowerUp.SPEED));

            options.add(new PowerupOption(loc.addXYZ(57.5, 15.5, 1.5), PowerUp.HEALING));
            options.add(new PowerupOption(loc.addXYZ(-56.5, 15.5, -0.5), PowerUp.HEALING));

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
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

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

            options.add(new PowerupOption(loc.addXYZ(608.5, 16.5, 280.5), PowerUp.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(592.5, 16.5, 194.5), PowerUp.ENERGY));

            options.add(new PowerupOption(loc.addXYZ(551.5, 31.5, 217.5), PowerUp.SPEED));
            options.add(new PowerupOption(loc.addXYZ(649.5, 31.5, 257.5), PowerUp.SPEED));

            options.add(new PowerupOption(loc.addXYZ(577.5, 22.5, 286.5), PowerUp.HEALING));
            options.add(new PowerupOption(loc.addXYZ(623.5, 22.5, 188.5), PowerUp.HEALING));

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
    GORGE_REMASTERED(
            "Gorge Remastered",
            32,
            12,
            60 * SECOND,
            "GorgeRemastered",
            1,
            GameMode.CAPTURE_THE_FLAG
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);
            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());


            options.add(LobbyLocationMarker.create(loc.addXYZ(43.5, 76, -216.5, 90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-134.5, 76, -216.5, -90, 0), Team.RED).asOption());

            options.add(SpawnpointOption.forTeam(loc.addXYZ(5.5, 71, -159.5, 135, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-97.5, 71, -274.5, -45, 0), Team.RED));

            options.add(new GateOption(loc, -125.5, 81.5, -213.5, -125.5, 76, -219.5, Material.SPRUCE_FENCE));
            options.add(new GateOption(loc, 34.5, 80, -219.5, 34.5, 76, -213.5, Material.IRON_BARS));
            options.add(new GateOption(loc, -145.5, 76, -212.5, -143.5, 80, -212.5, Material.SPRUCE_FENCE));
            options.add(new GateOption(loc, 54.5, 76, -220.5, 52.5, 80, -220.5, Material.IRON_BARS));
            options.add(new GateOption(loc, -132.5, 76, -232.5, -132.5, 81, -234.5, Material.SPRUCE_FENCE));
            options.add(new GateOption(loc, 41.5, 76, -200.5, 41.5, 81, -198.5, Material.IRON_BARS));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(5.5, 71, -159.5, 135, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-97.5, 71, -274.5, -45, 0), Team.RED));

            options.add(new GateOption(loc, -125.5, 82.5, -213.5, -125.5, 76, -219.5, Material.SPRUCE_FENCE));
            options.add(new GateOption(loc, 34.5, 81, -219.5, 34.5, 76, -213.5, Material.IRON_BARS));

            options.add(new PowerupOption(loc.addXYZ(-2.5, 61.5, -236.5), PowerUp.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(-88.5, 61.5, -196.5), PowerUp.ENERGY));

            options.add(new PowerupOption(loc.addXYZ(-152.5, 75.5, -208.5), PowerUp.SPEED));
            options.add(new PowerupOption(loc.addXYZ(60.5, 75.5, -224.5), PowerUp.SPEED));
            options.add(new PowerupOption(loc.addXYZ(-152.5, 76.5, -232.5), PowerUp.SPEED));
            options.add(new PowerupOption(loc.addXYZ(62.5, 76, -200.5), PowerUp.SPEED));

            options.add(new PowerupOption(loc.addXYZ(-12.5, 45.5, -194.5), PowerUp.HEALING));
            options.add(new PowerupOption(loc.addXYZ(-78.5, 45.5, -238.5), PowerUp.HEALING));

            options.add(new FlagCapturePointOption(loc.addXYZ(50.5, 76.5, -199.5, 180, 0), Team.BLUE));
            options.add(new FlagSpawnPointOption(loc.addXYZ(50.5, 76.5, -199.5, 180, 0), Team.BLUE));

            options.add(new FlagCapturePointOption(loc.addXYZ(99.5, 45.5, 17.5, 90, 0), Team.RED));
            options.add(new FlagSpawnPointOption(loc.addXYZ(99.5, 45.5, 17.5, 90, 0), Team.RED));

            options.add(new AbstractScoreOnEventOption.FlagCapture(250));
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
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

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

            options.add(new PowerupOption(loc.addXYZ(72.5, 65.5, -60.5), PowerUp.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(39.5, 64.5, 58.5), PowerUp.SPEED, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(66.5, 63.5, 0.5), PowerUp.SPEED, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(145.5, 80.5, 0.5), PowerUp.SPEED, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-20.5, 65.5, 0.5), PowerUp.SPEED, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-54.5, 83.5, 63.5), PowerUp.SPEED, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-25.5, 63.5, -35.5), PowerUp.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-25.5, 64.5, 32.5), PowerUp.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(100.5, 79.5, 11.5), PowerUp.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-54.5, 83.5, -62.5), PowerUp.SPEED, 45, 30));

            options.add(new PowerupOption(loc.addXYZ(23.5, 62.5, 45.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(23.5, 65.5, -50.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(23.5, 62.5, 7.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(23.5, 62.5, -23.5), 45, 45));

            options.add(new PowerupOption(loc.addXYZ(-14.5, 84.5, -14.0), PowerUp.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(-14.5, 84.5, 15.5), PowerUp.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(83.5, 65.5, 64.5), PowerUp.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(100.5, 79.5, -10.5), PowerUp.HEALING, 45, 60));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(83, 84, 4), loc.addXYZ(83, 79, -4)));
            options.add(new GateOption(loc.addXYZ(-70, 83, 3), loc.addXYZ(-70, 86, -2), Material.IRON_BARS));

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
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

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

            options.add(new PowerupOption(loc.addXYZ(78.5, 47.5, 18.5), PowerUp.SPEED, 45, 0));
            options.add(new PowerupOption(loc.addXYZ(78.5, 47.5, -17.5), PowerUp.SPEED, 45, 0));
            options.add(new PowerupOption(loc.addXYZ(0.5, 32.5, 0.5), PowerUp.SPEED, 45, 0));
            options.add(new PowerupOption(loc.addXYZ(-77.5, 47.5, -17.5), PowerUp.SPEED, 45, 0));
            options.add(new PowerupOption(loc.addXYZ(-77.5, 47.5, 18.5), PowerUp.SPEED, 45, 0));

            options.add(new PowerupOption(loc.addXYZ(0.5, 57.5, -39.5), PowerUp.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(0.5, 34.5, 23.5), PowerUp.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(0.5, 34.5, -22.5), PowerUp.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(0.5, 57.5, 40.5), PowerUp.ENERGY, 45, 30));

            options.add(new PowerupOption(loc.addXYZ(0.5, 34.5, -32.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(0.5, 34.5, 33.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(-4.5, 47.5, 33.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(5.5, 47.5, -32.5), 45, 45));

            options.add(new PowerupOption(loc.addXYZ(0.5, 53.5, 0.5), PowerUp.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(-25.5, 32.5, 0.5), PowerUp.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(26.5, 32.5, 0.5), PowerUp.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(0.5, 47.5, 0.5), PowerUp.HEALING, 45, 60));


            options.add(SpawnpointOption.forTeam(loc.addXYZ(93.5, 50.5, 0.5, 90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-93.5, 50.5, 0.5, -90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(-85, 50, -5), loc.addXYZ(-85, 53, 6), Material.IRON_BARS));
            options.add(new GateOption(loc.addXYZ(85, 50, -5), loc.addXYZ(85, 53, 6), Material.IRON_BARS));

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
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

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

            options.add(new PowerupOption(loc.addXYZ(0.5, 43.5, 32.5), PowerUp.SPEED, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(0.5, 43.5, -31.5), PowerUp.SPEED, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(0.5, 38.5, 8.5), PowerUp.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(0.5, 38.5, -7.5), PowerUp.ENERGY, 45, 30));

            options.add(new PowerupOption(loc.addXYZ(14.5, 35.5, -24.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(-13.5, 35.5, 25.5), 45, 45));

            options.add(new PowerupOption(loc.addXYZ(0.5, 25.5, 0.5), PowerUp.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(122.5, 45.5, 52.5), PowerUp.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(122.5, 45.5, -51.5), PowerUp.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(-126.5, 45.5, -51.5), PowerUp.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(-126.5, 45.5, 52.5), PowerUp.HEALING, 45, 60));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-139.5, 39.5, 0.5, -90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(144.5, 37.5, 0.5, 90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(-131, 40, -4), loc.addXYZ(-131, 48, 5), Material.IRON_BARS));
            options.add(new GateOption(loc.addXYZ(131, 37, -4), loc.addXYZ(131, 45, 5), Material.IRON_BARS));

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
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

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

            options.add(new PowerupOption(loc.addXYZ(94.5, 27.5, 7.5), PowerUp.SPEED, 45, 0));
            options.add(new PowerupOption(loc.addXYZ(33.5, 27.5, 16.5), PowerUp.SPEED, 45, 0));
            options.add(new PowerupOption(loc.addXYZ(-29.5, 27.5, 16.5), PowerUp.SPEED, 45, 0));
            options.add(new PowerupOption(loc.addXYZ(-90.5, 27.5, -5.5), PowerUp.SPEED, 45, 0));

            options.add(new PowerupOption(loc.addXYZ(33.5, 27.5, -14.5), PowerUp.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(94.5, 27.5, -5.5), PowerUp.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-29.5, 27.5, -14.5), PowerUp.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-90.5, 27.5, 7.5), PowerUp.ENERGY, 45, 30));

            options.add(new PowerupOption(loc.addXYZ(56.5, 30.5, 0.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(-52.5, 30.5, 0.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(4.5, 31.5, -3.5), PowerUp.ENERGY, 45, 45));
            options.add(new PowerupOption(loc.addXYZ(0.5, 31.5, 6.5), PowerUp.ENERGY, 45, 45));

            options.add(new PowerupOption(loc.addXYZ(60.5, 27.5, -28.5), PowerUp.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(-55.5, 27.5, -27.5), PowerUp.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(59.5, 27.5, 27.5), PowerUp.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(-55.5, 27.5, 27.5), PowerUp.HEALING, 45, 60));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-93.5, 81.5, 0.5, -90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(111.5, 83.5, 0.5, 90, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(19, 26, -93), loc.addXYZ(19, 30, -102), Material.IRON_BARS));

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
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

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

            options.add(new PowerupOption(loc.addXYZ(3.5, 61.5, 3.5), PowerUp.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(12.5, 61.5, 12.5), PowerUp.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(76.5, 59.5, 56.5), PowerUp.SPEED, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(72.5, 56.5, -47.5), PowerUp.SPEED, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-56.5, 56.5, 63.5), PowerUp.SPEED, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-60.5, 59.5, -40.5), PowerUp.SPEED, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(83.5, 57.5, -2.5), PowerUp.ENERGY, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-67.5, 57.5, 18.5), PowerUp.ENERGY, 45, 30));

            options.add(new PowerupOption(loc.addXYZ(-28.5, 54.5, -27.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(-28.5, 54.5, 43.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(44.5, 54.5, -27.5), 45, 45));
            options.add(new PowerupOption(loc.addXYZ(44.5, 54.5, 43.5), 45, 45));

            options.add(new PowerupOption(loc.addXYZ(93.5, 54.5, -41.5), PowerUp.ENERGY, 45, 45));
            options.add(new PowerupOption(loc.addXYZ(-80.5, 59.5, -28.5), PowerUp.ENERGY, 45, 45));
            options.add(new PowerupOption(loc.addXYZ(-77.5, 54.5, 57.5), PowerUp.ENERGY, 45, 45));
            options.add(new PowerupOption(loc.addXYZ(96.5, 59.5, 44.5), PowerUp.HEALING, 45, 45));

            options.add(new PowerupOption(loc.addXYZ(46.5, 56.5, 3.5), PowerUp.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(75.5, 54.5, -54.5), PowerUp.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(-30.5, 56.5, 12.5), PowerUp.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(-59.5, 54.5, 70.5), PowerUp.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(-47.5, 54.5, -16.5), PowerUp.HEALING, 45, 60));
            options.add(new PowerupOption(loc.addXYZ(63.5, 54.5, 32.5), PowerUp.HEALING, 45, 60));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(8, 59, 109.5, 180, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(8, 59, -94.5, 0, 0), Team.RED));

            options.add(new GateOption(loc.addXYZ(2, 54, 89), loc.addXYZ(14, 58, 89), Material.IRON_BARS));
            options.add(new GateOption(loc.addXYZ(14, 54, -74), loc.addXYZ(2, 58, -74), Material.IRON_BARS));

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
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

            return options;
        }

    },
    ARATHI(
            "Arathi",
            60,
            18,
            60 * SECOND,
            "Arathi",
            1,
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

            InterceptionPointOption farm = new InterceptionPointOption("Farm", loc.addXYZ(585.5, 54, 479.5));
            InterceptionPointOption lumbermill = new InterceptionPointOption("Lumbermill", loc.addXYZ(449.5, 96, 587.5));
            InterceptionPointOption blacksmith = new InterceptionPointOption("Blacksmith", loc.addXYZ(441.5, 65, 447.5));
            InterceptionPointOption mines = new InterceptionPointOption("Mines", loc.addXYZ(439.5, 12, 290.5));
            InterceptionPointOption stables = new InterceptionPointOption("Stables", loc.addXYZ(320.5, 56, 454.5));
            options.add(farm);
            options.add(lumbermill);
            options.add(blacksmith);
            options.add(mines);
            options.add(stables);

            options.add(SpawnpointOption.forTeam(loc.addXYZ(173.5, 67, 426.5), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(736.5, 67, 450.5).yaw(-180), Team.RED));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(621.5, 56, 448.5, 0, 0), farm));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(452.5, 96, 625.5, -180, 0), lumbermill));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(463.5, 63, 387, 0, 0), blacksmith));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(425.5, 32, 258.5, -90, 0), mines));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(290.5, 56, 418.5, 0, 0), stables));

            options.add(new GateOption(loc.addXYZ(183, 67, 447), loc.addXYZ(183, 64, 443)));
            options.add(new GateOption(loc.addXYZ(727, 67, 437), loc.addXYZ(727, 64, 441)));

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
            options.add(LobbyLocationMarker.create(loc.addXYZ(-24.5, 64, -110.5, 0, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(25.5, 64, 111.5, -180, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(116.5, 55.5, -12.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-116.5, 53.5, -11.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(87.5, 84.5, -101.5), PowerUp.HEALING, 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-136.5, 84.5, 93.5), PowerUp.HEALING, 45, 30));

            InterceptionPointOption watchTower = new InterceptionPointOption("Watch Tower", loc.addXYZ(-97.5, 53, -9.5));
            InterceptionPointOption workshop = new InterceptionPointOption("Workshop", loc.addXYZ(-107.5, 83, 94.5));
            InterceptionPointOption ruins = new InterceptionPointOption("Ruins", loc.addXYZ(0.5, 86, 0.5));
            InterceptionPointOption house = new InterceptionPointOption("House", loc.addXYZ(108.5, 83, -93.5));
            InterceptionPointOption lumbermill = new InterceptionPointOption("Lumbermill", loc.addXYZ(98.5, 51, 10.5));
            options.add(watchTower);
            options.add(workshop);
            options.add(ruins);
            options.add(house);
            options.add(lumbermill);

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-24.5, 64, -110.5, 0, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(25.5, 64, 111.5, -180, 0), Team.RED));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-111, 52, 13.5, -90, 0), watchTower));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-81.5, 83, 119.5, 90, 0), workshop));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(0.5, 83, -32.5, 0, 0), ruins));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(86.5, 83, -121.5, -90, 0), house));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(94.5, 54, 32.5, 180, 0), lumbermill));

            options.add(new GateOption(loc.addXYZ(-18.5, 71, -96.5), loc.addXYZ(-30.5, 64, -96.5)));
            options.add(new GateOption(loc.addXYZ(18.5, 69, 99.5), loc.addXYZ(32.5, 64, 99.5)));

            return options;
        }

    },
    SCORCHED(
            "Scorched",
            60,
            18,
            60 * SECOND,
            "Scorched",
            1,
            GameMode.INTERCEPTION
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(0.5, 43, -141.5, 0, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(0.5, 43, 144.5, 180, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(113.5, 55.5, 5.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-112.5, 55.5, 5.5), 45, 30));

            InterceptionPointOption towers = new InterceptionPointOption("Towers", loc.addXYZ(0.5, 44, 60.5));
            InterceptionPointOption gate = new InterceptionPointOption("Gate", loc.addXYZ(89.5, 54, 0.55));
            InterceptionPointOption crater = new InterceptionPointOption("Crater", loc.addXYZ(0.5, 28, 0.5));
            InterceptionPointOption aviary = new InterceptionPointOption("Aviary", loc.addXYZ(-88.5, 54, 0.5));
            InterceptionPointOption throne = new InterceptionPointOption("Throne", loc.addXYZ(0.5, 44, -59.5));
            options.add(towers);
            options.add(gate);
            options.add(crater);
            options.add(aviary);
            options.add(throne);

            options.add(SpawnpointOption.forTeam(loc.addXYZ(0.5, 43, -141.5, 0, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(0.5, 43, 144.5, 180, 0), Team.RED));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-0.5, 33, 72.5, 0, 0), towers));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(101.5, 54, 25.5, 135, 0), gate));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(0.5, 31, 32.5, -180, 0), crater));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-113.5, 54, -10.5, -90, 0), aviary));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(0.5, 33, -73.5, -180, 0), throne));

            options.add(new GateOption(loc.addXYZ(-3.5, 43, -116.57), loc.addXYZ(4.5, 47, -116.5), Material.IRON_BARS));
            options.add(new GateOption(loc.addXYZ(-4.5, 43, 117.5), loc.addXYZ(5.5, 47, 117.5), Material.IRON_BARS));

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
            options.add(LobbyLocationMarker.create(loc.addXYZ(174.5, 74, -0.5, 90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-170.5, 72, 2.5, -90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(-60.5, 78.5, -94.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-31.5, 66.5, 84.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(18.5, 76.5, 0.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(33.5, 66.5, -84.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(61.5, 78.5, 95.5), 45, 30));

            InterceptionPointOption apollo = new InterceptionPointOption("Apollo", loc.addXYZ(62.5, 79, 81.5));
            InterceptionPointOption pond = new InterceptionPointOption("Pond", loc.addXYZ(48.5, 66, -67.5));
            InterceptionPointOption altar = new InterceptionPointOption("Altar", loc.addXYZ(0.5, 73, 0.5));
            InterceptionPointOption inferno = new InterceptionPointOption("Inferno", loc.addXYZ(-47.5, 65, 68.5));
            InterceptionPointOption monument = new InterceptionPointOption("Monument", loc.addXYZ(-61.5, 78, -80.5));
            options.add(apollo);
            options.add(pond);
            options.add(altar);
            options.add(inferno);
            options.add(monument);

            options.add(SpawnpointOption.forTeam(loc.addXYZ(174.5, 74, -0.5, 90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-170.5, 72, 2.5, -90, 0), Team.RED));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(62.5, 74, 32.5, 0, 0), apollo));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(71.5, 66, -86.5, 45, 0), pond));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-20.5, 76, -1.5, -90, 0), altar));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-70.5, 66, 85.5, -125, 0), inferno));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-61.5, 74, -31.5, -180, 0), monument));

            options.add(new GateOption(loc.addXYZ(155.5, 74, 2.5), loc.addXYZ(155.5, 78, -3.5)));
            options.add(new GateOption(loc.addXYZ(-156.5, 72, 8.5), loc.addXYZ(-156.5, 76, -3.5)));

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
            options.add(LobbyLocationMarker.create(loc.addXYZ(-208.5, 62, -57.5, -90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(216.5, 67, 58.5, 90, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(58.5, 62.5, 63.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(14.5, 54.5, -11.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(72.5, 46.5, -90.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-59.5, 55.5, -88.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-114.5, 81.5, 115.5), 45, 30));

            InterceptionPointOption shrine = new InterceptionPointOption("Shrine", loc.addXYZ(76.5, 65, 88.5));
            InterceptionPointOption tomb = new InterceptionPointOption("Tomb", loc.addXYZ(99.5, 44, -98.5));
            InterceptionPointOption chasm = new InterceptionPointOption("Chasm", loc.addXYZ(0.5, 54, 0.5));
            InterceptionPointOption leo = new InterceptionPointOption("Leo", loc.addXYZ(-114.5, 79, 98.5));
            InterceptionPointOption ruins = new InterceptionPointOption("Ruins", loc.addXYZ(-77.5, 58, -83.5));
            options.add(shrine);
            options.add(tomb);
            options.add(chasm);
            options.add(leo);
            options.add(ruins);

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-208.5, 62, -57.5, -90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(216.5, 67, 58.5, 90, 0), Team.RED));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(82.5, 63, 107.5, -180, 0), shrine));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(132.5, 46, -94.5, 91, 0), tomb));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-23.5, 56, 27.5, -180, 0), chasm));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-138.5, 78, 89.5, -90, 0), leo));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-107.5, 62, -81.5, -90, 0), ruins));


            options.add(new GateOption(loc.addXYZ(-175.5, 63, -50.5), loc.addXYZ(-175.5, 67, -64.5), Material.IRON_BARS));
            options.add(new GateOption(loc.addXYZ(178.5, 67, 65.5), loc.addXYZ(178.5, 73, 51.5), Material.IRON_BARS));

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

            InterceptionPointOption eclipse = new InterceptionPointOption("Eclipse", loc.addXYZ(0.5, 85, 0.5));
            InterceptionPointOption glacier = new InterceptionPointOption("Glacier", loc.addXYZ(23.5, 62, 70.5));
            InterceptionPointOption sun = new InterceptionPointOption("Sun", loc.addXYZ(-18.5, 67, 134.5));
            InterceptionPointOption moon = new InterceptionPointOption("Moon", loc.addXYZ(19.5, 67, -133.5));
            InterceptionPointOption valley = new InterceptionPointOption("Valley", loc.addXYZ(-22.5, 62, -69.5));
            options.add(eclipse);
            options.add(glacier);
            options.add(sun);
            options.add(moon);
            options.add(valley);

            options.add(SpawnpointOption.forTeam(loc.addXYZ(115.5, 89.5, 0.5, 90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-114.5, 89.5, 0.5, -90, 0), Team.RED));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(0.5, 77, -14.5, 180, 0), eclipse));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(52.5, 66, 62.5, 90, 0), glacier));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-23.5, 70, 170.5, 180, 0), sun));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(25.5, 70, -167.5, 0, 0), moon));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-51.5, 66, -60.5, -90, 0), valley));

            options.add(new GateOption(loc.addXYZ(-100, 89, 4), loc.addXYZ(-100, 93, -3)));
            options.add(new GateOption(loc.addXYZ(100, 89, 4), loc.addXYZ(100, 93, -3)));

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
            options.add(LobbyLocationMarker.create(loc.addXYZ(117.5, 5, -122.5, 0, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-211.5, 7, -119.5, 0, 0), Team.RED).asOption());

            options.add(new PowerupOption(loc.addXYZ(-41.5, 19.5, 4.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(65.5, 7.5, 56.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-40.5, 4.5, 67.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-179.5, 14.5, 59.5), 45, 30));
            options.add(new PowerupOption(loc.addXYZ(-67.5, 4.5, -121.5), 45, 30));

            InterceptionPointOption bridge = new InterceptionPointOption("Bridge", loc.addXYZ(-46.5, 4, -114.5));
            InterceptionPointOption stables = new InterceptionPointOption("Stables", loc.addXYZ(85.5, 8, 57.5));
            InterceptionPointOption stump = new InterceptionPointOption("Stump", loc.addXYZ(-50.5, 13, -4.5));
            InterceptionPointOption butchers = new InterceptionPointOption("Butchers", loc.addXYZ(-199.5, 7, 37.5));
            InterceptionPointOption quarry = new InterceptionPointOption("Quarry", loc.addXYZ(-47.5, 3, 59.5));
            options.add(bridge);
            options.add(stables);
            options.add(stump);
            options.add(butchers);
            options.add(quarry);

            options.add(SpawnpointOption.forTeam(loc.addXYZ(117.5, 5, -122.5, 0, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-211.5, 7, -119.5, 0, 0), Team.RED));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-52.5, 5, -93.5, -180, 0), bridge));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(67.5, 7, 26.5, -30, 0), stables));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-59.5, 12, -34.5, 0, 0), stump));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-171.5, 7, 21, 60, 0), butchers));
            options.add(SpawnpointOption.interceptionPoint(loc.addXYZ(-58.5, 4, 65.5, -118, 0), quarry));

            options.add(new GateOption(loc.addXYZ(101.5, 6, -95.5), loc.addXYZ(105.5, 13, -95.53)));
            options.add(new GateOption(loc.addXYZ(124.5, 6, -93.5), loc.addXYZ(129.5, 13, -93.5)));
            options.add(new GateOption(loc.addXYZ(-202.5, 7, -87.5), loc.addXYZ(-195.5, 15, -87.5)));
            options.add(new GateOption(loc.addXYZ(-225.5, 7, -88.5), loc.addXYZ(-218.5, 15, -88.5)));

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
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

            return options;
        }

    },
    ILLUSION_VALLEY(
            "Illusion Valley",
            4,
            1,
            120 * SECOND,
            "IllusionValley",
            4,
            GameMode.WAVE_DEFENSE
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);
            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(1.5, 14, 13.5), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(1.5, 14, 13.5), Team.RED).asOption());

            options.add(SpawnpointOption.forTeam(loc.addXYZ(1.5, 14, 13.5), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-14.5, 14, 3.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-9.5, 14, -8.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(4.5, 14, -12.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(14.5, 14, -4.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(11.5, 14, 5.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(5.5, 14, 14.5), Team.RED));

            options.add(new GraveOption());
            options.add(new RespawnWaveOption(1, 20, 45));
            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

            options.add(new CurrencyOnEventOption()
                    .onKill(500)
            );
            options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                    .add(1, new SimpleWave(12, 10 * SECOND, null)
                                    //basic
                                    .add(0.6, Mob.ZOMBIE_LAMENT)
                                    //.add(0, Mobs.BASIC_SKELETON)
                                    //.add(0, Mobs.PIG_DISCIPLE)
                                    .add(0.1, Mob.SLIMY_ANOMALY)
                                    .add(0.05, Mob.ARACHNO_VENARI)
                                    //elite
                                    .add(0.3, Mob.ZOMBIE_SWORDSMAN)
                                    .add(0.1, Mob.SKELETAL_WARLOCK)
                                    //.add(0, Mobs.PIG_SHAMAN)
                                    .add(0.02, Mob.ILLUMINATION)
                                    .add(0.1, Mob.GOLEM_APPRENTICE)
                                    //envoy
                                    //.add(0, Mobs.ZOMBIE_VANGUARD)
                                    //.add(0, Mobs.ENVOY_SKELETON)
                                    //.add(0, Mobs.PIG_ALLEVIATOR)
                                    //void
                                    .add(0.04, Mob.SKELETAL_MESMER)
                                    .add(0.04, Mob.ZOMBIE_KNIGHT)
                            //.add(0, Mobs.VOID_ZOMBIE)
                    )
                    .add(5, new SimpleWave(2, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.BOLTARO)
                    )
                    .add(6, new SimpleWave(16, 10 * SECOND, null)
                                    //basic
                                    .add(0.5, Mob.ZOMBIE_LAMENT)
                                    //.add(0, Mobs.BASIC_SKELETON)
                                    //.add(0, Mobs.PIG_DISCIPLE)
                                    .add(0.1, Mob.SLIMY_ANOMALY)
                                    .add(0.05, Mob.ARACHNO_VENARI)
                                    //elite
                                    .add(0.3, Mob.ZOMBIE_SWORDSMAN)
                                    .add(0.05, Mob.SKELETAL_WARLOCK)
                                    //.add(0, Mobs.PIG_SHAMAN)
                                    .add(0.02, Mob.ILLUMINATION)
                                    .add(0.1, Mob.GOLEM_APPRENTICE)
                                    //envoy
                                    //.add(0, Mobs.ZOMBIE_VANGUARD)
                                    //.add(0, Mobs.ENVOY_SKELETON)
                                    //.add(0, Mobs.PIG_ALLEVIATOR)
                                    //void
                                    .add(0.03, Mob.SKELETAL_MESMER)
                                    .add(0.03, Mob.VOID_ZOMBIE)
                                    .add(0.03, Mob.ZOMBIE_KNIGHT)
                                    .add(0.03, Mob.RIFT_WALKER)
                            //.add(0.01, Mobs.FORGOTTEN_ZOMBIE)
                    )
                    .add(10, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.GHOULCALLER)
                    )
                    .add(11, new SimpleWave(18, 10 * SECOND, null)
                                    //basic
                                    .add(0.35, Mob.ZOMBIE_LAMENT)
                                    //.add(0, Mobs.BASIC_SKELETON)
                                    //.add(0.0, Mobs.PIG_DISCIPLE)
                                    .add(0.15, Mob.SLIMY_ANOMALY)
                                    //.add(0.0, Mobs.ARACHNO_VENARI)
                                    //elite
                                    .add(0.25, Mob.ZOMBIE_SWORDSMAN)
                                    .add(0.05, Mob.SKELETAL_WARLOCK)
                                    .add(0.1, Mob.PIG_SHAMAN)
                                    .add(0.02, Mob.ILLUMINATION)
                                    .add(0.1, Mob.GOLEM_APPRENTICE)
                                    .add(0.01, Mob.WITCH_DEACON)
                                    //envoy
                                    .add(0.1, Mob.ZOMBIE_VANGUARD)
                                    //.add(0, Mobs.ENVOY_SKELETON)
                                    //.add(0, Mobs.PIG_ALLEVIATOR)
                                    .add(0.01, Mob.ADVANCED_WARRIOR_BERSERKER)
                                    //void
                                    .add(0.04, Mob.VOID_ZOMBIE)
                                    .add(0.04, Mob.SKELETAL_MESMER)
                                    .add(0.02, Mob.SKELETAL_SORCERER)
                                    .add(0.04, Mob.ZOMBIE_KNIGHT)
                                    .add(0.04, Mob.CREEPY_BOMBER)
                            //.add(0.01, Mobs.FORGOTTEN_ZOMBIE)
                    )
                    .add(15, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.NARMER)
                    )
                    .add(16, new SimpleWave(20, 10 * SECOND, null)
                            //basic
                            .add(0.3, Mob.ZOMBIE_LAMENT)
                            //.add(0, Mobs.BASIC_SKELETON)
                            //.add(0.0, Mobs.PIG_DISCIPLE)
                            .add(0.15, Mob.SLIMY_ANOMALY)
                            .add(0.1, Mob.ARACHNO_VENARI)
                            //elite
                            .add(0.6, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.05, Mob.SKELETAL_WARLOCK)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.02, Mob.ILLUMINATION)
                            .add(0.15, Mob.GOLEM_APPRENTICE)
                            .add(0.01, Mob.WITCH_DEACON)
                            //envoy
                            .add(0.02, Mob.ZOMBIE_VANGUARD)
                            //.add(0, Mobs.ENVOY_SKELETON)
                            //.add(0, Mobs.PIG_ALLEVIATOR)
                            .add(0.02, Mob.ADVANCED_WARRIOR_BERSERKER)
                            //void
                            .add(0.03, Mob.VOID_ZOMBIE)
                            .add(0.06, Mob.SKELETAL_MESMER)
                            .add(0.06, Mob.SKELETAL_SORCERER)
                            .add(0.02, Mob.NIGHTMARE_ZOMBIE)
                            .add(0.04, Mob.CREEPY_BOMBER)
                            .add(0.04, Mob.ZOMBIE_KNIGHT)
                            .add(0.04, Mob.RIFT_WALKER)
                            .add(0.04, Mob.FIRE_SPLITTER)
                    )
                    .add(20, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.MITHRA)
                    )
                    .add(21, new SimpleWave(25, 10 * SECOND, null)
                            //basic
                            .add(0.2, Mob.ZOMBIE_LAMENT)
                            //.add(0, Mobs.BASIC_SKELETON)
                            //.add(0, Mobs.PIG_DISCIPLE)
                            .add(0.1, Mob.SLIMY_ANOMALY)
                            .add(0.2, Mob.ARACHNO_VENARI)
                            //elite
                            .add(0.5, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.1, Mob.SKELETAL_WARLOCK)
                            .add(0.2, Mob.PIG_SHAMAN)
                            .add(0.06, Mob.ILLUMINATION)
                            .add(0.15, Mob.GOLEM_APPRENTICE)
                            .add(0.04, Mob.WITCH_DEACON)
                            //envoy
                            .add(0.05, Mob.ZOMBIE_VANGUARD)
                            //.add(0, Mobs.ENVOY_SKELETON)
                            .add(0.05, Mob.PIG_ALLEVIATOR)
                            .add(0.04, Mob.ADVANCED_WARRIOR_BERSERKER)
                            //elite
                            .add(0.02, Mob.VOID_ZOMBIE)
                            .add(0.06, Mob.SKELETAL_SORCERER)
                            .add(0.04, Mob.ZOMBIE_KNIGHT)
                            .add(0.04, Mob.NIGHTMARE_ZOMBIE)
                            .add(0.06, Mob.CREEPY_BOMBER)
                            .add(0.1, Mob.SKELETAL_MESMER)
                            .add(0.04, Mob.RIFT_WALKER)
                            .add(0.08, Mob.FIRE_SPLITTER)
                    )
                    .add(25, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.ZENITH)
                    ),
                    DifficultyIndex.HARD
            ));
            options.add(new ItemOption());
            options.add(new CoinGainOption()
                    .guildCoinInsigniaConvertBonus(2000)
            );
            options.add(new ExperienceGainOption()
                    .playerExpPer(96)
                    .playerExpGameWinBonus(1500)
                    .guildExpPer(8)
                    .guildExpMaxGameWinBonus(500)
            );

            return options;
        }

    },
    ILLUSION_VALLEY2(
            "Illusion Valley",
            4,
            1,
            120 * SECOND,
            "IllusionValley2",
            4,
            GameMode.WAVE_DEFENSE
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);
            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(1.5, 14, 13.5), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(1.5, 14, 13.5), Team.RED).asOption());

            options.add(SpawnpointOption.forTeam(loc.addXYZ(1.5, 14, 13.5), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-14.5, 14, 3.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-9.5, 14, -8.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(4.5, 14, -12.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(14.5, 14, -4.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(11.5, 14, 5.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(5.5, 14, 14.5), Team.RED));

            options.add(new GraveOption());
            options.add(new RespawnWaveOption(1, 20, 60));
            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

            options.add(new CurrencyOnEventOption()
                    .onKill(500)
            );
            options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                    .add(1, new SimpleWave(12, 10 * SECOND, null)
                                    //basic
                                    .add(0.6, Mob.ZOMBIE_LAMENT)
                                    .add(0.1, Mob.SLIMY_ANOMALY)
                                    .add(0.05, Mob.ARACHNO_VENARI)
                                    //elite
                                    .add(0.3, Mob.ZOMBIE_SWORDSMAN)
                                    //.add(0, Mobs.PIG_SHAMAN)
                                    .add(0.04, Mob.ILLUMINATION)
                                    .add(0.15, Mob.GOLEM_APPRENTICE)
                                    //envoy
                                    //void
                                    .add(0.05, Mob.PIG_PARTICLE)
                                    .add(0.08, Mob.SKELETAL_MESMER)
                                    .add(0.08, Mob.ZOMBIE_KNIGHT)
                            //.add(0, Mobs.VOID_ZOMBIE)
                    )
                    .add(5, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.NARMER)
                    )
                    .add(6, new SimpleWave(16, 10 * SECOND, null)
                                    //basic
                                    .add(0.1, Mob.SLIMY_ANOMALY)
                                    //elite
                                    .add(0.5, Mob.ZOMBIE_SWORDSMAN)
                                    .add(0.05, Mob.SKELETAL_WARLOCK)
                                    //.add(0, Mobs.PIG_SHAMAN)
                                    .add(0.02, Mob.ILLUMINATION)
                                    .add(0.1, Mob.GOLEM_APPRENTICE)
                                    //envoy
                                    //void
                                    .add(0.06, Mob.CELESTIAL_OPUS)
                                    .add(0.06, Mob.SKELETAL_MESMER)
                                    .add(0.05, Mob.VOID_ZOMBIE)
                                    .add(0.06, Mob.ZOMBIE_KNIGHT)
                                    .add(0.06, Mob.RIFT_WALKER)
                                    .add(0.04, Mob.CREEPY_BOMBER)
                            //.add(0.01, Mobs.FORGOTTEN_ZOMBIE)
                    )
                    .add(10, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.MITHRA)
                    )
                    .add(11, new SimpleWave(18, 10 * SECOND, null)
                                    //basic
                                    .add(0.15, Mob.SLIMY_ANOMALY)
                                    //elite
                                    .add(0.4, Mob.ZOMBIE_SWORDSMAN)
                                    .add(0.05, Mob.SKELETAL_WARLOCK)
                                    .add(0.1, Mob.PIG_SHAMAN)
                                    .add(0.02, Mob.ILLUMINATION)
                                    .add(0.15, Mob.GOLEM_APPRENTICE)
                                    .add(0.01, Mob.WITCH_DEACON)
                                    //envoy
                                    .add(0.1, Mob.ZOMBIE_VANGUARD)
                                    .add(0.02, Mob.ADVANCED_WARRIOR_BERSERKER)
                                    //void
                                    .add(0.02, Mob.NIGHTMARE_ZOMBIE)
                                    .add(0.04, Mob.CREEPY_BOMBER)
                                    .add(0.06, Mob.CELESTIAL_OPUS)
                                    .add(0.04, Mob.VOID_ZOMBIE)
                                    .add(0.04, Mob.SKELETAL_MESMER)
                                    .add(0.02, Mob.SKELETAL_SORCERER)
                                    .add(0.04, Mob.ZOMBIE_KNIGHT)
                            //.add(0.01, Mobs.FORGOTTEN_ZOMBIE)
                    )
                    .add(15, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.ZENITH)
                    )
                    .add(16, new SimpleWave(20, 10 * SECOND, null)
                            //elite
                            .add(0.4, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.05, Mob.PIG_PARTICLE)
                            .add(0.02, Mob.ILLUMINATION)
                            .add(0.25, Mob.GOLEM_APPRENTICE)
                            .add(0.01, Mob.WITCH_DEACON)
                            //envoy
                            .add(0.02, Mob.ZOMBIE_VANGUARD)
                            .add(0.02, Mob.ADVANCED_WARRIOR_BERSERKER)
                            //void
                            .add(0.03, Mob.VOID_ZOMBIE)
                            .add(0.06, Mob.SKELETAL_MESMER)
                            .add(0.06, Mob.SKELETAL_SORCERER)
                            .add(0.02, Mob.NIGHTMARE_ZOMBIE)
                            .add(0.04, Mob.CREEPY_BOMBER)
                            .add(0.06, Mob.CELESTIAL_OPUS)
                            .add(0.04, Mob.ZOMBIE_KNIGHT)
                            .add(0.04, Mob.RIFT_WALKER)
                            .add(0.04, Mob.FIRE_SPLITTER)
                            .add(0.02, Mob.SCRUPULOUS_ZOMBIE)
                    )
                    .add(20, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.ILLUMINA)
                    )
                    .add(21, new SimpleWave(25, 10 * SECOND, null)
                            //basic
                            //elite
                            .add(0.4, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.05, Mob.PIG_PARTICLE)
                            .add(0.06, Mob.ILLUMINATION)
                            .add(0.25, Mob.GOLEM_APPRENTICE)
                            .add(0.04, Mob.WITCH_DEACON)
                            //envoy
                            .add(0.05, Mob.ZOMBIE_VANGUARD)
                            //.add(0, Mobs.ENVOY_SKELETON)
                            .add(0.05, Mob.PIG_ALLEVIATOR)
                            .add(0.04, Mob.ADVANCED_WARRIOR_BERSERKER)
                            //elite
                            .add(0.02, Mob.VOID_ZOMBIE)
                            .add(0.06, Mob.SKELETAL_SORCERER)
                            .add(0.04, Mob.ZOMBIE_KNIGHT)
                            .add(0.06, Mob.CELESTIAL_OPUS)
                            .add(0.04, Mob.NIGHTMARE_ZOMBIE)
                            .add(0.06, Mob.CREEPY_BOMBER)
                            .add(0.1, Mob.SKELETAL_MESMER)
                            .add(0.04, Mob.RIFT_WALKER)
                            .add(0.08, Mob.FIRE_SPLITTER)
                            .add(0.02, Mob.SCRUPULOUS_ZOMBIE)
                    )
                    .add(25, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.VOID)
                    ),
                    DifficultyIndex.EXTREME
            ));
            options.add(new ItemOption());
            options.add(new CoinGainOption()
                    .guildCoinInsigniaConvertBonus(4000)
            );
            options.add(new ExperienceGainOption()
                    .playerExpPer(192)
                    .playerExpGameWinBonus(3000)
                    .guildExpPer(16)
                    .guildExpMaxGameWinBonus(1000)
            );

            return options;
        }

    },
    ILLUSION_RIFT(
            "Illusion Rift",
            4,
            1,
            120 * SECOND,
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

            options.add(new PowerupOption(loc.addXYZ(16.5, 24.5, 17.5), PowerUp.COOLDOWN, 180, 30));
            options.add(new PowerupOption(loc.addXYZ(-15.5, 24.5, -18.5), PowerUp.HEALING, 90, 30));

            options.add(new RespawnWaveOption(1, 20, 20));
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

            options.add(new CurrencyOnEventOption()
                    .onKill(500)
            );
            options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                    .add(1, new SimpleWave(12, 10 * SECOND, null)
                                    //basic
                                    .add(0.9, Mob.ZOMBIE_LANCER)
                                    .add(0.08, Mob.SKELETAL_MAGE)
                                    .add(0.1, Mob.PIG_DISCIPLE)
                                    .add(0.1, Mob.SLIMY_ANOMALY)
                                    .add(0.05, Mob.ARACHNO_VENARI)
                                    //elite
                                    .add(0.01, Mob.ZOMBIE_SWORDSMAN)
                                    .add(0.01, Mob.SKELETAL_WARLOCK)
                                    //.add(0, Mobs.PIG_SHAMAN)
                                    //.add(0, Mobs.MAGMA_CUBE)
                                    .add(0.01, Mob.GOLEM_APPRENTICE)
                            //envoy
                            //.add(0, Mobs.ZOMBIE_VANGUARD)
                            //.add(0, Mobs.ENVOY_SKELETON)
                            //.add(0, Mobs.PIG_ALLEVIATOR)
                            //void
                            //.add(0, Mobs.VOID_ZOMBIE)
                    )
                    .add(5, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.BOLTARO)
                    )
                    .add(6, new SimpleWave(16, 10 * SECOND, null)
                                    //basic
                                    .add(0.8, Mob.ZOMBIE_LANCER)
                                    .add(0.1, Mob.SKELETAL_MAGE)
                                    .add(0.1, Mob.PIG_DISCIPLE)
                                    .add(0.1, Mob.SLIMY_ANOMALY)
                                    .add(0.05, Mob.ARACHNO_VENARI)
                                    //elite
                                    .add(0.15, Mob.ZOMBIE_SWORDSMAN)
                                    .add(0.01, Mob.SKELETAL_WARLOCK)
                                    .add(0.05, Mob.PIG_SHAMAN)
                                    //.add(0, Mobs.MAGMA_CUBE)
                                    .add(0.03, Mob.GOLEM_APPRENTICE)
                            //envoy
                            //.add(0, Mobs.ZOMBIE_VANGUARD)
                            //.add(0, Mobs.ENVOY_SKELETON)
                            //.add(0, Mobs.PIG_ALLEVIATOR)
                            //void
                            //.add(0, Mobs.VOID_ZOMBIE)
                    )
                    .add(10, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.GHOULCALLER)
                    )
                    .add(11, new SimpleWave(18, 10 * SECOND, null)
                                    //basic
                                    .add(0.7, Mob.ZOMBIE_LANCER)
                                    .add(0.1, Mob.SKELETAL_MAGE)
                                    .add(0.05, Mob.PIG_DISCIPLE)
                                    .add(0.15, Mob.SLIMY_ANOMALY)
                                    .add(0.25, Mob.ARACHNO_VENARI)
                                    //elite
                                    .add(0.2, Mob.ZOMBIE_SWORDSMAN)
                                    .add(0.05, Mob.SKELETAL_WARLOCK)
                                    .add(0.1, Mob.PIG_SHAMAN)
                                    .add(0.02, Mob.ILLUMINATION)
                                    .add(0.02, Mob.GOLEM_APPRENTICE)
                                    .add(0.02, Mob.WITCH_DEACON)
                                    .add(0.02, Mob.BASIC_WARRIOR_BERSERKER)
                                    //envoy
                                    .add(0.01, Mob.ZOMBIE_VANGUARD)
                            //.add(0, Mobs.ENVOY_SKELETON)
                            //.add(0, Mobs.PIG_ALLEVIATOR)
                            //void
                            //.add(0, Mobs.VOID_ZOMBIE)
                    )
                    .add(15, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.NARMER)
                    )
                    .add(16, new SimpleWave(20, 10 * SECOND, null)
                            //basic
                            .add(0.5, Mob.ZOMBIE_LANCER)
                            .add(0.2, Mob.SKELETAL_MAGE)
                            //.add(0, Mobs.PIG_DISCIPLE)
                            .add(0.15, Mob.SLIMY_ANOMALY)
                            .add(0.5, Mob.ARACHNO_VENARI)
                            //elite
                            .add(0.35, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.1, Mob.SKELETAL_WARLOCK)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.02, Mob.ILLUMINATION)
                            .add(0.05, Mob.GOLEM_APPRENTICE)
                            .add(0.04, Mob.WITCH_DEACON)
                            .add(0.01, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                            //envoy
                            .add(0.02, Mob.ZOMBIE_VANGUARD)
                            .add(0.02, Mob.SKELETAL_ENTROPY)
                            .add(0.01, Mob.PIG_ALLEVIATOR)
                            //void
                            .add(0.03, Mob.VOID_ZOMBIE)
                            .add(0.01, Mob.SKELETAL_MESMER)
                            .add(0.03, Mob.FIRE_SPLITTER)
                    )
                    .add(20, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.MITHRA)
                    )
                    .add(21, new SimpleWave(25, 10 * SECOND, null)
                            //basic
                            .add(0.4, Mob.ZOMBIE_LANCER)
                            //.add(0, Mobs.BASIC_SKELETON)
                            //.add(0, Mobs.PIG_DISCIPLE)
                            .add(0.2, Mob.SLIMY_ANOMALY)
                            .add(0.2, Mob.ARACHNO_VENARI)
                            //elite
                            .add(0.5, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.1, Mob.SKELETAL_WARLOCK)
                            .add(0.2, Mob.PIG_SHAMAN)
                            .add(0.02, Mob.ILLUMINATION)
                            .add(0.15, Mob.GOLEM_APPRENTICE)
                            .add(0.06, Mob.WITCH_DEACON)
                            .add(0.02, Mob.ADVANCED_WARRIOR_BERSERKER)
                            //envoy
                            .add(0.05, Mob.ZOMBIE_VANGUARD)
                            .add(0.05, Mob.SKELETAL_ENTROPY)
                            .add(0.01, Mob.PIG_ALLEVIATOR)
                            //elite
                            .add(0.04, Mob.VOID_ZOMBIE)
                            .add(0.04, Mob.SKELETAL_MESMER)
                            .add(0.04, Mob.FIRE_SPLITTER)
                            .add(0.02, Mob.RIFT_WALKER)
                    )
                    .add(25, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.ZENITH)
                    ),
                    DifficultyIndex.NORMAL
            ));
            options.add(new ItemOption());
            options.add(new CoinGainOption()
                    .guildCoinInsigniaConvertBonus(1000)
            );
            options.add(new ExperienceGainOption()
                    .playerExpPer(48)
                    .playerExpGameWinBonus(1500)
                    .guildExpPer(4)
                    .guildExpMaxGameWinBonus(200)
            );

            return options;
        }

    },
    ILLUSION_APERTURE(
            "Illusion Aperture",
            4,
            1,
            60 * SECOND,
            "IllusionAperture",
            10,
            GameMode.WAVE_DEFENSE
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);
            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(601.5, 17, 220.5), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(601.5, 17, 220.5), Team.RED).asOption());

            options.add(SpawnpointOption.forTeam(loc.addXYZ(614.5, 19, 227.5), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(617.5, 19, 240.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(608, 18, 250.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(595.5, 18, 255.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(586.5, 19, 235.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(593.5, 20, 242.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(608, 20, 232.5), Team.RED));

            options.add(new PowerupOption(loc.addXYZ(618.5, 19.5, 223.5), PowerUp.COOLDOWN, 180, 30));
            options.add(new PowerupOption(loc.addXYZ(581.5, 19.5, 250.5), PowerUp.HEALING, 90, 30));

            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            options.add(new RespawnWaveOption(1, 20, 20));
            options.add(new CurrencyOnEventOption()
                    .onKill(1000)
            );
            options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                    .add(1, new SimpleWave(8, 10 * SECOND, null)
                                    //basic
                                    .add(0.9, Mob.ZOMBIE_LANCER)
                                    .add(0.04, Mob.SKELETAL_MAGE)
                                    .add(0.1, Mob.SLIMY_ANOMALY)
                                    .add(0.05, Mob.ARACHNO_VENARI)
                            //elite
                            //.add(0, Mobs.ZOMBIE_SWORDSMAN)
                            //.add(0, Mobs.SKELETAL_WARLOCK)
                            //.add(0, Mobs.PIG_SHAMAN)
                            //.add(0, Mobs.MAGMA_CUBE)
                            //.add(0, Mobs.IRON_GOLEM)
                            //envoy
                            //.add(0, Mobs.ZOMBIE_VANGUARD)
                            //.add(0, Mobs.ENVOY_SKELETON)
                            //.add(0, Mobs.PIG_ALLEVIATOR)
                            //void
                            //.add(0, Mobs.VOID_ZOMBIE)
                    )
                    .add(5, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.BOLTARO)
                    )
                    .add(6, new SimpleWave(10, 10 * SECOND, null)
                                    //basic
                                    .add(0.8, Mob.ZOMBIE_LANCER)
                                    .add(0.1, Mob.SKELETAL_MAGE)
                                    .add(0.1, Mob.PIG_DISCIPLE)
                                    .add(0.1, Mob.SLIMY_ANOMALY)
                                    .add(0.05, Mob.ARACHNO_VENARI)
                                    //elite
                                    .add(0.05, Mob.ZOMBIE_SWORDSMAN)
                                    .add(0.01, Mob.SKELETAL_WARLOCK)
                                    .add(0.05, Mob.PIG_SHAMAN)
                                    //.add(0, Mobs.MAGMA_CUBE)
                                    .add(0.03, Mob.GOLEM_APPRENTICE)
                            //envoy
                            //.add(0, Mobs.ZOMBIE_VANGUARD)
                            //.add(0, Mobs.ENVOY_SKELETON)
                            //.add(0, Mobs.PIG_ALLEVIATOR)
                            //void
                            //.add(0, Mobs.VOID_ZOMBIE)
                    )
                    .add(10, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.GHOULCALLER)
                    )
                    .add(11, new SimpleWave(12, 10 * SECOND, null)
                                    //basic
                                    .add(0.7, Mob.ZOMBIE_LANCER)
                                    .add(0.1, Mob.SKELETAL_MAGE)
                                    .add(0.25, Mob.PIG_DISCIPLE)
                                    .add(0.25, Mob.SLIMY_ANOMALY)
                                    //elite
                                    .add(0.1, Mob.ZOMBIE_SWORDSMAN)
                                    .add(0.05, Mob.SKELETAL_WARLOCK)
                                    .add(0.1, Mob.PIG_SHAMAN)
                                    .add(0.02, Mob.ILLUMINATION)
                                    //envoy
                                    .add(0.01, Mob.ZOMBIE_VANGUARD)
                            //.add(0, Mobs.ENVOY_SKELETON)
                            //.add(0, Mobs.PIG_ALLEVIATOR)
                            //void
                            //.add(0, Mobs.VOID_ZOMBIE)
                    )
                    .add(15, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.NARMER)
                    )
                    .add(16, new SimpleWave(15, 10 * SECOND, null)
                                    //basic
                                    .add(0.7, Mob.ZOMBIE_LANCER)
                                    .add(0.2, Mob.SKELETAL_MAGE)
                                    //.add(0, Mobs.PIG_DISCIPLE)
                                    .add(0.15, Mob.SLIMY_ANOMALY)
                                    .add(0.1, Mob.ARACHNO_VENARI)
                                    //elite
                                    .add(0.15, Mob.ZOMBIE_SWORDSMAN)
                                    .add(0.1, Mob.SKELETAL_WARLOCK)
                                    .add(0.1, Mob.PIG_SHAMAN)
                                    .add(0.02, Mob.ILLUMINATION)
                                    .add(0.05, Mob.GOLEM_APPRENTICE)
                                    .add(0.04, Mob.WITCH_DEACON)
                                    .add(0.01, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                                    //envoy
                                    .add(0.02, Mob.ZOMBIE_VANGUARD)
                                    .add(0.02, Mob.SKELETAL_ENTROPY)
                                    .add(0.01, Mob.PIG_ALLEVIATOR)
                            //void
                            //.add(0, Mobs.VOID_ZOMBIE)
                    )
                    .add(20, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.MITHRA)
                    )
                    .add(21, new SimpleWave(18, 10 * SECOND, null)
                                    //basic
                                    .add(0.5, Mob.ZOMBIE_LANCER)
                                    //.add(0, Mobs.BASIC_SKELETON)
                                    //.add(0, Mobs.PIG_DISCIPLE)
                                    .add(0.2, Mob.SLIMY_ANOMALY)
                                    .add(0.1, Mob.ARACHNO_VENARI)
                                    //elite
                                    .add(0.3, Mob.ZOMBIE_SWORDSMAN)
                                    .add(0.1, Mob.SKELETAL_WARLOCK)
                                    .add(0.2, Mob.PIG_SHAMAN)
                                    .add(0.02, Mob.ILLUMINATION)
                                    .add(0.02, Mob.GOLEM_APPRENTICE)
                                    .add(0.02, Mob.WITCH_DEACON)
                                    //envoy
                                    .add(0.05, Mob.SKELETAL_ENTROPY)
                            //elite
                            //.add(0, Mobs.VOID_ZOMBIE)
                    )
                    .add(25, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.ZENITH)
                    ),
                    DifficultyIndex.EASY
            ));
            options.add(new ItemOption());
            options.add(new CoinGainOption()
                    .guildCoinInsigniaConvertBonus(750)
            );
            options.add(new ExperienceGainOption()
                    .playerExpPer(24)
                    .guildExpPer(2)
            );

            return options;
        }

    },
    ILLUSION_CROSSFIRE(
            "Illusion Crossfire",
            6,
            1,
            120 * SECOND,
            "IllusionCrossfire",
            7,
            GameMode.WAVE_DEFENSE
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(112.5, 11, 77.5), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(112.5, 11, 77.5), Team.RED).asOption());

            options.add(SpawnpointOption.forTeam(loc.addXYZ(112.5, 11, 77.5), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(104.5, 11, 71.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(97.5, 11, 62.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(104.5, 11, 53.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(112.5, 11, 47.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(120.5, 11, 53.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(129.5, 12, 45.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(95.5, 12, 79.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(129.5, 12, 79.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(95.5, 12, 45.5), Team.RED));

            options.add(new RespawnWaveOption(1, 20, 30));
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

            options.add(new CurrencyOnEventOption()
                    .onKill(250, true)
                    .onPerWaveClear(5, 1000)
            );
            options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                    .add(1, new SimpleWave(10, 10 * SECOND, null)
                            // basic
                            .add(0.8, Mob.ZOMBIE_LANCER)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                            .add(0.1, Mob.SKELETAL_MAGE)
                            .add(0, Mob.PIG_DISCIPLE)
                            .add(0.06, Mob.SLIMY_ANOMALY)
                            .add(0.06, Mob.ARACHNO_VENARI)
                            // elite
                            .add(0.15, Mob.ZOMBIE_SWORDSMAN)
                            .add(0, Mob.SKELETAL_WARLOCK)
                            .add(0, Mob.PIG_SHAMAN)
                            .add(0.01, Mob.ILLUMINATION)
                            .add(0.01, Mob.GOLEM_APPRENTICE)
                            .add(0, Mob.WITCH_DEACON)
                            // envoy
                            .add(0, Mob.ZOMBIE_VANGUARD)
                            .add(0, Mob.SKELETAL_ENTROPY)
                            .add(0, Mob.PIG_ALLEVIATOR)
                            // void
                            .add(0, Mob.VOID_ZOMBIE)
                            .add(0, Mob.SKELETAL_MESMER)
                            // exiled
                            .add(0, Mob.ZOMBIE_KNIGHT)
                            .add(0, Mob.SCRUPULOUS_ZOMBIE)
                            .add(0, Mob.SKELETAL_SORCERER)
                            .add(0, Mob.FIRE_SPLITTER)
                            .add(0, Mob.RIFT_WALKER)
                            // forgotten
                            .add(0, Mob.NIGHTMARE_ZOMBIE)
                    )
                    .add(5, new SimpleWave(10, 10 * SECOND, null)
                                    //basic
                                    .add(0.8, Mob.ZOMBIE_LANCER)
                                    .add(0.05, Mob.ZOMBIE_LAMENT)
                                    .add(0.1, Mob.SKELETAL_MAGE)
                                    //.add(0, Mobs.PIG_DISCIPLE)
                                    .add(0.06, Mob.SLIMY_ANOMALY)
                                    .add(0.08, Mob.ARACHNO_VENARI)
                                    //elite
                                    .add(0.2, Mob.ZOMBIE_SWORDSMAN)
                                    //.add(0, Mobs.SKELETAL_WARLOCK)
                                    .add(0.1, Mob.PIG_SHAMAN)
                                    .add(0.02, Mob.ILLUMINATION)
                                    .add(0.02, Mob.GOLEM_APPRENTICE)
                                    .add(0.01, Mob.WITCH_DEACON)
                                    //envoy
                                    .add(0.01, Mob.ZOMBIE_VANGUARD)
                                    //.add(0, Mobs.ENVOY_SKELETON)
                                    .add(0.01, Mob.PIG_ALLEVIATOR)
                                    //void
                                    //.add(0, Mobs.VOID_ZOMBIE)
                                    .add(0.01, Mob.SKELETAL_MESMER)
                            // exiled
                            //.add(0, Mobs.ZOMBIE_KNIGHT)
                            //.add(0, Mobs.EXILED_ZOMBIE)
                            //.add(0, Mobs.SKELETAL_SORCERER)
                            //.add(0, Mobs.EXILED_ZOMBIE_LAVA)
                            //.add(0, Mobs.RIFT_WALKER)
                            // forgotten
                            //.add(0, Mobs.FORGOTTEN_ZOMBIE)
                    )
                    .add(10, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.BOLTARO)
                    )
                    .add(11, new SimpleWave(10, 10 * SECOND, null)
                                    //basic
                                    .add(0.6, Mob.ZOMBIE_LANCER)
                                    .add(0.15, Mob.ZOMBIE_LAMENT)
                                    //.add(0, Mobs.BASIC_SKELETON)
                                    //.add(0, Mobs.PIG_DISCIPLE)
                                    .add(0.08, Mob.SLIMY_ANOMALY)
                                    .add(0.08, Mob.ARACHNO_VENARI)
                                    //elite
                                    .add(0.25, Mob.ZOMBIE_SWORDSMAN)
                                    //.add(0, Mobs.SKELETAL_WARLOCK)
                                    .add(0.1, Mob.PIG_SHAMAN)
                                    .add(0.04, Mob.ILLUMINATION)
                                    .add(0.06, Mob.GOLEM_APPRENTICE)
                                    .add(0.02, Mob.WITCH_DEACON)
                                    //envoy
                                    .add(0.01, Mob.ZOMBIE_VANGUARD)
                                    //.add(0, Mobs.ENVOY_SKELETON)
                                    .add(0.01, Mob.PIG_ALLEVIATOR)
                                    //void
                                    .add(0.01, Mob.VOID_ZOMBIE)
                                    .add(0.01, Mob.SKELETAL_MESMER)
                                    // exiled
                                    //.add(0, Mobs.ZOMBIE_KNIGHT)
                                    //.add(0, Mobs.EXILED_ZOMBIE)
                                    //.add(0, Mobs.SKELETAL_SORCERER)
                                    //.add(0, Mobs.EXILED_ZOMBIE_LAVA)
                                    .add(0.01, Mob.RIFT_WALKER)
                            // forgotten
                            //.add(0, Mobs.FORGOTTEN_ZOMBIE)
                    )
                    .add(15, new SimpleWave(20, 10 * SECOND, null)
                                    //basic
                                    .add(0.5, Mob.ZOMBIE_LANCER)
                                    .add(0.25, Mob.ZOMBIE_LAMENT)
                                    //.add(0, Mobs.BASIC_SKELETON)
                                    //.add(0, Mobs.PIG_DISCIPLE)
                                    .add(0.08, Mob.SLIMY_ANOMALY)
                                    .add(0.08, Mob.ARACHNO_VENARI)
                                    //elite
                                    .add(0.25, Mob.ZOMBIE_SWORDSMAN)
                                    .add(0.1, Mob.SKELETAL_WARLOCK)
                                    .add(0.1, Mob.PIG_SHAMAN)
                                    .add(0.04, Mob.ILLUMINATION)
                                    .add(0.1, Mob.GOLEM_APPRENTICE)
                                    .add(0.02, Mob.WITCH_DEACON)
                                    //envoy
                                    .add(0.01, Mob.ZOMBIE_VANGUARD)
                                    //.add(0, Mobs.ENVOY_SKELETON)
                                    .add(0.01, Mob.PIG_ALLEVIATOR)
                                    //void
                                    .add(0.02, Mob.VOID_ZOMBIE)
                                    .add(0.02, Mob.SKELETAL_MESMER)
                                    // exiled
                                    //.add(0, Mobs.ZOMBIE_KNIGHT)
                                    //.add(0, Mobs.EXILED_ZOMBIE)
                                    //.add(0, Mobs.SKELETAL_SORCERER)
                                    //.add(0, Mobs.EXILED_ZOMBIE_LAVA)
                                    .add(0.01, Mob.RIFT_WALKER)
                            // forgotten
                            //.add(0, Mobs.FORGOTTEN_ZOMBIE)
                    )
                    .add(20, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.GHOULCALLER)
                    )
                    .add(21, new SimpleWave(25, 10 * SECOND, null)
                            //basic
                            .add(0.2, Mob.ZOMBIE_LANCER)
                            .add(0.35, Mob.ZOMBIE_LAMENT)
                            //.add(0, Mobs.BASIC_SKELETON)
                            //.add(0, Mobs.PIG_DISCIPLE)
                            .add(0.08, Mob.SLIMY_ANOMALY)
                            .add(0.08, Mob.ARACHNO_VENARI)
                            //elite
                            .add(0.4, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.15, Mob.SKELETAL_WARLOCK)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.06, Mob.ILLUMINATION)
                            .add(0.15, Mob.GOLEM_APPRENTICE)
                            .add(0.03, Mob.WITCH_DEACON)
                            //envoy
                            .add(0.01, Mob.ZOMBIE_VANGUARD)
                            //.add(0, Mobs.ENVOY_SKELETON)
                            .add(0.01, Mob.PIG_ALLEVIATOR)
                            //void
                            .add(0.03, Mob.VOID_ZOMBIE)
                            .add(0.03, Mob.SKELETAL_MESMER)
                            // exiled
                            //.add(0, Mobs.ZOMBIE_KNIGHT)
                            .add(0.01, Mob.SCRUPULOUS_ZOMBIE)
                            //.add(0, Mobs.SKELETAL_SORCERER)
                            .add(0.02, Mob.FIRE_SPLITTER)
                            .add(0.04, Mob.RIFT_WALKER)
                            // forgotten
                            .add(0.01, Mob.NIGHTMARE_ZOMBIE)
                    )
                    .add(25, new SimpleWave(25, 10 * SECOND, null)
                            //basic
                            //.add(0, Mobs.ZOMBIE_LANCER)
                            .add(0.5, Mob.ZOMBIE_LAMENT)
                            //.add(0, Mobs.BASIC_SKELETON)
                            //.add(0, Mobs.PIG_DISCIPLE)
                            //.add(0, Mobs.BASIC_SLIME)
                            //.add(0, Mobs.ARACHNO_VENARI)
                            //elite
                            //.add(0, Mobs.ZOMBIE_SWORDSMAN)
                            //.add(0, Mobs.SKELETAL_WARLOCK)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.08, Mob.ILLUMINATION)
                            .add(0.2, Mob.GOLEM_APPRENTICE)
                            .add(0.05, Mob.WITCH_DEACON)
                            //envoy
                            .add(0.01, Mob.ZOMBIE_VANGUARD)
                            //.add(0, Mobs.ENVOY_SKELETON)
                            .add(0.01, Mob.PIG_ALLEVIATOR)
                            //void
                            .add(0.06, Mob.VOID_ZOMBIE)
                            .add(0.1, Mob.SKELETAL_MESMER)
                            // exiled
                            .add(0.02, Mob.ZOMBIE_KNIGHT)
                            .add(0.02, Mob.SCRUPULOUS_ZOMBIE)
                            //.add(0, Mobs.SKELETAL_SORCERER)
                            .add(0.02, Mob.FIRE_SPLITTER)
                            .add(0.06, Mob.RIFT_WALKER)
                            // forgotten
                            .add(0.01, Mob.NIGHTMARE_ZOMBIE)
                    )
                    .add(30, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.NARMER)
                    )
                    .add(31, new SimpleWave(30, 10 * SECOND, null)
                                    //basic
                                    //.add(0, Mobs.ZOMBIE_LANCER)
                                    .add(0.4, Mob.ZOMBIE_LAMENT)
                                    //.add(0, Mobs.BASIC_SKELETON)
                                    //.add(0, Mobs.PIG_DISCIPLE)
                                    //.add(0, Mobs.BASIC_SLIME)
                                    //.add(0, Mobs.ARACHNO_VENARI)
                                    //elite
                                    //.add(0, Mobs.ZOMBIE_SWORDSMAN)
                                    //.add(0, Mobs.SKELETAL_WARLOCK)
                                    .add(0.1, Mob.PIG_SHAMAN)
                                    .add(0.08, Mob.ILLUMINATION)
                                    .add(0.2, Mob.GOLEM_APPRENTICE)
                                    .add(0.05, Mob.WITCH_DEACON)
                                    //envoy
                                    .add(0.01, Mob.ZOMBIE_VANGUARD)
                                    //.add(0, Mobs.ENVOY_SKELETON)
                                    .add(0.01, Mob.PIG_ALLEVIATOR)
                                    //void
                                    .add(0.06, Mob.VOID_ZOMBIE)
                                    .add(0.1, Mob.SKELETAL_MESMER)
                                    // exiled
                                    .add(0.03, Mob.ZOMBIE_KNIGHT)
                                    .add(0.03, Mob.SCRUPULOUS_ZOMBIE)
                                    //.add(0, Mobs.SKELETAL_SORCERER)
                                    .add(0.03, Mob.FIRE_SPLITTER)
                                    .add(0.03, Mob.RIFT_WALKER)
                            // forgotten
                            //.add(0, Mobs.FORGOTTEN_ZOMBIE)
                    )
                    .add(35, new SimpleWave(30, 10 * SECOND, null)
                            //basic
                            //.add(0, Mobs.ZOMBIE_LANCER)
                            .add(0.4, Mob.ZOMBIE_LAMENT)
                            //.add(0, Mobs.BASIC_SKELETON)
                            //.add(0, Mobs.PIG_DISCIPLE)
                            //.add(0, Mobs.BASIC_SLIME)
                            //.add(0, Mobs.ARACHNO_VENARI)
                            //elite
                            //.add(0, Mobs.ZOMBIE_SWORDSMAN)
                            //.add(0, Mobs.SKELETAL_WARLOCK)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.08, Mob.ILLUMINATION)
                            .add(0.2, Mob.GOLEM_APPRENTICE)
                            .add(0.05, Mob.WITCH_DEACON)
                            //envoy
                            .add(0.1, Mob.ZOMBIE_VANGUARD)
                            //.add(0, Mobs.ENVOY_SKELETON)
                            .add(0.03, Mob.PIG_ALLEVIATOR)
                            //void
                            .add(0.06, Mob.VOID_ZOMBIE)
                            .add(0.1, Mob.SKELETAL_MESMER)
                            // exiled
                            .add(0.03, Mob.ZOMBIE_KNIGHT)
                            .add(0.03, Mob.SCRUPULOUS_ZOMBIE)
                            //.add(0, Mobs.SKELETAL_SORCERER)
                            .add(0.04, Mob.FIRE_SPLITTER)
                            .add(0.04, Mob.RIFT_WALKER)
                            // forgotten
                            .add(0.02, Mob.NIGHTMARE_ZOMBIE)
                    )
                    .add(40, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.MITHRA)
                    )
                    .add(41, new SimpleWave(35, 10 * SECOND, null)
                            //basic
                            //.add(0, Mobs.ZOMBIE_LANCER)
                            .add(0.3, Mob.ZOMBIE_LAMENT)
                            //.add(0, Mobs.BASIC_SKELETON)
                            //.add(0, Mobs.PIG_DISCIPLE)
                            //.add(0, Mobs.BASIC_SLIME)
                            //.add(0, Mobs.ARACHNO_VENARI)
                            //elite
                            //.add(0, Mobs.ZOMBIE_SWORDSMAN)
                            //.add(0, Mobs.SKELETAL_WARLOCK)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.08, Mob.ILLUMINATION)
                            .add(0.1, Mob.GOLEM_APPRENTICE)
                            .add(0.05, Mob.WITCH_DEACON)
                            //envoy
                            .add(0.15, Mob.ZOMBIE_VANGUARD)
                            //.add(0, Mobs.ENVOY_SKELETON)
                            .add(0.03, Mob.PIG_ALLEVIATOR)
                            //void
                            .add(0.06, Mob.VOID_ZOMBIE)
                            .add(0.1, Mob.SKELETAL_MESMER)
                            // exiled
                            .add(0.06, Mob.ZOMBIE_KNIGHT)
                            .add(0.06, Mob.SCRUPULOUS_ZOMBIE)
                            .add(0.04, Mob.SKELETAL_SORCERER)
                            .add(0.08, Mob.FIRE_SPLITTER)
                            .add(0.08, Mob.RIFT_WALKER)
                            // forgotten
                            .add(0.02, Mob.NIGHTMARE_ZOMBIE)
                    )
                    .add(45, new SimpleWave(35, 10 * SECOND, null)
                            //basic
                            //.add(0, Mobs.ZOMBIE_LANCER)
                            .add(0.2, Mob.ZOMBIE_LAMENT)
                            //.add(0, Mobs.BASIC_SKELETON)
                            //.add(0, Mobs.PIG_DISCIPLE)
                            //.add(0, Mobs.BASIC_SLIME)
                            //.add(0, Mobs.ARACHNO_VENARI)
                            //elite
                            //.add(0, Mobs.ZOMBIE_SWORDSMAN)
                            //.add(0, Mobs.SKELETAL_WARLOCK)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.08, Mob.ILLUMINATION)
                            .add(0.1, Mob.GOLEM_APPRENTICE)
                            .add(0.05, Mob.WITCH_DEACON)
                            //envoy
                            .add(0.3, Mob.ZOMBIE_VANGUARD)
                            .add(0.02, Mob.SKELETAL_ENTROPY)
                            .add(0.05, Mob.PIG_ALLEVIATOR)
                            //void
                            .add(0.06, Mob.VOID_ZOMBIE)
                            .add(0.12, Mob.SKELETAL_MESMER)
                            // exiled
                            .add(0.08, Mob.ZOMBIE_KNIGHT)
                            .add(0.06, Mob.SCRUPULOUS_ZOMBIE)
                            .add(0.04, Mob.SKELETAL_SORCERER)
                            .add(0.08, Mob.FIRE_SPLITTER)
                            .add(0.08, Mob.RIFT_WALKER)
                            // forgotten
                            .add(0.02, Mob.NIGHTMARE_ZOMBIE)
                    )
                    .add(50, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.ZENITH)
                    )
                    .add(51, new SimpleWave(35, 5 * SECOND, null)
                            //basic
                            //.add(0, Mobs.ZOMBIE_LANCER)
                            //.add(0, Mobs.ZOMBIE_LAMENT)
                            //.add(0, Mobs.BASIC_SKELETON)
                            //.add(0, Mobs.PIG_DISCIPLE)
                            //.add(0, Mobs.BASIC_SLIME)
                            //.add(0, Mobs.ARACHNO_VENARI)
                            //elite
                            //.add(0, Mobs.ZOMBIE_SWORDSMAN)
                            //.add(0, Mobs.SKELETAL_WARLOCK)
                            //.add(0, Mobs.PIG_SHAMAN)
                            .add(0.08, Mob.ILLUMINATION)
                            .add(0.12, Mob.GOLEM_APPRENTICE)
                            .add(0.05, Mob.WITCH_DEACON)
                            //envoy
                            .add(0.3, Mob.ZOMBIE_VANGUARD)
                            //.add(0, Mobs.ENVOY_SKELETON)
                            //.add(0, Mobs.PIG_ALLEVIATOR)
                            //void
                            .add(0.08, Mob.VOID_ZOMBIE)
                            .add(0.12, Mob.SKELETAL_MESMER)
                            .add(0.06, Mob.PIG_PARTICLE)
                            // exiled
                            .add(0.2, Mob.ZOMBIE_KNIGHT)
                            .add(0.2, Mob.SCRUPULOUS_ZOMBIE)
                            .add(0.1, Mob.SKELETAL_SORCERER)
                            .add(0.08, Mob.FIRE_SPLITTER)
                            .add(0.08, Mob.RIFT_WALKER)
                            // forgotten
                            .add(0.02, Mob.NIGHTMARE_ZOMBIE)
                            .add(0.01, Mob.CELESTIAL_BOW_WIELDER)
                            .add(0.01, Mob.CELESTIAL_SWORD_WIELDER)
                            .add(0.01, Mob.CELESTIAL_OPUS)
                            .add(0.001, Mob.CREEPY_BOMBER)
                    )
                    .add(55, new SimpleWave(35, 5 * SECOND, null)
                            //basic
                            //.add(0, Mobs.ZOMBIE_LANCER)
                            //.add(0, Mobs.ZOMBIE_LAMENT)
                            //.add(0, Mobs.BASIC_SKELETON)
                            //.add(0, Mobs.PIG_DISCIPLE)
                            //.add(0, Mobs.BASIC_SLIME)
                            //.add(0, Mobs.ARACHNO_VENARI)
                            //elite
                            //.add(0, Mobs.ZOMBIE_SWORDSMAN)
                            //.add(0, Mobs.SKELETAL_WARLOCK)
                            //.add(0, Mobs.PIG_SHAMAN)
                            .add(0.08, Mob.ILLUMINATION)
                            .add(0.15, Mob.GOLEM_APPRENTICE)
                            .add(0.05, Mob.WITCH_DEACON)
                            //envoy
                            .add(0.2, Mob.ZOMBIE_VANGUARD)
                            //.add(0, Mobs.ENVOY_SKELETON)
                            //.add(0, Mobs.PIG_ALLEVIATOR)
                            //void
                            .add(0.08, Mob.VOID_ZOMBIE)
                            .add(0.16, Mob.SKELETAL_MESMER)
                            .add(0.06, Mob.PIG_PARTICLE)
                            // exiled
                            .add(0.2, Mob.ZOMBIE_KNIGHT)
                            .add(0.2, Mob.SCRUPULOUS_ZOMBIE)
                            .add(0.1, Mob.SKELETAL_SORCERER)
                            .add(0.08, Mob.FIRE_SPLITTER)
                            .add(0.08, Mob.RIFT_WALKER)
                            // forgotten
                            .add(0.02, Mob.NIGHTMARE_ZOMBIE)
                            .add(0.05, Mob.OVERGROWN_ZOMBIE)
                            .add(0.01, Mob.CELESTIAL_BOW_WIELDER)
                            .add(0.01, Mob.CELESTIAL_SWORD_WIELDER)
                            .add(0.01, Mob.CELESTIAL_OPUS)
                            .add(0.002, Mob.CREEPY_BOMBER)
                    )
                    .add(60, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.CHESSKING)
                    )
                    .add(61, new SimpleWave(40, 5 * SECOND, null)
                            //basic
                            //.add(0, Mobs.ZOMBIE_LANCER)
                            //.add(0, Mobs.ZOMBIE_LAMENT)
                            //.add(0, Mobs.BASIC_SKELETON)
                            //.add(0, Mobs.PIG_DISCIPLE)
                            //.add(0, Mobs.BASIC_SLIME)
                            //.add(0, Mobs.ARACHNO_VENARI)
                            //elite
                            //.add(0, Mobs.ZOMBIE_SWORDSMAN)
                            //.add(0, Mobs.SKELETAL_WARLOCK)
                            //.add(0, Mobs.PIG_SHAMAN)
                            .add(0.08, Mob.ILLUMINATION)
                            .add(0.15, Mob.GOLEM_APPRENTICE)
                            .add(0.05, Mob.WITCH_DEACON)
                            //envoy
                            .add(0.1, Mob.ZOMBIE_VANGUARD)
                            //.add(0, Mobs.ENVOY_SKELETON)
                            //.add(0, Mobs.PIG_ALLEVIATOR)
                            .add(0.1, Mob.SLIME_GUARD)
                            //void
                            .add(0.08, Mob.VOID_ZOMBIE)
                            .add(0.16, Mob.SKELETAL_MESMER)
                            .add(0.06, Mob.PIG_PARTICLE)
                            // exiled
                            .add(0.2, Mob.ZOMBIE_KNIGHT)
                            .add(0.1, Mob.SCRUPULOUS_ZOMBIE)
                            .add(0.1, Mob.SKELETAL_SORCERER)
                            .add(0.08, Mob.FIRE_SPLITTER)
                            .add(0.08, Mob.RIFT_WALKER)
                            // forgotten
                            .add(0.03, Mob.NIGHTMARE_ZOMBIE)
                            .add(0.07, Mob.OVERGROWN_ZOMBIE)
                            .add(0.01, Mob.CELESTIAL_BOW_WIELDER)
                            .add(0.01, Mob.CELESTIAL_SWORD_WIELDER)
                            .add(0.01, Mob.CELESTIAL_OPUS)
                            .add(0.001, Mob.CREEPY_BOMBER)
                    )
                    .add(65, new SimpleWave(40, 5 * SECOND, null)
                            //basic
                            //.add(0, Mobs.ZOMBIE_LANCER)
                            //.add(0, Mobs.ZOMBIE_LAMENT)
                            //.add(0, Mobs.BASIC_SKELETON)
                            //.add(0, Mobs.PIG_DISCIPLE)
                            .add(0.05, Mob.SLIMY_ANOMALY)
                            //.add(0, Mobs.ARACHNO_VENARI)
                            //elite
                            //.add(0, Mobs.ZOMBIE_SWORDSMAN)
                            //.add(0, Mobs.SKELETAL_WARLOCK)
                            //.add(0, Mobs.PIG_SHAMAN)
                            .add(0.08, Mob.ILLUMINATION)
                            .add(0.15, Mob.GOLEM_APPRENTICE)
                            .add(0.05, Mob.WITCH_DEACON)
                            //envoy
                            //.add(0, Mobs.ZOMBIE_VANGUARD)
                            //.add(0, Mobs.ENVOY_SKELETON)
                            //.add(0, Mobs.PIG_ALLEVIATOR)
                            .add(0.1, Mob.SLIME_GUARD)
                            //void
                            .add(0.08, Mob.VOID_ZOMBIE)
                            .add(0.16, Mob.SKELETAL_MESMER)
                            .add(0.06, Mob.PIG_PARTICLE)
                            // exiled
                            .add(0.15, Mob.ZOMBIE_KNIGHT)
                            .add(0.1, Mob.SCRUPULOUS_ZOMBIE)
                            .add(0.12, Mob.SKELETAL_SORCERER)
                            .add(0.12, Mob.FIRE_SPLITTER)
                            .add(0.12, Mob.RIFT_WALKER)
                            // forgotten
                            .add(0.03, Mob.NIGHTMARE_ZOMBIE)
                            .add(0.1, Mob.OVERGROWN_ZOMBIE)
                            .add(0.01, Mob.CELESTIAL_BOW_WIELDER)
                            .add(0.01, Mob.CELESTIAL_SWORD_WIELDER)
                            .add(0.01, Mob.CELESTIAL_OPUS)
                            .add(0.001, Mob.CREEPY_BOMBER)
                    )
                    .add(70, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.ILLUMINA)
                    )
                    .add(71, new SimpleWave(45, 5 * SECOND, null)
                            //basic
                            //.add(0, Mobs.ZOMBIE_LANCER)
                            //.add(0, Mobs.ZOMBIE_LAMENT)
                            //.add(0, Mobs.BASIC_SKELETON)
                            //.add(0, Mobs.PIG_DISCIPLE)
                            //.add(0, Mobs.BASIC_SLIME)
                            //.add(0, Mobs.ARACHNO_VENARI)
                            //elite
                            //.add(0, Mobs.ZOMBIE_SWORDSMAN)
                            //.add(0, Mobs.SKELETAL_WARLOCK)
                            //.add(0, Mobs.PIG_SHAMAN)
                            .add(0.1, Mob.ILLUMINATION)
                            .add(0.15, Mob.GOLEM_APPRENTICE)
                            .add(0.1, Mob.WITCH_DEACON)
                            //envoy
                            //.add(0, Mobs.ZOMBIE_VANGUARD)
                            //.add(0, Mobs.ENVOY_SKELETON)
                            //.add(0, Mobs.PIG_ALLEVIATOR)
                            //void
                            .add(0.2, Mob.VOID_ZOMBIE)
                            .add(0.2, Mob.SKELETAL_MESMER)
                            .add(0.06, Mob.PIG_PARTICLE)
                            .add(0.01, Mob.SLIMY_CHESS)
                            // exiled
                            .add(0.1, Mob.ZOMBIE_KNIGHT)
                            .add(0.1, Mob.SCRUPULOUS_ZOMBIE)
                            .add(0.1, Mob.SKELETAL_SORCERER)
                            .add(0.2, Mob.FIRE_SPLITTER)
                            .add(0.2, Mob.RIFT_WALKER)
                            // forgotten
                            .add(0.05, Mob.NIGHTMARE_ZOMBIE)
                            .add(0.4, Mob.OVERGROWN_ZOMBIE)
                            .add(0.01, Mob.CELESTIAL_BOW_WIELDER)
                            .add(0.01, Mob.CELESTIAL_SWORD_WIELDER)
                            .add(0.01, Mob.CELESTIAL_OPUS)
                            .add(0.002, Mob.CREEPY_BOMBER)
                    )
                    .add(75, new SimpleWave(45, 5 * SECOND, null)
                            //basic
                            //.add(0, Mobs.ZOMBIE_LANCER)
                            //.add(0, Mobs.ZOMBIE_LAMENT)
                            //.add(0, Mobs.BASIC_SKELETON)
                            //.add(0, Mobs.PIG_DISCIPLE)
                            //.add(0, Mobs.BASIC_SLIME)
                            //.add(0, Mobs.ARACHNO_VENARI)
                            //elite
                            //.add(0, Mobs.ZOMBIE_SWORDSMAN)
                            //.add(0, Mobs.SKELETAL_WARLOCK)
                            //.add(0, Mobs.PIG_SHAMAN)
                            .add(0.1, Mob.ILLUMINATION)
                            .add(0.15, Mob.GOLEM_APPRENTICE)
                            .add(0.1, Mob.WITCH_DEACON)
                            //envoy
                            //.add(0, Mobs.ZOMBIE_VANGUARD)
                            //.add(0, Mobs.ENVOY_SKELETON)
                            //.add(0, Mobs.PIG_ALLEVIATOR)
                            .add(0.3, Mob.SLIME_GUARD)
                            //void
                            .add(0.2, Mob.VOID_ZOMBIE)
                            .add(0.2, Mob.SKELETAL_MESMER)
                            .add(0.06, Mob.PIG_PARTICLE)
                            .add(0.02, Mob.SLIMY_CHESS)
                            // exiled
                            .add(0.1, Mob.ZOMBIE_KNIGHT)
                            .add(0.1, Mob.SCRUPULOUS_ZOMBIE)
                            .add(0.3, Mob.SKELETAL_SORCERER)
                            //.add(0, Mobs.EXILED_ZOMBIE_LAVA)
                            .add(0.25, Mob.RIFT_WALKER)
                            // forgotten
                            .add(0.05, Mob.NIGHTMARE_ZOMBIE)
                            .add(0.5, Mob.OVERGROWN_ZOMBIE)
                            .add(0.01, Mob.CELESTIAL_BOW_WIELDER)
                            .add(0.01, Mob.CELESTIAL_SWORD_WIELDER)
                            .add(0.02, Mob.CELESTIAL_OPUS)
                    )
                    .add(80, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.MAGMATIC_OOZE)
                    )
                    .add(81, new SimpleWave(50, 5 * SECOND, null)
                            //basic
                            //.add(0, Mobs.ZOMBIE_LANCER)
                            //.add(0, Mobs.ZOMBIE_LAMENT)
                            //.add(0, Mobs.BASIC_SKELETON)
                            //.add(0, Mobs.PIG_DISCIPLE)
                            //.add(0, Mobs.BASIC_SLIME)
                            //.add(0, Mobs.ARACHNO_VENARI)
                            //elite
                            //.add(0, Mobs.ZOMBIE_SWORDSMAN)
                            //.add(0, Mobs.SKELETAL_WARLOCK)
                            //.add(0, Mobs.PIG_SHAMAN)
                            .add(0.1, Mob.ILLUMINATION)
                            .add(0.15, Mob.GOLEM_APPRENTICE)
                            .add(0.1, Mob.WITCH_DEACON)
                            //envoy
                            //.add(0, Mobs.ZOMBIE_VANGUARD)
                            //.add(0, Mobs.ENVOY_SKELETON)
                            //.add(0, Mobs.PIG_ALLEVIATOR)
                            .add(0.5, Mob.SLIME_GUARD)
                            //void
                            .add(0.2, Mob.VOID_ZOMBIE)
                            .add(0.3, Mob.SKELETAL_MESMER)
                            .add(0.06, Mob.PIG_PARTICLE)
                            .add(0.02, Mob.SLIMY_CHESS)
                            // exiled
                            .add(0.1, Mob.ZOMBIE_KNIGHT)
                            .add(0.2, Mob.SCRUPULOUS_ZOMBIE)
                            .add(0.3, Mob.SKELETAL_SORCERER)
                            .add(0.25, Mob.FIRE_SPLITTER)
                            //.add(0, Mobs.RIFT_WALKER)
                            // forgotten
                            .add(0.05, Mob.NIGHTMARE_ZOMBIE)
                            .add(0.5, Mob.OVERGROWN_ZOMBIE)
                            .add(0.01, Mob.CELESTIAL_BOW_WIELDER)
                            .add(0.01, Mob.CELESTIAL_SWORD_WIELDER)
                            .add(0.01, Mob.CELESTIAL_OPUS)
                            .add(0.003, Mob.CREEPY_BOMBER)
                    )
                    .add(85, new SimpleWave(50, 5 * SECOND, null)
                            //basic
                            //.add(0, Mobs.ZOMBIE_LANCER)
                            //.add(0, Mobs.ZOMBIE_LAMENT)
                            //.add(0, Mobs.BASIC_SKELETON)
                            //.add(0, Mobs.PIG_DISCIPLE)
                            //.add(0, Mobs.BASIC_SLIME)
                            //.add(0, Mobs.ARACHNO_VENARI)
                            //elite
                            //.add(0, Mobs.ZOMBIE_SWORDSMAN)
                            //.add(0, Mobs.SKELETAL_WARLOCK)
                            //.add(0, Mobs.PIG_SHAMAN)
                            .add(0.1, Mob.ILLUMINATION)
                            .add(0.15, Mob.GOLEM_APPRENTICE)
                            .add(0.1, Mob.WITCH_DEACON)
                            //envoy
                            //.add(0, Mobs.ZOMBIE_VANGUARD)
                            //.add(0, Mobs.ENVOY_SKELETON)
                            //.add(0, Mobs.PIG_ALLEVIATOR)
                            .add(0.7, Mob.SLIME_GUARD)
                            //void
                            .add(0.1, Mob.VOID_ZOMBIE)
                            .add(0.2, Mob.SKELETAL_MESMER)
                            .add(0.04, Mob.PIG_PARTICLE)
                            .add(0.04, Mob.SLIMY_CHESS)
                            // exiled
                            .add(0.05, Mob.ZOMBIE_KNIGHT)
                            .add(0.2, Mob.SCRUPULOUS_ZOMBIE)
                            .add(0.3, Mob.SKELETAL_SORCERER)
                            .add(0.25, Mob.FIRE_SPLITTER)
                            //.add(0, Mobs.RIFT_WALKER)
                            // forgotten
                            .add(0.05, Mob.NIGHTMARE_ZOMBIE)
                            .add(0.3, Mob.OVERGROWN_ZOMBIE)
                            .add(0.01, Mob.CELESTIAL_BOW_WIELDER)
                            .add(0.01, Mob.CELESTIAL_SWORD_WIELDER)
                            .add(0.01, Mob.CELESTIAL_OPUS)
                            .add(0.003, Mob.CREEPY_BOMBER)
                    )
                    .add(90, new SimpleWave(2, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.MITHRA)
                    )
                    .add(91, new SimpleWave(50, 5 * SECOND, null)
                            //basic
                            //.add(0, Mobs.ZOMBIE_LANCER)
                            //.add(0, Mobs.ZOMBIE_LAMENT)
                            //.add(0, Mobs.BASIC_SKELETON)
                            //.add(0, Mobs.PIG_DISCIPLE)
                            //.add(0, Mobs.BASIC_SLIME)
                            //.add(0, Mobs.ARACHNO_VENARI)
                            //elite
                            //.add(0, Mobs.ZOMBIE_SWORDSMAN)
                            //.add(0, Mobs.SKELETAL_WARLOCK)
                            //.add(0, Mobs.PIG_SHAMAN)
                            .add(0.1, Mob.ILLUMINATION)
                            .add(0.25, Mob.GOLEM_APPRENTICE)
                            .add(0.1, Mob.WITCH_DEACON)
                            //envoy
                            //.add(0, Mobs.ZOMBIE_VANGUARD)
                            //.add(0, Mobs.ENVOY_SKELETON)
                            //.add(0, Mobs.PIG_ALLEVIATOR)
                            .add(0.5, Mob.SLIME_GUARD)
                            //void
                            .add(0.1, Mob.VOID_ZOMBIE)
                            .add(0.2, Mob.SKELETAL_MESMER)
                            .add(0.06, Mob.PIG_PARTICLE)
                            .add(0.08, Mob.SLIMY_CHESS)
                            // exiled
                            .add(0.08, Mob.ZOMBIE_KNIGHT)
                            .add(0.2, Mob.SCRUPULOUS_ZOMBIE)
                            .add(0.3, Mob.SKELETAL_SORCERER)
                            //.add(0, Mobs.EXILED_ZOMBIE_LAVA)
                            .add(0.25, Mob.RIFT_WALKER)
                            // forgotten
                            .add(0.05, Mob.NIGHTMARE_ZOMBIE)
                            .add(0.4, Mob.OVERGROWN_ZOMBIE)
                            .add(0.01, Mob.CELESTIAL_BOW_WIELDER)
                            .add(0.01, Mob.CELESTIAL_SWORD_WIELDER)
                            .add(0.01, Mob.CELESTIAL_OPUS)
                            .add(0.004, Mob.CREEPY_BOMBER)
                    )
                    .add(95, new SimpleWave(50, 5 * SECOND, null)
                            //basic
                            //.add(0, Mobs.ZOMBIE_LANCER)
                            //.add(0, Mobs.ZOMBIE_LAMENT)
                            //.add(0, Mobs.BASIC_SKELETON)
                            //.add(0, Mobs.PIG_DISCIPLE)
                            //.add(0, Mobs.BASIC_SLIME)
                            //.add(0, Mobs.ARACHNO_VENARI)
                            //elite
                            //.add(0, Mobs.ZOMBIE_SWORDSMAN)
                            //.add(0, Mobs.SKELETAL_WARLOCK)
                            //.add(0, Mobs.PIG_SHAMAN)
                            .add(0.1, Mob.ILLUMINATION)
                            .add(0.25, Mob.GOLEM_APPRENTICE)
                            .add(0.1, Mob.WITCH_DEACON)
                            //envoy
                            //.add(0, Mobs.ZOMBIE_VANGUARD)
                            //.add(0, Mobs.ENVOY_SKELETON)
                            //.add(0, Mobs.PIG_ALLEVIATOR)
                            .add(0.5, Mob.SLIME_GUARD)
                            //void
                            .add(0.1, Mob.VOID_ZOMBIE)
                            .add(0.2, Mob.SKELETAL_MESMER)
                            .add(0.04, Mob.PIG_PARTICLE)
                            .add(0.06, Mob.SLIMY_CHESS)
                            // exiled
                            .add(0.08, Mob.ZOMBIE_KNIGHT)
                            .add(0.2, Mob.SCRUPULOUS_ZOMBIE)
                            .add(0.3, Mob.SKELETAL_SORCERER)
                            //.add(0, Mobs.EXILED_ZOMBIE_LAVA)
                            .add(0.25, Mob.RIFT_WALKER)
                            // forgotten
                            .add(0.05, Mob.NIGHTMARE_ZOMBIE)
                            .add(0.5, Mob.OVERGROWN_ZOMBIE)
                            .add(0.01, Mob.CELESTIAL_BOW_WIELDER)
                            .add(0.01, Mob.CELESTIAL_SWORD_WIELDER)
                            .add(0.01, Mob.CELESTIAL_OPUS)
                            .add(0.004, Mob.CREEPY_BOMBER)
                    )
                    .add(100, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.VOID)
                    )
                    .add(101, new SimpleWave(80, 2 * SECOND, null)
                                    //basic
                                    //.add(0, Mobs.ZOMBIE_LANCER)
                                    //.add(0, Mobs.ZOMBIE_LAMENT)
                                    //.add(0, Mobs.BASIC_SKELETON)
                                    //.add(0, Mobs.PIG_DISCIPLE)
                                    //.add(0, Mobs.BASIC_SLIME)
                                    //.add(0, Mobs.ARACHNO_VENARI)
                                    //elite
                                    //.add(0, Mobs.ZOMBIE_SWORDSMAN)
                                    //.add(0, Mobs.SKELETAL_WARLOCK)
                                    //.add(0, Mobs.PIG_SHAMAN)
                                    //.add(0, Mobs.MAGMA_CUBE)
                                    //.add(0, Mobs.IRON_GOLEM)
                                    .add(0.1, Mob.WITCH_DEACON)
                                    //envoy
                                    //.add(0, Mobs.ZOMBIE_VANGUARD)
                                    //.add(0, Mobs.ENVOY_SKELETON)
                                    //.add(0, Mobs.PIG_ALLEVIATOR)
                                    //void
                                    .add(1, Mob.VOID_ZOMBIE)
                                    .add(0.5, Mob.SKELETAL_MESMER)
                                    .add(0.05, Mob.PIG_PARTICLE)
                                    // exiled
                                    .add(1, Mob.ZOMBIE_KNIGHT)
                                    //.add(0, Mobs.EXILED_ZOMBIE)
                                    //.add(0, Mobs.SKELETAL_SORCERER)
                                    //.add(0, Mobs.EXILED_ZOMBIE_LAVA)
                                    //.add(0, Mobs.RIFT_WALKER)
                                    // forgotten
                                    .add(0.1, Mob.NIGHTMARE_ZOMBIE)
                                    .add(0.005, Mob.CREEPY_BOMBER)
                            //.add(0, Mobs.FORGOTTEN_LANCER)
                    )
                    .add(102, new SimpleWave(80, 2 * SECOND, null)
                            .add(0.1, Mob.ILLUMINATION)
                            .add(1, Mob.GOLEM_APPRENTICE)
                            .add(0.1, Mob.WITCH_DEACON)
                    )
                    .add(103, new SimpleWave(80, 2 * SECOND, null)
                            .add(0.2, Mob.ARACHNO_VENARI)
                            .add(1, Mob.SKELETAL_MESMER)
                    )
                    .add(104, new SimpleWave(80, 2 * SECOND, null)
                            .add(0.5, Mob.SLIMY_ANOMALY)
                            .add(0.2, Mob.ILLUMINATION)
                            .add(0.2, Mob.SLIMY_CHESS)
                    )
                    .add(105, new SimpleWave(80, 2 * SECOND, null)
                            .add(0.5, Mob.ARACHNO_VENARI)
                    )
                    .add(106, new SimpleWave(20, 2 * SECOND, null)
                            .add(0.5, Mob.CELESTIAL_OPUS)
                    )
                    .add(107, new SimpleWave(5, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.BOLTARO)
                    )
                    .add(108, new SimpleWave(3, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.ILLUMINA)
                    )
                    .add(109, new SimpleWave(3, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.NARMER)
                    )
                    .add(110, new SimpleWave(5, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.GHOULCALLER)
                    )
                    .add(111, new SimpleWave(5, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.ZENITH)
                    )
                    .add(112, new SimpleWave(5, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.MITHRA)
                    )
                    .add(113, new SimpleWave(3, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.CHESSKING)
                    )
                    .add(114, new SimpleWave(3, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.VOID)
                    )
                    .add(115, new SimpleWave(50, 2 * SECOND, null)
                            .add(0.1, Mob.ILLUMINATION)
                            .add(0.5, Mob.GOLEM_APPRENTICE)
                            .add(0.5, Mob.NIGHTMARE_ZOMBIE)
                            .add(0.1, Mob.WITCH_DEACON)
                            .add(0.01, Mob.BOLTARO)
                            .add(0.01, Mob.GHOULCALLER)
                            .add(0.01, Mob.NARMER)
                            .add(0.01, Mob.MITHRA)
                            .add(0.01, Mob.ZENITH)
                            .add(0.005, Mob.CHESSKING)
                            .add(0.005, Mob.MAGMATIC_OOZE)
                            .add(0.005, Mob.ILLUMINA)
                            .add(0.0001, Mob.VOID)
                    )

                    ,

                    DifficultyIndex.ENDLESS
            ));
            options.add(new ItemOption());
            options.add(new CoinGainOption()
                    .guildCoinInsigniaConvertBonus(1500)
            );
            options.add(new ExperienceGainOption()
                    .playerExpPer(80)
                    .guildExpPer(5)
            );

            return options;
        }
    },
    THE_OBSIDIAN_TRAIL_RAID(
            "§c§lThe Obsidian Trail",
            8,
            4,
            60 * SECOND,
            "TheObsidianTrail",
            1,
            GameMode.RAID
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(711.5, 7, 179.5), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(711.5, 7, 179.5), Team.RED).asOption());

            options.add(SpawnpointOption.forTeam(loc.addXYZ(711.5, 7, 179.5), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(711.5, 7, 179.5), Team.RED));

            options.add(new RaidOption());
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld()));

            return options;
        }

    },
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

            options.add(new PowerupOption(loc.addXYZ(713.5, 8.5, 209.5), PowerUp.SELF_DAMAGE, 5, 5));
            options.add(new PowerupOption(loc.addXYZ(710.5, 8.5, 209.5), PowerUp.SELF_HEAL, 5, 5));

            options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 188.5), PowerUp.DAMAGE, 5, 5));
            options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 192.5), PowerUp.ENERGY, 5, 5));
            options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 196.5), PowerUp.SPEED, 5, 5));
            options.add(new PowerupOption(loc.addXYZ(699.5, 8.5, 200.5), PowerUp.HEALING, 5, 5));

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
            3,
            GameMode.TUTORIAL
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(0.5, 2, 0.5), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(0.5, 2, 0.5), Team.RED).asOption());

            options.add(SpawnpointOption.forTeam(loc.addXYZ(0, 2, 0), Team.BLUE));

            //options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

            return options;
        }

    },
    ILLUSION_RIFT_EVENT_1(
            "Combatant’s Cavern",
            4,
            1,
            120 * SECOND,
            "IllusionRiftEvent1",
            1,
            GameMode.EVENT_WAVE_DEFENSE
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TextOption.Type.CHAT_CENTERED.create(
                    Component.text("Boltaro's Lair", NamedTextColor.WHITE, TextDecoration.BOLD),
                    Component.empty(),
                    Component.text("Kill mobs to gain event points!", NamedTextColor.YELLOW, TextDecoration.BOLD),
                    Component.empty()
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    Component.text("GO!", NamedTextColor.GREEN),
                    Component.text("Kill as many mobs as possible!", NamedTextColor.YELLOW)
            ));

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

            options.add(new PowerupOption(loc.addXYZ(16.5, 24.5, 17.5), PowerUp.COOLDOWN, 180, 30));
            options.add(new PowerupOption(loc.addXYZ(-15.5, 24.5, -18.5), PowerUp.HEALING, 90, 30));

            //options.add(new RespawnOption(20));
            options.add(new RespawnWaveOption(2, 1, 20));
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

            options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                    .add(1, new SimpleWave(8, 5 * SECOND, null)
                            .add(0.6, Mob.ZOMBIE_LANCER)
                            .add(0.3, Mob.PIG_DISCIPLE)
                            .add(0.1, Mob.BASIC_WARRIOR_BERSERKER)
                    )
                    .add(2, new SimpleWave(8, SECOND, null)
                            .add(0.5, Mob.ZOMBIE_LANCER)
                            .add(0.3, Mob.PIG_DISCIPLE)
                            .add(0.1, Mob.GOLEM_APPRENTICE)
                            .add(0.1, Mob.BASIC_WARRIOR_BERSERKER)
                    )
                    .add(5, new SimpleWave(1, SECOND, Component.text("Boss"))
                            .add(Mob.BOLTARO)
                    )
                    .add(6, new SimpleWave(12, SECOND, null)
                            .add(0.4, Mob.ZOMBIE_LAMENT)
                            .add(0.3, Mob.PIG_DISCIPLE)
                            .add(0.1, Mob.GOLEM_APPRENTICE)
                            .add(0.1, Mob.BASIC_WARRIOR_BERSERKER)
                            .add(0.05, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                    )
                    .add(8, new SimpleWave(12, SECOND, null)
                            .add(0.4, Mob.ZOMBIE_LAMENT)
                            .add(0.2, Mob.PIG_DISCIPLE)
                            .add(0.1, Mob.GOLEM_APPRENTICE)
                            .add(0.1, Mob.BASIC_WARRIOR_BERSERKER)
                            .add(0.1, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                            .add(0.05, Mob.RIFT_WALKER)
                            .add(0.05, Mob.FIRE_SPLITTER)
                    )
                    .add(10, new SimpleWave(2, SECOND, Component.text("Boss"))
                            .add(Mob.BOLTARO)
                    )
                    .add(11, new SimpleWave(16, SECOND, null)
                            .add(0.3, Mob.ZOMBIE_LAMENT)
                            .add(0.15, Mob.PIG_DISCIPLE)
                            .add(0.1, Mob.GOLEM_APPRENTICE)
                            .add(0.1, Mob.SKELETAL_SORCERER)
                            .add(0.15, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                            .add(0.05, Mob.ADVANCED_WARRIOR_BERSERKER)
                            .add(0.05, Mob.RIFT_WALKER)
                            .add(0.05, Mob.FIRE_SPLITTER)
                            .add(0.05, Mob.NIGHTMARE_ZOMBIE)
                    )
                    .add(15, new SimpleWave(3, SECOND, Component.text("Boss"))
                            .add(Mob.BOLTARO)
                    )
                    .add(16, new SimpleWave(16, SECOND, null)
                            .add(0.2, Mob.ZOMBIE_LAMENT)
                            .add(0.1, Mob.PIG_DISCIPLE)
                            .add(0.2, Mob.GOLEM_APPRENTICE)
                            .add(0.15, Mob.ADVANCED_WARRIOR_BERSERKER)
                            .add(0.2, Mob.SKELETAL_SORCERER)
                            .add(0.1, Mob.RIFT_WALKER)
                            .add(0.1, Mob.FIRE_SPLITTER)
                            .add(0.05, Mob.NIGHTMARE_ZOMBIE)
                    )
                    .add(20, new SimpleWave(4, SECOND, Component.text("Boss"))
                            .add(Mob.BOLTARO)
                    )
                    .add(21, new SimpleWave(16, SECOND, null)
                            .add(0.2, Mob.ZOMBIE_LAMENT)
                            .add(0.1, Mob.PIG_DISCIPLE)
                            .add(0.2, Mob.GOLEM_APPRENTICE)
                            .add(0.15, Mob.ADVANCED_WARRIOR_BERSERKER)
                            .add(0.2, Mob.SKELETAL_SORCERER)
                            .add(0.1, Mob.RIFT_WALKER)
                            .add(0.1, Mob.FIRE_SPLITTER)
                            .add(0.05, Mob.NIGHTMARE_ZOMBIE)
                    )
                    .add(25, new SimpleWave(5, SECOND, Component.text("Boss"))
                            .add(Mob.BOLTARO)
                    )
                    .loop(6, 21, 5)
                    .loop(6, 25, 5)
                    ,
                    DifficultyIndex.EVENT
            ) {

                @Override
                public List<Component> getWaveScoreboard(WarlordsPlayer player) {
                    return Collections.singletonList(Component.text("Event: ").append(Component.text("Boltaro's Lair", NamedTextColor.GREEN)));

                }

                @Override
                public float getSpawnCountMultiplier(int playerCount) {
                    return switch (playerCount) {
                        case 3 -> 1.25f;
                        case 4 -> 1.5f;
                        default -> 1;
                    };
                }
            });
            options.add(new ItemOption());
            options.add(new WinAfterTimeoutOption(600, 50, "spec"));
            options.add(new BoltarosLairOption());
            options.add(new SafeZoneOption());
            options.add(new EventPointsOption()
                    .reduceScoreOnAllDeath(30, Team.BLUE)
                    .onPerWaveClear(1, 500)
                    .onPerWaveClear(5, 2000)
                    .onPerMobKill(Mob.ZOMBIE_LANCER, 5)
                    .onPerMobKill(Mob.PIG_DISCIPLE, 10)
                    .onPerMobKill(Mob.ZOMBIE_LAMENT, 10)
                    .onPerMobKill(Mob.GOLEM_APPRENTICE, 20)
                    .onPerMobKill(Mob.BASIC_WARRIOR_BERSERKER, 10)
                    .onPerMobKill(Mob.INTERMEDIATE_WARRIOR_BERSERKER, 20)
                    .onPerMobKill(Mob.ADVANCED_WARRIOR_BERSERKER, 40)
                    .onPerMobKill(Mob.RIFT_WALKER, 40)
                    .onPerMobKill(Mob.FIRE_SPLITTER, 40)
                    .onPerMobKill(Mob.BOLTARO, 100)
                    .onPerMobKill(Mob.BOLTARO_SHADOW, 100)
                    .onPerMobKill(Mob.BOLTARO_EXLIED, 10)
                    .cap(50_000)
            );
            options.add(new CurrencyOnEventOption()
                    .startWith(120000)
                    .onKill(500)
                    .setPerWaveClear(1, 10000)
            );
            options.add(new CoinGainOption()
                    .guildCoinInsigniaConvertBonus(1000)
                    .guildCoinPerXSec(1, 1)
            );
            options.add(new ExperienceGainOption()
                    .playerExpPer(80)
                    .guildExpPer(10)
            );
            options.add(new FieldEffectOption(options));

            return options;
        }

    },
    ILLUSION_RIFT_EVENT_2(
            "Combatant’s Cavern",
            4,
            1,
            120 * SECOND,
            "IllusionRiftEvent2",
            1,
            GameMode.EVENT_WAVE_DEFENSE
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TextOption.Type.CHAT_CENTERED.create(
                    Component.text("Boltaro Bonanza", NamedTextColor.WHITE, TextDecoration.BOLD),
                    Component.empty(),
                    Component.text("The Boltaros just keep on coming.", NamedTextColor.YELLOW, TextDecoration.BOLD),
                    Component.empty()
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    Component.text("GO!", NamedTextColor.GREEN),
                    Component.text("Kill as many Boltaros as possible!", NamedTextColor.YELLOW)
            ));

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

            options.add(new PowerupOption(loc.addXYZ(16.5, 24.5, 17.5), PowerUp.COOLDOWN, 180, 30));
            options.add(new PowerupOption(loc.addXYZ(-15.5, 24.5, -18.5), PowerUp.HEALING, 90, 30));

            //options.add(new RespawnOption(20));
            options.add(new RespawnWaveOption(2, 1, 20));
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

            options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                    .add(1, new SimpleWave(1, 5 * SECOND, Component.text("Event", NamedTextColor.GREEN))
                            .add(Mob.EVENT_BOLTARO)
                    )
                    .add(2, new SimpleWave(0, 5 * SECOND, null)
                    ),
                    DifficultyIndex.EVENT
            ) {

                @Override
                public List<Component> getWaveScoreboard(WarlordsPlayer player) {
                    return Collections.singletonList(Component.text("Event: ").append(Component.text("Boltaro Bonanza", NamedTextColor.GREEN)));
                }

            });
            options.add(new ItemOption());
            options.add(new WinAfterTimeoutOption(200, 50, "spec"));
            options.add(new SafeZoneOption());
            options.add(new BoltaroBonanzaOption());
            options.add(new EventPointsOption()
                    .onKill(30)
                    .reduceScoreOnAllDeath(30, Team.BLUE)
                    .cap(15_000)
            );
            options.add(new CurrencyOnEventOption()
                    .startWith(120000)
                    .onKill(10000)
            );
            options.add(new CoinGainOption()
                    .noPlayerCoinWavesClearedBonus()
                    .playerCoinPerKill(20)
                    .guildCoinInsigniaConvertBonus(1000)
                    .guildCoinPerXSec(1, 1)
            );
            options.add(new ExperienceGainOption()
                    .playerExpPerXSec(15, 10)
                    .guildExpPerXSec(4, 10)
            );
            options.add(new FieldEffectOption(options));

            return options;
        }

    },
    ILLUSION_RIFT_EVENT_3(
            "Acolyte Archives",
            4,
            1,
            120 * SECOND,
            "IllusionRiftEvent3",
            1,
            GameMode.EVENT_WAVE_DEFENSE
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TextOption.Type.CHAT_CENTERED.create(
                    Component.text("Narmer’s Tomb", NamedTextColor.WHITE, TextDecoration.BOLD),
                    Component.empty(),
                    Component.text("Kill mobs to gain event points!", NamedTextColor.YELLOW, TextDecoration.BOLD),
                    Component.empty()
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    Component.text("GO!", NamedTextColor.GREEN),
                    Component.text("Kill as many mobs as possible!", NamedTextColor.YELLOW)
            ));

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

            options.add(new PowerupOption(loc.addXYZ(16.5, 24.5, 17.5), PowerUp.COOLDOWN, 180, 30));
            options.add(new PowerupOption(loc.addXYZ(-15.5, 24.5, -18.5), PowerUp.HEALING, 90, 30));

            //options.add(new RespawnOption(20));
            options.add(new RespawnWaveOption(2, 1, 20));
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

            options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                    .add(1, new SimpleWave(8, 5 * SECOND, null)
                            .add(0.4, Mob.ZOMBIE_LANCER)
                            .add(0.5, Mob.PIG_DISCIPLE)
                            .add(0.1, Mob.BASIC_WARRIOR_BERSERKER)
                    )
                    .add(2, new SimpleWave(8, SECOND, null)
                            .add(0.4, Mob.ZOMBIE_LANCER)
                            .add(0.4, Mob.PIG_DISCIPLE)
                            .add(0.1, Mob.BASIC_WARRIOR_BERSERKER)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                    )
                    .add(4, new SimpleWave(8, SECOND, null)
                            .add(0.4, Mob.ZOMBIE_LANCER)
                            .add(0.3, Mob.PIG_DISCIPLE)
                            .add(0.1, Mob.BASIC_WARRIOR_BERSERKER)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                            .add(0.1, Mob.GOLEM_APPRENTICE)
                    )
                    .add(5, new SimpleWave(1, SECOND, Component.text("Boss"))
                            .add(Mob.EVENT_NARMER)
                    )
                    .add(6, new SimpleWave(12, SECOND, null)
                            .add(0.4, Mob.ZOMBIE_LANCER)
                            .add(0.3, Mob.PIG_DISCIPLE)
                            .add(0.1, Mob.BASIC_WARRIOR_BERSERKER)
                            .add(0.1, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                            .add(0.05, Mob.ZOMBIE_LAMENT)
                            .add(0.05, Mob.GOLEM_APPRENTICE)
                    )
                    .add(7, new SimpleWave(12, SECOND, null)
                            .add(0.3, Mob.ZOMBIE_LANCER)
                            .add(0.3, Mob.BASIC_WARRIOR_BERSERKER)
                            .add(0.1, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.1, Mob.GOLEM_APPRENTICE)
                    )
                    .add(8, new SimpleWave(12, SECOND, null)
                            .add(0.2, Mob.ZOMBIE_LANCER)
                            .add(0.2, Mob.BASIC_WARRIOR_BERSERKER)
                            .add(0.2, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.1, Mob.GOLEM_APPRENTICE)
                            .add(0.1, Mob.ZOMBIE_SWORDSMAN)
                    )
                    .add(10, new SimpleWave(1, SECOND, Component.text("Boss"))
                            .add(Mob.EVENT_NARMER)
                    )
                    .add(11, new SimpleWave(16, SECOND, null)
                            .add(0.1, Mob.ZOMBIE_LANCER)
                            .add(0.1, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.3, Mob.GOLEM_APPRENTICE)
                            .add(0.2, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.05, Mob.ADVANCED_WARRIOR_BERSERKER)
                            .add(0.05, Mob.ZOMBIE_VANGUARD)
                    )
                    .add(12, new SimpleWave(16, SECOND, null)
                            .add(0.1, Mob.ZOMBIE_LANCER)
                            .add(0.1, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.02, Mob.GOLEM_APPRENTICE)
                            .add(0.01, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.2, Mob.ADVANCED_WARRIOR_BERSERKER)
                            .add(0.1, Mob.ZOMBIE_VANGUARD)
                    )
                    .add(13, new SimpleWave(16, SECOND, null)
                            .add(0.1, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.2, Mob.GOLEM_APPRENTICE)
                            .add(0.1, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.1, Mob.ADVANCED_WARRIOR_BERSERKER)
                            .add(0.05, Mob.ZOMBIE_VANGUARD)
                            .add(0.05, Mob.SKELETAL_SORCERER)
                            .add(0.02, Mob.FIRE_SPLITTER)
                    )
                    .add(15, new SimpleWave(1, SECOND, Component.text("Boss"))
                            .add(Mob.EVENT_NARMER)
                    )
                    .add(16, new SimpleWave(20, SECOND, null)
                            .add(0.2, Mob.ZOMBIE_LAMENT)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.1, Mob.GOLEM_APPRENTICE)
                            .add(0.2, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.05, Mob.ADVANCED_WARRIOR_BERSERKER)
                            .add(0.05, Mob.ZOMBIE_VANGUARD)
                            .add(0.05, Mob.ZOMBIE_KNIGHT)
                            .add(0.05, Mob.OVERGROWN_ZOMBIE)
                            .add(0.1, Mob.SKELETAL_SORCERER)
                            .add(0.1, Mob.FIRE_SPLITTER)
                    )
                    .add(20, new SimpleWave(1, SECOND, Component.text("Boss"))
                            .add(Mob.EVENT_NARMER)
                    )
                    .add(21, new SimpleWave(20, SECOND, null)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.1, Mob.GOLEM_APPRENTICE)
                            .add(0.1, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.1, Mob.ADVANCED_WARRIOR_BERSERKER)
                            .add(0.1, Mob.ZOMBIE_VANGUARD)
                            .add(0.05, Mob.ZOMBIE_KNIGHT)
                            .add(0.05, Mob.OVERGROWN_ZOMBIE)
                            .add(0.2, Mob.SKELETAL_SORCERER)
                            .add(0.05, Mob.FIRE_SPLITTER)
                            .add(0.05, Mob.NIGHTMARE_ZOMBIE)
                    )
                    .add(25, new SimpleWave(1, SECOND, Component.text("Boss"))
                            .add(Mob.EVENT_NARMER)
                    )
                    .loop(6, 21, 5)
                    .loop(6, 25, 5)
                    ,
                    DifficultyIndex.EVENT
            ) {

                @Override
                public List<Component> getWaveScoreboard(WarlordsPlayer player) {
                    return Collections.singletonList(Component.text("Event: ").append(Component.text("Pharaoh's Revenge", NamedTextColor.GREEN)));
                }

                @Override
                public float getSpawnCountMultiplier(int playerCount) {
                    return switch (playerCount) {
                        case 3 -> 1.25f;
                        case 4 -> 1.5f;
                        default -> 1;
                    };
                }
            });
            options.add(new ItemOption());
            options.add(new WinAfterTimeoutOption(600, 50, "spec"));
            options.add(new NarmersTombOption());
            options.add(new SafeZoneOption());
            options.add(new EventPointsOption()
                            .reduceScoreOnAllDeath(30, Team.BLUE)
                            .onPerWaveClear(1, 500)
                            .onPerWaveClear(5, 2000)
                            .onPerMobKill(Mob.ZOMBIE_LANCER, 5)
                            .onPerMobKill(Mob.PIG_DISCIPLE, 10)
                            .onPerMobKill(Mob.BASIC_WARRIOR_BERSERKER, 10)
                            .onPerMobKill(Mob.ZOMBIE_LAMENT, 10)
                            .onPerMobKill(Mob.GOLEM_APPRENTICE, 20)
                            .onPerMobKill(Mob.INTERMEDIATE_WARRIOR_BERSERKER, 20)
                            .onPerMobKill(Mob.PIG_SHAMAN, 20)
                            .onPerMobKill(Mob.ZOMBIE_SWORDSMAN, 25)
                            .onPerMobKill(Mob.ADVANCED_WARRIOR_BERSERKER, 40)
                            .onPerMobKill(Mob.RIFT_WALKER, 40)
                            .onPerMobKill(Mob.ZOMBIE_VANGUARD, 40)
                            .onPerMobKill(Mob.SKELETAL_SORCERER, 40)
                            .onPerMobKill(Mob.FIRE_SPLITTER, 40)
                            .onPerMobKill(Mob.ZOMBIE_KNIGHT, 45)
                            .onPerMobKill(Mob.OVERGROWN_ZOMBIE, 45)
                            .onPerMobKill(Mob.NIGHTMARE_ZOMBIE, 50)
                            .onPerMobKill(Mob.EVENT_NARMER_ACOLYTE, 100)
                            .onPerMobKill(Mob.EVENT_NARMER_DJER, 150)
                            .onPerMobKill(Mob.EVENT_NARMER_DJET, 150)
                            .onPerMobKill(Mob.EVENT_NARMER, 500)
                    //.cap(50_000)
            );
            options.add(new CurrencyOnEventOption()
                    .startWith(120000)
                    .onKill(500)
                    .setPerWaveClear(5, 10000)
            );
            options.add(new CoinGainOption()
                    .clearMobCoinValueAndSet("Narmer's Killed", "Narmer", 100)
                    .guildCoinInsigniaConvertBonus(1000)
                    .guildCoinPerXSec(1, 1)
            );
            options.add(new ExperienceGainOption()
                    .playerExpPerXSec(15, 10)
                    .guildExpPerXSec(1, 60)
            );
            options.add(new FieldEffectOption(options, FieldEffectOption.FieldEffect.CONQUERING_ENERGY));

            return options;
        }

    },
    ILLUSION_RIFT_EVENT_4(
            "Spiders Burrow",
            4,
            1,
            120 * SECOND,
            "IllusionRiftEvent4",
            1,
            GameMode.EVENT_WAVE_DEFENSE
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TextOption.Type.CHAT_CENTERED.create(
                    Component.text("ARACHNO_VENARIs Dwelling", NamedTextColor.WHITE, TextDecoration.BOLD),
                    Component.empty(),
                    Component.text("Kill mobs to gain event points!", NamedTextColor.YELLOW, TextDecoration.BOLD),
                    Component.empty()
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    Component.text("GO!", NamedTextColor.GREEN),
                    Component.text("Kill as many mobs as possible!", NamedTextColor.YELLOW)
            ));

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

            options.add(new PowerupOption(loc.addXYZ(16.5, 24.5, 17.5), PowerUp.COOLDOWN, 180, 30));
            options.add(new PowerupOption(loc.addXYZ(-15.5, 24.5, -18.5), PowerUp.HEALING, 90, 30));

            //options.add(new RespawnOption(20));
            options.add(new RespawnWaveOption(2, 1, 20));
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

            options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                    .add(1, new SimpleWave(8, 5 * SECOND, null)
                            .add(0.4, Mob.ZOMBIE_LANCER)
                            .add(0.5, Mob.PIG_DISCIPLE)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                    )
                    .add(2, new SimpleWave(8, SECOND, null)
                            .add(0.4, Mob.ZOMBIE_LANCER)
                            .add(0.4, Mob.PIG_DISCIPLE)
                            .add(0.1, Mob.ARACHNO_VENARI)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                    )
                    .add(4, new SimpleWave(8, SECOND, null)
                            .add(0.4, Mob.ZOMBIE_LANCER)
                            .add(0.3, Mob.PIG_DISCIPLE)
                            .add(0.2, Mob.ARACHNO_VENARI)
                            .add(0.05, Mob.ZOMBIE_LAMENT)
                            .add(0.05, Mob.PIG_SHAMAN)
                    )
                    .add(5, new SimpleWave(8, SECOND, Component.text("Boss"))
                            .add(0.1, Mob.EVENT_MITHRA_FORSAKEN_FROST)
                            .add(0.2, Mob.EVENT_MITHRA_FORSAKEN_FOLIAGE)
                            .add(0.1, Mob.EVENT_MITHRA_FORSAKEN_SHRIEKER)
                            .add(0.1, Mob.EVENT_MITHRA_FORSAKEN_RESPITE)
                            .add(0.1, Mob.EVENT_MITHRA_FORSAKEN_CRUOR)
                            .add(0.2, Mob.EVENT_MITHRA_FORSAKEN_DEGRADER)
                            .add(0.2, Mob.EVENT_MITHRA_FORSAKEN_APPARITION)
                    )
                    .add(6, new SimpleWave(12, SECOND, null)
                            .add(0.4, Mob.ZOMBIE_LANCER)
                            .add(0.3, Mob.PIG_DISCIPLE)
                            .add(0.2, Mob.ARACHNO_VENARI)
                            .add(0.2, Mob.ZOMBIE_LAMENT)
                            .add(0.05, Mob.PIG_SHAMAN)
                            .add(0.05, Mob.PIG_ALLEVIATOR)
                    )
                    .add(8, new SimpleWave(12, SECOND, null)
                            .add(0.2, Mob.ZOMBIE_LANCER)
                            .add(0.2, Mob.PIG_DISCIPLE)
                            .add(0.3, Mob.ARACHNO_VENARI)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.05, Mob.PIG_ALLEVIATOR)
                            .add(0.05, Mob.ZOMBIE_VANGUARD)
                    )
                    .add(10, new SimpleWave(12, SECOND, Component.text("Boss"))
                            .add(0.2, Mob.EVENT_MITHRA_FORSAKEN_FROST)
                            .add(0.1, Mob.EVENT_MITHRA_FORSAKEN_FOLIAGE)
                            .add(0.2, Mob.EVENT_MITHRA_FORSAKEN_SHRIEKER)
                            .add(0.1, Mob.EVENT_MITHRA_FORSAKEN_RESPITE)
                            .add(0.2, Mob.EVENT_MITHRA_FORSAKEN_CRUOR)
                            .add(0.1, Mob.EVENT_MITHRA_FORSAKEN_DEGRADER)
                            .add(0.1, Mob.EVENT_MITHRA_FORSAKEN_APPARITION)
                    )
                    .add(11, new SimpleWave(16, SECOND, null)
                            .add(0.1, Mob.ZOMBIE_LANCER)
                            .add(0.1, Mob.PIG_DISCIPLE)
                            .add(0.2, Mob.ZOMBIE_LAMENT)
                            .add(0.3, Mob.ARACHNO_VENARI)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.1, Mob.PIG_ALLEVIATOR)
                            .add(0.05, Mob.ZOMBIE_VANGUARD)
                            .add(0.05, Mob.SLIME_GUARD)
                    )
                    .add(13, new SimpleWave(16, SECOND, null)
                            .add(0.1, Mob.ZOMBIE_LANCER)
                            .add(0.1, Mob.PIG_DISCIPLE)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                            .add(0.2, Mob.ARACHNO_VENARI)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.2, Mob.PIG_ALLEVIATOR)
                            .add(0.05, Mob.ZOMBIE_VANGUARD)
                            .add(0.05, Mob.SLIME_GUARD)
                            .add(0.1, Mob.ZOMBIE_SWORDSMAN)
                    )
                    .add(15, new SimpleWave(16, SECOND, Component.text("Boss"))
                            .add(0.2, Mob.EVENT_MITHRA_FORSAKEN_FROST)
                            .add(0.1, Mob.EVENT_MITHRA_FORSAKEN_FOLIAGE)
                            .add(0.1, Mob.EVENT_MITHRA_FORSAKEN_SHRIEKER)
                            .add(0.1, Mob.EVENT_MITHRA_FORSAKEN_RESPITE)
                            .add(0.1, Mob.EVENT_MITHRA_FORSAKEN_CRUOR)
                            .add(0.2, Mob.EVENT_MITHRA_FORSAKEN_DEGRADER)
                            .add(0.2, Mob.EVENT_MITHRA_FORSAKEN_APPARITION)
                    )
                    .add(16, new SimpleWave(20, SECOND, null)
                            .add(0.2, Mob.PIG_DISCIPLE)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                            .add(0.1, Mob.ARACHNO_VENARI)
                            .add(0.2, Mob.PIG_SHAMAN)
                            .add(0.05, Mob.PIG_ALLEVIATOR)
                            .add(0.05, Mob.ZOMBIE_VANGUARD)
                            .add(0.05, Mob.SLIME_GUARD)
                            .add(0.05, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.1, Mob.OVERGROWN_ZOMBIE)
                            .add(0.1, Mob.RIFT_WALKER)
                    )
                    .add(20, new SimpleWave(20, SECOND, Component.text("Boss"))
                            .add(0.1, Mob.EVENT_MITHRA_FORSAKEN_FROST)
                            .add(0.1, Mob.EVENT_MITHRA_FORSAKEN_FOLIAGE)
                            .add(0.1, Mob.EVENT_MITHRA_FORSAKEN_SHRIEKER)
                            .add(0.2, Mob.EVENT_MITHRA_FORSAKEN_RESPITE)
                            .add(0.2, Mob.EVENT_MITHRA_FORSAKEN_CRUOR)
                            .add(0.1, Mob.EVENT_MITHRA_FORSAKEN_DEGRADER)
                            .add(0.2, Mob.EVENT_MITHRA_FORSAKEN_APPARITION)
                    )
                    .add(21, new SimpleWave(24, SECOND, null)
                            .add(0.1, Mob.ZOMBIE_LANCER)
                            .add(0.1, Mob.PIG_DISCIPLE)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                            .add(0.1, Mob.ARACHNO_VENARI)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.1, Mob.PIG_ALLEVIATOR)
                            .add(0.05, Mob.ZOMBIE_VANGUARD)
                            .add(0.05, Mob.SLIME_GUARD)
                            .add(0.2, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.05, Mob.OVERGROWN_ZOMBIE)
                            .add(0.05, Mob.RIFT_WALKER)
                    ).add(25, new SimpleWave(1, SECOND, Component.text("Boss"))
                            .add(Mob.EVENT_MITHRA, new Location(loc.getWorld(), 4.5, 22, -2.5))
                    )
                    .loop(6, 21, 5)
                    .loop(6, 25, 5)
                    ,
                    DifficultyIndex.EVENT
            ) {

                @Override
                public List<Component> getWaveScoreboard(WarlordsPlayer player) {
                    return Collections.singletonList(Component.text("Event: ").append(Component.text("ARACHNO_VENARIs Burrow", NamedTextColor.GREEN)));
                }

                @Override
                public float getSpawnCountMultiplier(int playerCount) {
                    return switch (playerCount) {
                        case 3 -> 1.25f;
                        case 4 -> 1.5f;
                        default -> 1;
                    };
                }
            });
            options.add(new ItemOption());
            options.add(new WinAfterTimeoutOption(600, 50, "spec"));
            options.add(new SpidersDwellingOption());
            options.add(new SafeZoneOption());
            options.add(new EventPointsOption()
                    .reduceScoreOnAllDeath(30, Team.BLUE)
                    .onPerWaveClear(1, 500)
                    .onPerWaveClear(5, 2000)
                    .onPerMobKill(Mob.ZOMBIE_LANCER, 5)
                    .onPerMobKill(Mob.PIG_DISCIPLE, 10)
                    .onPerMobKill(Mob.ZOMBIE_LAMENT, 10)
                    .onPerMobKill(Mob.ARACHNO_VENARI, 10)
                    .onPerMobKill(Mob.PIG_SHAMAN, 20)
                    .onPerMobKill(Mob.PIG_ALLEVIATOR, 20)
                    .onPerMobKill(Mob.ZOMBIE_VANGUARD, 20)
                    .onPerMobKill(Mob.SLIME_GUARD, 25)
                    .onPerMobKill(Mob.ZOMBIE_SWORDSMAN, 30)
                    .onPerMobKill(Mob.OVERGROWN_ZOMBIE, 40)
                    .onPerMobKill(Mob.RIFT_WALKER, 45)
                    .onPerMobKill(Mob.EVENT_MITHRA_FORSAKEN_FROST, 50)
                    .onPerMobKill(Mob.EVENT_MITHRA_FORSAKEN_FOLIAGE, 50)
                    .onPerMobKill(Mob.EVENT_MITHRA_FORSAKEN_SHRIEKER, 50)
                    .onPerMobKill(Mob.EVENT_MITHRA_FORSAKEN_RESPITE, 50)
                    .onPerMobKill(Mob.EVENT_MITHRA_FORSAKEN_CRUOR, 50)
                    .onPerMobKill(Mob.EVENT_MITHRA_FORSAKEN_DEGRADER, 50)
                    .onPerMobKill(Mob.EVENT_MITHRA_FORSAKEN_APPARITION, 50)
                    .onPerMobKill(Mob.EVENT_MITHRA_POISONOUS_SPIDER, 50)
                    .onPerMobKill(Mob.EVENT_MITHRA_EGG_SAC, 150)
                    .onPerMobKill(Mob.EVENT_MITHRA, 500)

            );
            options.add(new CurrencyOnEventOption()
                    .startWith(120000)
                    .onKill(500)
                    .setPerWaveClear(5, 10000)
            );
            options.add(new CoinGainOption()
                    .clearMobCoinValueAndSet("Mithra Killed", "Mithra", 100)
                    .guildCoinInsigniaConvertBonus(1000)
                    .guildCoinPerXSec(1, 1)
            );
            options.add(new ExperienceGainOption()
                    .playerExpPerXSec(15, 10)
                    .guildExpPerXSec(1, 60)
            );
            options.add(new FieldEffectOption(options, FieldEffectOption.FieldEffect.ARACHNOPHOBIA));

            return options;
        }

    },
    ILLUSION_RIFT_EVENT_5(
            "The Borderline of Illusion",
            4,
            1,
            120 * SECOND,
            "IllusionRiftEvent5",
            1,
            GameMode.EVENT_WAVE_DEFENSE
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TextOption.Type.CHAT_CENTERED.create(
                    Component.text(getMapName(), NamedTextColor.WHITE, TextDecoration.BOLD),
                    Component.empty(),
                    Component.text("Kill mobs to gain event points!", NamedTextColor.YELLOW, TextDecoration.BOLD),
                    Component.empty()
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    Component.text("GO!", NamedTextColor.GREEN),
                    Component.text("Kill as many mobs as possible!", NamedTextColor.YELLOW)
            ));

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

            options.add(new PowerupOption(loc.addXYZ(16.5, 24.5, 17.5), PowerUp.COOLDOWN, 180, 30));
            options.add(new PowerupOption(loc.addXYZ(-15.5, 24.5, -18.5), PowerUp.HEALING, 90, 30));

            //options.add(new RespawnOption(20));
            options.add(new RespawnWaveOption(2, 1, 20));
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

            Location bossSpawnLocation = new Location(loc.getWorld(), 0.5, 24.5, -0.5);
            options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                    .add(1, new SimpleWave(16, 8 * SECOND, null)
                            .add(0.4, Mob.ZOMBIE_LAMENT)
                            .add(0.1, Mob.SLIMY_ANOMALY)
                            .add(0.2, Mob.SKELETAL_WARLOCK)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.2, Mob.SLIME_GUARD)
                    )
                    .add(4, new SimpleWave(16, 8 * SECOND, null)
                            .add(0.3, Mob.ZOMBIE_LAMENT)
                            .add(0.1, Mob.ADVANCED_WARRIOR_BERSERKER)
                            .add(0.2, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.2, Mob.OVERGROWN_ZOMBIE)
                            .add(0.1, Mob.FIRE_SPLITTER)
                            .add(0.1, Mob.SKELETAL_MESMER)
                    )
                    .add(9, new SimpleWave(16, 8 * SECOND, null)
                            .add(0.3, Mob.OVERGROWN_ZOMBIE)
                            .add(0.1, Mob.ADVANCED_WARRIOR_BERSERKER)
                            .add(0.1, Mob.SLIMY_ANOMALY)
                            .add(0.1, Mob.FIRE_SPLITTER)
                            .add(0.2, Mob.PIG_SHAMAN)
                            .add(0.2, Mob.ZOMBIE_VANGUARD)
                    )
                    .add(10, new SimpleWave(1, 8 * SECOND, Component.text("Boss"))
                            .add(1, Mob.EVENT_ILLUSION_CORE, bossSpawnLocation)
                    )
                    .add(11, new SimpleWave(15, 8 * SECOND, null)
                            .add(0.2, Mob.OVERGROWN_ZOMBIE)
                            .add(0.1, Mob.ADVANCED_WARRIOR_BERSERKER)
                            .add(0.2, Mob.SLIMY_ANOMALY)
                            .add(0.1, Mob.PIG_ALLEVIATOR)
                            .add(0.2, Mob.ZOMBIE_LAMENT)
                    )
                    .add(14, new SimpleWave(18, 8 * SECOND, null)
                            .add(0.1, Mob.PIG_ALLEVIATOR)
                            .add(0.1, Mob.PIG_DISCIPLE)
                            .add(0.2, Mob.ARACHNO_VENARI)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                            .add(0.2, Mob.PIG_SHAMAN)
                            .add(0.3, Mob.SLIMY_ANOMALY)
                    )
                    .add(19, new SimpleWave(20, 8 * SECOND, null)
                            .add(0.3, Mob.ZOMBIE_LAMENT)
                            .add(0.1, Mob.SKELETAL_SORCERER)
                            .add(0.1, Mob.ARACHNO_VENARI)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.1, Mob.PIG_ALLEVIATOR)
                            .add(0.1, Mob.ZOMBIE_VANGUARD)
                            .add(0.1, Mob.SKELETAL_MESMER)
                    )
                    .add(20, new SimpleWave(1, 8 * SECOND, Component.text("Boss"))
                            .add(1, Mob.EVENT_EXILED_CORE, bossSpawnLocation)
                    )
                    .add(21, new SimpleWave(24, 8 * SECOND, null)
                            .add(0.1, Mob.EXTREME_ZEALOT)
                            .add(0.1, Mob.ZOMBIE_VANGUARD)
                            .add(0.2, Mob.SKELETAL_SORCERER)
                            .add(0.2, Mob.SLIMY_ANOMALY)
                            .add(0.6, Mob.SLIME_GUARD)
                    )
                    .add(22, new SimpleWave(14, 8 * SECOND, null)
                            .add(0.1, Mob.EXTREME_ZEALOT)
                            .add(0.1, Mob.FIRE_SPLITTER)
                            .add(0.2, Mob.RIFT_WALKER)
                            .add(0.2, Mob.SKELETAL_SORCERER)
                            .add(0.1, Mob.ZOMBIE_KNIGHT)
                            .add(0.1, Mob.PIG_ALLEVIATOR)
                            .add(0.1, Mob.ZOMBIE_VANGUARD)
                            .add(0.1, Mob.SKELETAL_ENTROPY)
                    )
                    .add(25, new SimpleWave(18, 8 * SECOND, null)
                            .add(0.05, Mob.ZOMBIE_LANCER)
                            .add(0.05, Mob.PIG_DISCIPLE)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                            .add(0.05, Mob.ARACHNO_VENARI)
                            .add(0.05, Mob.PIG_SHAMAN)
                            .add(0.2, Mob.PIG_ALLEVIATOR)
                            .add(0.05, Mob.ZOMBIE_VANGUARD)
                            .add(0.05, Mob.SLIME_GUARD)
                            .add(0.1, Mob.RIFT_WALKER)
                            .add(0.1, Mob.SKELETAL_SORCERER)
                            .add(0.1, Mob.ADVANCED_WARRIOR_BERSERKER)
                            .add(0.1, Mob.SLIMY_ANOMALY)
                    )
                    .add(29, new SimpleWave(18, 8 * SECOND, null)
                            .add(0.2, Mob.SKELETAL_MESMER)
                            .add(0.3, Mob.OVERGROWN_ZOMBIE)
                            .add(0.1, Mob.RIFT_WALKER)
                            .add(0.1, Mob.FIRE_SPLITTER)
                            .add(0.2, Mob.SKELETAL_ENTROPY)
                            .add(0.1, Mob.SKELETAL_SORCERER)
                    )
                    .add(30, new SimpleWave(1, 8 * SECOND, Component.text("Boss"))
                            .add(1, Mob.EVENT_ILLUMINA, bossSpawnLocation)
                    )
                    .add(31, new SimpleWave(20, 8 * SECOND, null)
                            .add(0.2, Mob.SKELETAL_SORCERER)
                            .add(0.4, Mob.OVERGROWN_ZOMBIE)
                            .add(0.1, Mob.SLIMY_ANOMALY)
                            .add(0.1, Mob.ZOMBIE_KNIGHT)
                            .add(0.1, Mob.FIRE_SPLITTER)
                            .add(0.1, Mob.SLIME_GUARD)
                    )
                    .add(36, new SimpleWave(24, 8 * SECOND, null)
                            .add(0.1, Mob.SKELETAL_MESMER)
                            .add(0.1, Mob.ADVANCED_WARRIOR_BERSERKER)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                            .add(0.1, Mob.ARACHNO_VENARI)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.1, Mob.PIG_ALLEVIATOR)
                            .add(0.05, Mob.RIFT_WALKER)
                            .add(0.05, Mob.ZOMBIE_KNIGHT)
                            .add(0.1, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.1, Mob.OVERGROWN_ZOMBIE)
                            .add(0.1, Mob.SLIMY_ANOMALY)
                    )
                    .add(40, new SimpleWave(1, 8 * SECOND, Component.text("Boss"))
                            .add(1, Mob.EVENT_CALAMITY_CORE, bossSpawnLocation)
                    )
                    .loop(6, 36, 5)
                    .loop(6, 40, 5)
                    ,
                    DifficultyIndex.EVENT
            ) {
                @Override
                public void register(@Nonnull Game game) {
                    super.register(game);
                    game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(SCOREBOARD_PRIORITY - 2, "wave") {
                        @Nonnull
                        @Override
                        public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                            return Collections.singletonList(Component.text("Event: ").append(Component.text(getMapName(), NamedTextColor.GREEN)));
                        }
                    });
                }

                @Override
                public float getSpawnCountMultiplier(int playerCount) {
                    return switch (playerCount) {
                        case 3 -> 1.2f;
                        case 4 -> 1.5f;
                        default -> 1;
                    };
                }

                @Override
                protected void modifyStats(WarlordsNPC warlordsNPC) {
                    warlordsNPC.getMob().onSpawn(this);
                    if (warlordsNPC.getMob() instanceof BossLike) {
                        float scaledHealth = (float) (warlordsNPC.getMaxHealth() * (.0625 * Math.pow(Math.E, 0.69314718056 * playerCount()))); // ln4/2 = 0.69314718056
                        warlordsNPC.setMaxBaseHealth(scaledHealth);
                        warlordsNPC.setMaxHealth(scaledHealth);
                        warlordsNPC.setHealth(scaledHealth);
                        return;
                    }
                    int playerCount = playerCount();
                    int wavesCleared = getWavesCleared();

                    float healthMultiplier;
                    float meleeDamageMultiplier = 1;

                    float waveHealthMultiplier = 0;
                    float waveMeleeDamageMultiplier = 0;
                    switch (playerCount) {
                        case 1, 2 -> healthMultiplier = 1.1f;
                        case 3 -> healthMultiplier = 1.25f;
                        default -> healthMultiplier = 1.40f;
                    }
                    if (wavesCleared >= 10) {
                        waveHealthMultiplier += .05;
                    }
                    if (wavesCleared >= 20) {
                        waveHealthMultiplier += .05;
                        waveMeleeDamageMultiplier += .05;
                    }
                    if (wavesCleared >= 30) {
                        waveMeleeDamageMultiplier += .05;
                    }
                    if (wavesCleared >= 40) {
                        waveHealthMultiplier += .1;
                    }
                    healthMultiplier += waveHealthMultiplier;
                    meleeDamageMultiplier += waveMeleeDamageMultiplier;

                    float maxHealth = warlordsNPC.getMaxHealth();
                    float minMeleeDamage = warlordsNPC.getMinMeleeDamage();
                    float maxMeleeDamage = warlordsNPC.getMaxMeleeDamage();
                    float newHealth = maxHealth * healthMultiplier;
                    warlordsNPC.setMaxBaseHealth(newHealth);
                    warlordsNPC.setMaxHealth(newHealth);
                    warlordsNPC.setHealth(newHealth);
                    warlordsNPC.setMinMeleeDamage((int) (minMeleeDamage * meleeDamageMultiplier));
                    warlordsNPC.setMaxMeleeDamage((int) (maxMeleeDamage * meleeDamageMultiplier));
                }
            });
            options.add(new ItemOption());
            options.add(new WinAfterTimeoutOption(900, 50, "spec"));
            options.add(new TheBorderlineOfIllusionEvent());
            options.add(new SafeZoneOption(1));
            options.add(new EventPointsOption()
                    .reduceScoreOnAllDeath(35, Team.BLUE)
                    .onPerWaveClear(1, 500)
                    .onPerWaveClear(5, 2000)
                    .onPerMobKill(Mob.ZOMBIE_LAMENT, 10)
                    .onPerMobKill(Mob.ARACHNO_VENARI, 10)
                    .onPerMobKill(Mob.SLIMY_ANOMALY, 10)
                    .onPerMobKill(Mob.SKELETAL_WARLOCK, 15)
                    .onPerMobKill(Mob.PIG_SHAMAN, 15)
                    .onPerMobKill(Mob.PIG_ALLEVIATOR, 20)
                    .onPerMobKill(Mob.ZOMBIE_VANGUARD, 20)
                    .onPerMobKill(Mob.SLIME_GUARD, 25)
                    .onPerMobKill(Mob.ZOMBIE_SWORDSMAN, 30)
                    .onPerMobKill(Mob.SKELETAL_MESMER, 35)
                    .onPerMobKill(Mob.ZOMBIE_KNIGHT, 35)
                    .onPerMobKill(Mob.ADVANCED_WARRIOR_BERSERKER, 35)
                    .onPerMobKill(Mob.OVERGROWN_ZOMBIE, 40)
                    .onPerMobKill(Mob.RIFT_WALKER, 45)
                    .onPerMobKill(Mob.EXTREME_ZEALOT, 45)
                    .onPerMobKill(Mob.SKELETAL_SORCERER, 50)
                    .onPerMobKill(Mob.EVENT_ILLUSION_CORE, 2500)
                    .onPerMobKill(Mob.EVENT_EXILED_CORE, 2500)
                    .onPerMobKill(Mob.EVENT_CALAMITY_CORE, 2500)
                    .onPerMobKill(Mob.EVENT_ILLUMINA, 3000)
            );
            options.add(new CurrencyOnEventOption()
                    .startWith(100000)
                    .onKill(500)
                    .setPerWaveClear(5, 25000)
                    .disableGuildBonus()
            );
            options.add(new CoinGainOption()
                    .playerCoinPerXSec(150, 10)
                    .guildCoinInsigniaConvertBonus(1000)
                    .guildCoinPerXSec(1, 1)
                    .disableCoinConversionUpgrade()
            );
            options.add(new ExperienceGainOption()
                    .playerExpPerXSec(10, 10)
                    .guildExpPerXSec(20, 30)
            );
            options.add(new FieldEffectOption(options, FieldEffectOption.FieldEffect.LOST_BUFF, FieldEffectOption.FieldEffect.DUMB_DEBUFFS));

            return options;
        }

    },
    ACROPOLIS(
            "Acropolis",
            4,
            1,
            120 * SECOND,
            "Acropolis",
            3,
            GameMode.EVENT_WAVE_DEFENSE
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TextOption.Type.CHAT_CENTERED.create(
                    Component.text(getMapName(), NamedTextColor.WHITE, TextDecoration.BOLD),
                    Component.empty(),
                    Component.text("Kill mobs to gain event points!", NamedTextColor.YELLOW, TextDecoration.BOLD),
                    Component.empty()
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    Component.text("GO!", NamedTextColor.GREEN),
                    Component.text("Kill as many mobs as possible!", NamedTextColor.YELLOW)
            ));

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(0.5, 23, -2.50), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(0.5, 23, -2.50), Team.RED).asOption());

            options.add(SpawnpointOption.forTeam(loc.addXYZ(0.5, 23, -2.50), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(8.5, 23, 5.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(0.5, 23, 13.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-7.5, 23, 5.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(8.5, 23, -10.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(0.5, 23, -18.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-7.5, 23, -10.5), Team.RED));

            options.add(new PowerupOption(loc.addXYZ(14.5, 24.5, 16.5), PowerUp.COOLDOWN, 180, 30));
            options.add(new PowerupOption(loc.addXYZ(-13.5, 24.5, -23.5), PowerUp.HEALING, 90, 30));

            options.add(new RespawnWaveOption(2, 1, 20));
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

            options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                    .add(1, new SimpleWave(8, 5 * SECOND, null)
                            .add(0.4, Mob.ZOMBIE_LANCER)
                            .add(0.5, Mob.PIG_DISCIPLE)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                    )
                    .add(2, new SimpleWave(8, 5 * SECOND, null)
                            .add(0.4, Mob.ZOMBIE_LANCER)
                            .add(0.4, Mob.PIG_DISCIPLE)
                            .add(0.2, Mob.ZOMBIE_LAMENT)
                    )
                    .add(4, new SimpleWave(8, 5 * SECOND, null)
                            .add(0.4, Mob.ZOMBIE_LANCER)
                            .add(0.3, Mob.PIG_DISCIPLE)
                            .add(0.2, Mob.ARACHNO_VENARI)
                            .add(0.05, Mob.SKELETAL_WARLOCK)
                            .add(0.05, Mob.PIG_SHAMAN)
                    )
                    .add(5, new SimpleWave(1, 5 * SECOND, Component.text("Boss"))
                            .add(1, Mob.EVENT_APOLLO)
                    )
                    .add(6, new SimpleWave(12, 5 * SECOND, null)
                            .add(0.4, Mob.ZOMBIE_LANCER)
                            .add(0.2, Mob.PIG_DISCIPLE)
                            .add(0.2, Mob.ARACHNO_VENARI)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                            .add(0.05, Mob.SKELETAL_WARLOCK)
                            .add(0.05, Mob.PIG_ALLEVIATOR)
                    )
                    .add(8, new SimpleWave(12, 5 * SECOND, null)
                            .add(0.2, Mob.ZOMBIE_LANCER)
                            .add(0.2, Mob.PIG_DISCIPLE)
                            .add(0.2, Mob.ARACHNO_VENARI)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                            .add(0.2, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                            .add(0.05, Mob.PIG_ALLEVIATOR)
                            .add(0.05, Mob.ZOMBIE_VANGUARD)
                    )
                    .add(10, new SimpleWave(1, 5 * SECOND, Component.text("Boss"))
                            .add(1, Mob.EVENT_ARES)
                    )
                    .add(11, new SimpleWave(16, 5 * SECOND, null)
                            .add(0.1, Mob.ZOMBIE_LANCER)
                            .add(0.1, Mob.PIG_DISCIPLE)
                            .add(0.2, Mob.ZOMBIE_LAMENT)
                            .add(0.2, Mob.ARACHNO_VENARI)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.2, Mob.PIG_ALLEVIATOR)
                            .add(0.05, Mob.ZOMBIE_VANGUARD)
                            .add(0.05, Mob.SLIME_GUARD)
                    )
                    .add(13, new SimpleWave(16, 5 * SECOND, null)
                            .add(0.1, Mob.ZOMBIE_LANCER)
                            .add(0.1, Mob.PIG_DISCIPLE)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                            .add(0.1, Mob.ARACHNO_VENARI)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.2, Mob.PIG_ALLEVIATOR)
                            .add(0.05, Mob.ZOMBIE_VANGUARD)
                            .add(0.05, Mob.SLIME_GUARD)
                            .add(0.2, Mob.ZOMBIE_SWORDSMAN)
                    )
                    .add(15, new SimpleWave(1, 5 * SECOND, Component.text("Boss"))
                            .add(1, Mob.EVENT_PROMETHEUS)
                    )
                    .add(16, new SimpleWave(20, 5 * SECOND, null)
                            .add(0.2, Mob.PIG_DISCIPLE)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                            .add(0.1, Mob.ARACHNO_VENARI)
                            .add(0.2, Mob.PIG_SHAMAN)
                            .add(0.05, Mob.PIG_ALLEVIATOR)
                            .add(0.05, Mob.ZOMBIE_VANGUARD)
                            .add(0.05, Mob.SLIME_GUARD)
                            .add(0.05, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.1, Mob.OVERGROWN_ZOMBIE)
                            .add(0.1, Mob.RIFT_WALKER)
                    )
                    .add(20, new SimpleWave(1, 5 * SECOND, Component.text("Boss"))
                            .add(1, Mob.EVENT_ATHENA)
                    )
                    .add(21, new SimpleWave(24, 5 * SECOND, null)
                            .add(0.2, Mob.ZOMBIE_LAMENT)
                            .add(0.2, Mob.PIG_ALLEVIATOR)
                            .add(0.1, Mob.ZOMBIE_VANGUARD)
                            .add(0.1, Mob.SLIME_GUARD)
                            .add(0.1, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.1, Mob.OVERGROWN_ZOMBIE)
                            .add(0.1, Mob.RIFT_WALKER)
                            .add(0.05, Mob.SKELETAL_SORCERER)
                            .add(0.05, Mob.ZOMBIE_KNIGHT)
                    )
                    .add(25, new SimpleWave(1, 5 * SECOND, Component.text("Boss"))
                            .add(1, Mob.EVENT_CRONUS)
                    )
                    .loop(6, 21, 5)
                    .loop(6, 25, 5)
                    ,
                    DifficultyIndex.EVENT
            ) {
                @Override
                public void register(@Nonnull Game game) {
                    super.register(game);
                    game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(SCOREBOARD_PRIORITY - 2, "wave") {
                        @Nonnull
                        @Override
                        public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                            return Collections.singletonList(Component.text("Event: ").append(Component.text(getMapName(), NamedTextColor.GREEN)));
                        }
                    });
                }

                @Override
                public float getSpawnCountMultiplier(int playerCount) {
                    return switch (playerCount) {
                        case 3 -> 1.2f;
                        case 4 -> 1.5f;
                        default -> 1;
                    };
                }

                @Override
                protected void modifyStats(WarlordsNPC warlordsNPC) {
                    warlordsNPC.getMob().onSpawn(this);

                    int playerCount = playerCount();
                    float healthMultiplier = .5f + .5f * playerCount; // 1 / 1.5 / 2 / 2.5
                    float damageMultiplier = playerCount >= 4 ? 1.15f : 1;

                    float newBaseHealth = warlordsNPC.getMaxBaseHealth() * healthMultiplier;
                    warlordsNPC.setMaxBaseHealth(newBaseHealth);
                    warlordsNPC.setMaxHealth(newBaseHealth);
                    warlordsNPC.setHealth(newBaseHealth);
                    warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                            "Scaling",
                            null,
                            GameMap.class,
                            null,
                            warlordsNPC,
                            CooldownTypes.INTERNAL,
                            cooldownManager -> {

                            },
                            false
                    ) {
                        @Override
                        public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                            return currentDamageValue * damageMultiplier;
                        }
                    });
                }
            });
            options.add(new ItemOption());
            options.add(new WinAfterTimeoutOption(600, 50, "spec"));
            options.add(new TheAcropolisOption());
//            options.add(new SafeZoneOption(1));
            options.add(new EventPointsOption()
                    .reduceScoreOnAllDeath(30, Team.BLUE)
                    .onPerWaveClear(1, 500)
                    .onPerWaveClear(5, 2000)
                    .onPerMobKill(Mob.ZOMBIE_LANCER, 5)
                    .onPerMobKill(Mob.SKELETAL_ENTROPY, 5)
                    .onPerMobKill(Mob.PIG_DISCIPLE, 10)
                    .onPerMobKill(Mob.ZOMBIE_LAMENT, 10)
                    .onPerMobKill(Mob.ARACHNO_VENARI, 10)
                    .onPerMobKill(Mob.PIG_SHAMAN, 15)
                    .onPerMobKill(Mob.PIG_ALLEVIATOR, 15)
                    .onPerMobKill(Mob.ZOMBIE_VANGUARD, 20)
                    .onPerMobKill(Mob.SLIME_GUARD, 25)
                    .onPerMobKill(Mob.ZOMBIE_SWORDSMAN, 25)
                    .onPerMobKill(Mob.ILLUMINATION, 25)
                    .onPerMobKill(Mob.INTERMEDIATE_WARRIOR_BERSERKER, 25)
                    .onPerMobKill(Mob.SKELETAL_MESMER, 35)
                    .onPerMobKill(Mob.OVERGROWN_ZOMBIE, 40)
                    .onPerMobKill(Mob.ADVANCED_WARRIOR_BERSERKER, 40)
                    .onPerMobKill(Mob.RIFT_WALKER, 45)
                    .onPerMobKill(Mob.SKELETAL_SORCERER, 45)
                    .onPerMobKill(Mob.FIRE_SPLITTER, 45)
                    .onPerMobKill(Mob.ZOMBIE_KNIGHT, 50)
                    .onPerMobKill(Mob.SCRUPULOUS_ZOMBIE, 50)
                    .onPerMobKill(Mob.EVENT_TERAS_MINOTAUR, 150)
                    .onPerMobKill(Mob.EVENT_TERAS_CYCLOPS, 150)
                    .onPerMobKill(Mob.EVENT_TERAS_SIREN, 150)
                    .onPerMobKill(Mob.EVENT_TERAS_DRYAD, 150)
                    .onPerMobKill(Mob.EVENT_APOLLO, 1500)
                    .onPerMobKill(Mob.EVENT_ARES, 1500)
                    .onPerMobKill(Mob.EVENT_PROMETHEUS, 1500)
                    .onPerMobKill(Mob.EVENT_ATHENA, 1500)
                    .onPerMobKill(Mob.EVENT_CRONUS, 1500)
            );
            options.add(new CurrencyOnEventOption()
                    .startWith(120000)
                    .onKill(500)
                    .setPerWaveClear(5, 25000)
                    .onPerMobKill(Mob.EVENT_APOLLO, 10000)
                    .onPerMobKill(Mob.EVENT_ARES, 10000)
                    .onPerMobKill(Mob.EVENT_PROMETHEUS, 10000)
                    .onPerMobKill(Mob.EVENT_ATHENA, 10000)
                    .onPerMobKill(Mob.EVENT_CRONUS, 10000)
            );
            options.add(new CoinGainOption()
                    .clearMobCoinValueAndSet("Greek Gods Killed", new LinkedHashMap<>() {{
                        put("Apollo", 100L);
                        put("Ares", 100L);
                        put("Prometheus", 100L);
                        put("Athena", 100L);
                        put("Cronus", 100L);
                    }})
                    .playerCoinPerXSec(150, 10)
                    .guildCoinInsigniaConvertBonus(1000)
                    .guildCoinPerXSec(1, 1)
                    .disableCoinConversionUpgrade()
            );
            options.add(new ExperienceGainOption()
                    .playerExpPerXSec(15, 10)
                    .guildExpPerXSec(4, 10)
            );
            options.add(new FieldEffectOption(options, FieldEffectOption.FieldEffect.TYCHE_PROSPERITY));

            return options;
        }

    },
    TARTARUS(
            "Tartarus",
            4,
            2,
            120 * SECOND,
            "Tartarus",
            3,
            GameMode.EVENT_WAVE_DEFENSE
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TextOption.Type.CHAT_CENTERED.create(
                    Component.text(getMapName(), NamedTextColor.WHITE, TextDecoration.BOLD),
                    Component.empty(),
                    Component.text("Kill the gods as fast as possible!", NamedTextColor.YELLOW, TextDecoration.BOLD),
                    Component.empty()
            ));
            options.add(TextOption.Type.TITLE.create(
                    0,
                    Component.text("Grace Period!", NamedTextColor.GREEN),
                    Component.text("Buy upgrades and prepare", NamedTextColor.YELLOW)
            ));

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(108.5, 33, 61.5), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(108.5, 33, 61.5), Team.RED).asOption());

            options.add(SpawnpointOption.forTeam(loc.addXYZ(108.5, 33, 61.5), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(8.5, 23, 5.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(0.5, 23, 13.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-7.5, 23, 5.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(8.5, 23, -10.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(0.5, 23, -18.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-7.5, 23, -10.5), Team.RED));

            options.add(new PowerupOption(loc.addXYZ(137.5, 34.5, 87.5), PowerUp.COOLDOWN, 180, 30));
            options.add(new PowerupOption(loc.addXYZ(137.5, 34.5, 37.5), PowerUp.DAMAGE, 180, 30));
            options.add(new PowerupOption(loc.addXYZ(87.5, 34.5, 37.5), PowerUp.HEALING, 180, 30));
            options.add(new PowerupOption(loc.addXYZ(87.5, 34.5, 87.5), PowerUp.SPEED, 90, 30));

            options.add(new RespawnWaveOption(2, 1, 20));
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

            options.add(new WinByMaxWaveClearOption());

            ReadyUpOption readyUpOption = new ReadyUpOption("Charon") {
                @Override
                protected void createNPC(@Nonnull Game game) {
                    npc = NPCManager.NPC_REGISTRY.createNPC(EntityType.PLAYER, "ready-up");
                    npc.getOrAddTrait(ReadyUpTrait.class).setReadyUpOption(this);
                    // https://minesk.in/29ee19b22b7d421d86f10f5017f7abe5
                    SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
                    skinTrait.setSkinPersistent(
                            "Charon",
                            "bksbQ5KevV4Bjm7cJHkCa6D2BCPSB7KLQZ7wO2+omZGw93NcEkalzl6sjRDyLYft4TPxa7QsXr7tDnHqLcrgkGM9tlNIlKLN6wtJ/WJjUIo5SOsm09JVuRtohQH4HRbu5fIhaoFZhkywsetZmJHW3ZNMR8ErgojpUdg6UqaUuL6DMZWTcbTBpDHOWmM9GVwDPArHrsFMD11L5BlDdKsUgbIbvmHdOep8oXx4PujVP5G2GWDEHc9j4XIZN8PpR9t5PajyAOoXMcUJXFf8d5QJXMWTT1VDqPy0Q17aAM87xjzASJbXxBrvMgcV2bkhkmwifxMAEuvrHAXdpjRBBNz+5iPDwFmjs4XNWvdy/Z6idrPcfJuD9qDSq3V6SGqfoFw4b4FUOg2K5T/MCIttp8SGL2cN52uuirlFwk+oalrOidhhbB8YoEMpYLp06aU6MSTVBKL8uIjB/yOjVX40664ciOJf+GPAhXYnVBTYHqrVu4rUf3FJkNbfROo6if0oM6EVzJ+FMCEbNTmhtVZGQ6ljJ/pVR+Qy/EMUbU+lLHTVZ28DVnwTZ8XcuNUX6c+hnVfyic+yy7m3lxhmd6Wthsrr3JwZfw19wakqapJQ1j7bYAny/oH3AqMQqBDW6sIOIwtJgttPGVtqHn41iXfcP23zr85gJjl5tiFrRH3qNiTVpFI=",
                            "ewogICJ0aW1lc3RhbXAiIDogMTYwODU2NTczOTQyNywKICAicHJvZmlsZUlkIiA6ICJmMTA0NzMxZjljYTU0NmI0OTkzNjM4NTlkZWY5N2NjNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJ6aWFkODciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmI4NThkYTE0MDE2YjRhNDVhMGMzNzRlMmI4M2QxYzY3MTJiNzIwNzNiODEyM2QzNTlmOTQxNmVmNGJhZjk5NSIKICAgIH0KICB9Cn0="
                    );

                    npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
                    npc.spawn(game.getLocations().addXYZ(119.5, 33, 72.5, 135, 0));
                }
            };
            options.add(readyUpOption);

            List<Location> bossSpawnLocations = new ArrayList<>();
            bossSpawnLocations.add(loc.addXYZ(100.5, 33, 51.5, -90, 0));
            bossSpawnLocations.add(loc.addXYZ(125.5, 33, 40.5, 0, 0));
            bossSpawnLocations.add(loc.addXYZ(144.5, 33, 69.5, 180, 0));
            bossSpawnLocations.add(loc.addXYZ(114.5, 33, 96.5, 180, 0));
            bossSpawnLocations.add(loc.addXYZ(88.5, 33, 76.5, -90, 0));
            Collections.shuffle(bossSpawnLocations);
            WaveDefenseOption waveDefenseOption = new WaveDefenseOption(Team.RED, new StaticWaveList()
                    .add(1, new FixedWave(60 * SECOND, -1, null)
                            .add(Mob.EVENT_HADES, bossSpawnLocations.get(0))
                            .add(Mob.EVENT_POSEIDON, bossSpawnLocations.get(1))
                            .add(Mob.EVENT_ZEUS, bossSpawnLocations.get(2))
                    ), DifficultyIndex.EVENT, 1
            ) {
                @Override
                public void register(@Nonnull Game game) {
                    super.register(game);
                    game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(SCOREBOARD_PRIORITY - 2, "wave") {
                        @Nonnull
                        @Override
                        public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                            return Collections.singletonList(Component.text("Event: ").append(Component.text(getMapName(), NamedTextColor.GREEN)));
                        }
                    });
                }

                @Override
                public List<Component> getWaveScoreboard(WarlordsPlayer player) {
                    return Collections.emptyList();
                }

                @Override
                public float getSpawnCountMultiplier(int playerCount) {
                    return 1;
                }

                @Override
                protected void modifyStats(WarlordsNPC warlordsNPC) {
                    warlordsNPC.getMob().onSpawn(this);

                    int playerCount = playerCount();
                    boolean fourManPlus = playerCount >= 4;
                    float healthMultiplier = .5f * playerCount; // 1 / 1.5 / 2
                    float damageMultiplier = fourManPlus ? 1.30f : 1;

                    if (fourManPlus) {
                        healthMultiplier += .15f;
                    }

                    float newBaseHealth = warlordsNPC.getMaxBaseHealth() * healthMultiplier;
                    warlordsNPC.setMaxBaseHealth(newBaseHealth);
                    warlordsNPC.setMaxHealth(newBaseHealth);
                    warlordsNPC.setHealth(newBaseHealth);
                    warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                            "Scaling",
                            null,
                            GameMap.class,
                            null,
                            warlordsNPC,
                            CooldownTypes.INTERNAL,
                            cooldownManager -> {

                            },
                            false
                    ) {
                        @Override
                        public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                            return currentDamageValue * damageMultiplier;
                        }
                    });
                }

                @Override
                protected Pair<Float, Component> getWaveOpening() {
                    return new Pair<>(.8f, Component.text(""));
                }

                @Override
                protected void onSpawnDelayChange(int newTickDelay) {
                    switch (newTickDelay) {
                        case 960 -> readyUpOption.sendNPCMessage(Component.text("Hades has powers of resurrection given to him by the eternal flames.", NamedTextColor.YELLOW));
                        case 720 -> readyUpOption.sendNPCMessage(Component.text("Poseidon retaliates and responds negatively to the loss of his brothers.", NamedTextColor.YELLOW));
                        case 480 -> readyUpOption.sendNPCMessage(Component.text("Zeus sends his brothers into battle as he leads the gods to ruin.", NamedTextColor.YELLOW));
                        case 240 -> readyUpOption.sendNPCMessage(Component.text("The order in which the brothers fall will determine their fate as well as yours!",
                                NamedTextColor.YELLOW
                        ));
                        case 0 -> {
                            readyUpOption.sendNPCMessage(Component.text("Good luck, you'll need it.", NamedTextColor.YELLOW));
                            readyUpOption.getNpc().destroy();
                            getGame().forEachOnlinePlayer((p, t) -> {
                                p.showTitle(Title.title(
                                        Component.text("Fight!", NamedTextColor.GREEN),
                                        Component.text("Organization is key.", NamedTextColor.YELLOW),
                                        Title.Times.times(Ticks.duration(0), Ticks.duration(30), Ticks.duration(20))
                                ));
                            });
                        }
                    }
                }
            };
            options.add(waveDefenseOption);
            readyUpOption.setWhenAllReady(() -> {
                waveDefenseOption.setCurrentDelay(1);
                readyUpOption.getNpc().destroy();
            });
            options.add(new ItemOption());
            options.add(new WinAfterTimeoutOption(600, 50, "spec"));
            options.add(new TartarusOption());
//            options.add(new SafeZoneOption(1));
            options.add(new EventPointsOption()
                    .reduceScoreOnAllDeath(30, Team.BLUE)
                    .onPerMobKill(Mob.EVENT_ZEUS, 5000)
                    .onPerMobKill(Mob.EVENT_POSEIDON, 5000)
                    .onPerMobKill(Mob.EVENT_HADES, 5000)

            );
            options.add(new CurrencyOnEventOption()
                    .startWith(750000)
                    .onKill(500)
            );
            options.add(new CoinGainOption()
                    .clearMobCoinValueAndSet("Greek Gods Killed", new LinkedHashMap<>() {{
                        put("Zeus", 100L);
                        put("Poseidon", 100L);
                        put("Hades", 100L);
                    }})
                    .playerCoinPerXSec(150, 10)
                    .guildCoinInsigniaConvertBonus(1000)
                    .guildCoinPerXSec(1, 1)
                    .disableCoinConversionUpgrade()
            );
            options.add(new ExperienceGainOption()
                    .playerExpGameWinBonus(2500)
                    .playerExpPerXSec(15, 10)
                    .guildExpPerXSec(4, 10)
            );
            options.add(new FieldEffectOption(options, FieldEffectOption.FieldEffect.TYCHE_PROSPERITY));

            return options;
        }

    },
    GRIMOIRES_GRAVEYARD(
            "Grimoire’s Graveyard",
            6,
            2,
            120 * SECOND,
            "Grimoires",
            3,
            GameMode.EVENT_WAVE_DEFENSE
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TextOption.Type.CHAT_CENTERED.create(
                    Component.text(getMapName(), NamedTextColor.WHITE, TextDecoration.BOLD),
                    Component.empty(),
                    Component.text("Kill mobs to gain event points!", NamedTextColor.YELLOW, TextDecoration.BOLD),
                    Component.empty()
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    Component.text("GO!", NamedTextColor.GREEN),
                    Component.text("Kill as many mobs as possible!", NamedTextColor.YELLOW)
            ));

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(16, 27, 1.5), Team.BLUE).asOption()); //TODO
            options.add(LobbyLocationMarker.create(loc.addXYZ(16, 27, 1.5), Team.RED).asOption());

            options.add(SpawnpointOption.forTeam(loc.addXYZ(16, 27, 1.5), Team.BLUE)); //TODO

            options.add(SpawnpointOption.forTeam(loc.addXYZ(2.5, 27, 17.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-10.5, 27, 16.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-18.5, 27, 8.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-18.5, 27, -5.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-10.5, 27, -13.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(3.5, 27, -13.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(11.5, 27, -5.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(11.5, 27, 8.5), Team.RED));

            List<Location> necronomiconSpawnLocations = List.of(
                    loc.addXYZ(-16.5, 24, 16.5),
                    loc.addXYZ(9.5, 24, 16.5),
                    loc.addXYZ(11.5, 24, 14.5),
                    loc.addXYZ(11.5, 24, -11.5),
                    loc.addXYZ(9.5, 24, -13.5),
                    loc.addXYZ(-16.5, 24, -13.5),
                    loc.addXYZ(-18.5, 24, -11.5),
                    loc.addXYZ(-18.5, 24, 14.5)
            );

            options.add(new PowerupOption(loc.addXYZ(16, 25, 1.5), PowerUp.DAMAGE, 180, 30));
            options.add(new PowerupOption(loc.addXYZ(-23.5, 25, 1.5), PowerUp.DAMAGE, 180, 30));
            options.add(new PowerupOption(loc.addXYZ(-3.5, 25, 21.5), PowerUp.HEALING, 90, 30));
            options.add(new PowerupOption(loc.addXYZ(-3.5, 25, -18.5), PowerUp.HEALING, 90, 30));

            options.add(new RespawnWaveOption(2, 1, 20));
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

            options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                    .add(1, new SimpleWave(8, 5 * SECOND, null)
                            .add(0.4, Mob.ZOMBIE_LANCER)
                            .add(0.4, Mob.PIG_DISCIPLE)
                            .add(0.2, Mob.SLIMY_ANOMALY)
                    )
                    .add(2, new SimpleWave(8, 5 * SECOND, null)
                            .add(0.5, Mob.ZOMBIE_LANCER)
                            .add(0.4, Mob.PIG_DISCIPLE)
                            .add(0.1, Mob.ZOMBIE_SWORDSMAN)
                    )
                    .add(4, new SimpleWave(10, 5 * SECOND, null)
                            .add(0.3, Mob.ZOMBIE_LANCER)
                            .add(0.3, Mob.PIG_DISCIPLE)
                            .add(0.1, Mob.SLIMY_ANOMALY)
                            .add(0.2, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.1, Mob.EVENT_UNPUBLISHED_GRIMOIRE)
                    )
                    .add(5, new SimpleWave(6, 5 * SECOND, Component.text("Boss"))
                            .add(1, 1, Mob.EVENT_ROUGE_GRIMOIRE)
                            .add(1, 1, Mob.EVENT_VIOLETTE_GRIMOIRE)
                            .add(1, 1, Mob.EVENT_BLEUE_GRIMOIRE)
                            .add(1, 1, Mob.EVENT_ORANGE_GRIMOIRE)
                            .add(1, 1, Mob.EVENT_UNPUBLISHED_GRIMOIRE)
                            .add(1, 1, Mob.EVENT_EMBELLISHED_GRIMOIRE, necronomiconSpawnLocations)
                    )
                    .add(6, new SimpleWave(12, 5 * SECOND, null)
                            .add(0.3, Mob.PIG_DISCIPLE)
                            .add(0.1, Mob.SLIMY_ANOMALY)
                            .add(0.2, Mob.EVENT_UNPUBLISHED_GRIMOIRE)
                            .add(0.2, Mob.PIG_SHAMAN)
                            .add(0.1, Mob.SKELETAL_WARLOCK)
                            .add(0.1, Mob.EVENT_EMBELLISHED_GRIMOIRE)
                    )
                    .add(8, new SimpleWave(12, 5 * SECOND, null)
                            .add(0.3, Mob.ZOMBIE_LAMENT)
                            .add(0.1, Mob.PIG_SHAMAN)
                            .add(0.1, Mob.SKELETAL_WARLOCK)
                            .add(0.2, Mob.EVENT_UNPUBLISHED_GRIMOIRE)
                            .add(0.2, Mob.EVENT_EMBELLISHED_GRIMOIRE)
                            .add(0.1, 1, Mob.EVENT_NECRONOMICON_GRIMOIRE, necronomiconSpawnLocations)
                    )
                    .add(10, new SimpleWave(13, 5 * SECOND, Component.text("Boss"))
                            .add(1, 2, Mob.EVENT_ROUGE_GRIMOIRE)
                            .add(1, 2, Mob.EVENT_VIOLETTE_GRIMOIRE)
                            .add(1, 2, Mob.EVENT_BLEUE_GRIMOIRE)
                            .add(1, 2, Mob.EVENT_ORANGE_GRIMOIRE)
                            .add(1, 2, Mob.EVENT_UNPUBLISHED_GRIMOIRE)
                            .add(1, 2, Mob.EVENT_EMBELLISHED_GRIMOIRE)
                            .add(1, 1, Mob.EVENT_NECRONOMICON_GRIMOIRE, necronomiconSpawnLocations)
                    )
                    .add(11, new SimpleWave(14, 5 * SECOND, null)
                            .add(0.05, Mob.ZOMBIE_LAMENT)
                            .add(0.05, Mob.SLIMY_ANOMALY)
                            .add(0.2, Mob.EVENT_UNPUBLISHED_GRIMOIRE)
                            .add(0.3, Mob.ARACHNO_VENARI)
                            .add(0.1, Mob.SKELETAL_ENTROPY)
                            .add(0.2, Mob.EVENT_EMBELLISHED_GRIMOIRE)
                            .add(0.1, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                    )
                    .add(13, new SimpleWave(14, 5 * SECOND, null)
                            .add(0.05, Mob.ZOMBIE_LAMENT)
                            .add(0.05, Mob.SLIMY_ANOMALY)
                            .add(0.2, Mob.ARACHNO_VENARI)
                            .add(0.2, Mob.EVENT_UNPUBLISHED_GRIMOIRE)
                            .add(0.2, Mob.SKELETAL_ENTROPY)
                            .add(0.1, Mob.EVENT_EMBELLISHED_GRIMOIRE)
                            .add(0.1, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                            .add(0.1, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                    )
                    .add(15, new SimpleWave(19, 5 * SECOND, Component.text("Boss"))
                            .add(1, 3, Mob.EVENT_ROUGE_GRIMOIRE)
                            .add(1, 3, Mob.EVENT_VIOLETTE_GRIMOIRE)
                            .add(1, 3, Mob.EVENT_BLEUE_GRIMOIRE)
                            .add(1, 3, Mob.EVENT_ORANGE_GRIMOIRE)
                            .add(1, 3, Mob.EVENT_UNPUBLISHED_GRIMOIRE)
                            .add(1, 3, Mob.EVENT_EMBELLISHED_GRIMOIRE)
                            .add(1, 1, Mob.EVENT_SCRIPTED_GRIMOIRE)
                    )
                    .add(16, new SimpleWave(16, 5 * SECOND, null)
                            .add(0.1, Mob.ZOMBIE_LAMENT)
                            .add(0.1, Mob.ARACHNO_VENARI)
                            .add(0.1, Mob.EVENT_UNPUBLISHED_GRIMOIRE)
                            .add(0.2, Mob.EVENT_EMBELLISHED_GRIMOIRE)
                            .add(0.1, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                            .add(0.1, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                            .add(0.05, Mob.ZOMBIE_VANGUARD)
                            .add(0.05, Mob.SLIME_GUARD)
                            .add(0.2, 1, Mob.EVENT_NECRONOMICON_GRIMOIRE, necronomiconSpawnLocations)
                    )
                    .add(20, new SimpleWave(26, 5 * SECOND, Component.text("Boss"))
                            .add(1, 4, Mob.EVENT_ROUGE_GRIMOIRE)
                            .add(1, 4, Mob.EVENT_VIOLETTE_GRIMOIRE)
                            .add(1, 4, Mob.EVENT_BLEUE_GRIMOIRE)
                            .add(1, 4, Mob.EVENT_ORANGE_GRIMOIRE)
                            .add(1, 3, Mob.EVENT_UNPUBLISHED_GRIMOIRE)
                            .add(1, 3, Mob.EVENT_EMBELLISHED_GRIMOIRE)
                            .add(1, 2, Mob.EVENT_SCRIPTED_GRIMOIRE)
                            .add(1, 2, Mob.EVENT_NECRONOMICON_GRIMOIRE, necronomiconSpawnLocations)
                    )
                    .add(21, new SimpleWave(18, 5 * SECOND, null)
                            .add(0.2, Mob.EVENT_UNPUBLISHED_GRIMOIRE)
                            .add(0.2, Mob.EVENT_EMBELLISHED_GRIMOIRE)
                            .add(0.1, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                            .add(0.1, Mob.ZOMBIE_VANGUARD)
                            .add(0.1, Mob.SLIME_GUARD)
                            .add(0.2, Mob.ILLUMINATION)
                            .add(0.1, Mob.FIRE_SPLITTER)
                    )
                    .add(25, new SimpleWave(30, 5 * SECOND, Component.text("Boss"))
                            .add(1, 4, Mob.EVENT_ROUGE_GRIMOIRE)
                            .add(1, 4, Mob.EVENT_VIOLETTE_GRIMOIRE)
                            .add(1, 4, Mob.EVENT_BLEUE_GRIMOIRE)
                            .add(1, 4, Mob.EVENT_ORANGE_GRIMOIRE)
                            .add(1, 4, Mob.EVENT_UNPUBLISHED_GRIMOIRE)
                            .add(1, 3, Mob.EVENT_EMBELLISHED_GRIMOIRE)
                            .add(1, 2, Mob.EVENT_SCRIPTED_GRIMOIRE)
                            .add(1, 4, Mob.EVENT_NECRONOMICON_GRIMOIRE, necronomiconSpawnLocations)
                            .add(10, 1, Mob.EVENT_THE_ARCHIVIST, loc.addXYZ(-3.5, 25, 1.5))
                    )
                    ,
                    DifficultyIndex.EVENT, 25
            ) {
                @Override
                public void register(@Nonnull Game game) {
                    super.register(game);
                    game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(SCOREBOARD_PRIORITY - 2, "wave") {
                        @Nonnull
                        @Override
                        public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                            return Collections.singletonList(Component.text("Event: ").append(Component.text(getMapName(), NamedTextColor.GREEN)));
                        }
                    });
                }

                @Override
                public float getSpawnCountMultiplier(int playerCount) {
                    return switch (playerCount) {
                        case 3 -> 1.2f;
                        case 4 -> 1.5f;
                        case 5 -> 1.9f;
                        case 6 -> 2.4f;
                        default -> 1;
                    };
                }

                @Override
                protected void modifyStats(WarlordsNPC warlordsNPC) {
                    warlordsNPC.getMob().onSpawn(this);

                    int playerCount = playerCount();
                    float healthMultiplier = switch (playerCount) {
                        case 3 -> 1.5f;
                        case 4 -> 2f;
                        case 5 -> 2.25f;
                        case 6 -> 2.5f;
                        default -> 1;
                    };
                    float damageMultiplier = playerCount >= 4 ? playerCount >= 6 ? 1.2f : 1.1f : 1f;
                    float newBaseHealth = warlordsNPC.getMaxBaseHealth() * healthMultiplier;
                    warlordsNPC.setMaxBaseHealth(newBaseHealth);
                    warlordsNPC.setMaxHealth(newBaseHealth);
                    warlordsNPC.setHealth(newBaseHealth);
                    warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                            "Scaling",
                            null,
                            GameMap.class,
                            null,
                            warlordsNPC,
                            CooldownTypes.INTERNAL,
                            cooldownManager -> {

                            },
                            false
                    ) {
                        @Override
                        public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                            return currentDamageValue * damageMultiplier;
                        }
                    });
                }
            });
            options.add(new ItemOption());
            options.add(new WinAfterTimeoutOption(900, 50, "spec"));
            options.add(new WinByMaxWaveClearOption());
            options.add(new GrimoiresGraveyardOption());
            options.add(new EventPointsOption()
                    .reduceScoreOnAllDeath(30, Team.BLUE)
                    .onPerWaveClear(1, 500)
                    .onPerWaveClear(5, 2000)
                    .onPerMobKill(Mob.ZOMBIE_LANCER, 5)
                    .onPerMobKill(Mob.SKELETAL_ENTROPY, 5)
                    .onPerMobKill(Mob.SLIMY_ANOMALY, 5)
                    .onPerMobKill(Mob.PIG_DISCIPLE, 10)
                    .onPerMobKill(Mob.EVENT_UNPUBLISHED_GRIMOIRE, 10)
                    .onPerMobKill(Mob.ZOMBIE_LAMENT, 10)
                    .onPerMobKill(Mob.ARACHNO_VENARI, 10)
                    .onPerMobKill(Mob.ZOMBIE_SWORDSMAN, 15)
                    .onPerMobKill(Mob.PIG_SHAMAN, 15)
                    .onPerMobKill(Mob.SKELETAL_ENTROPY, 15)
                    .onPerMobKill(Mob.SKELETAL_WARLOCK, 15)
                    .onPerMobKill(Mob.INTERMEDIATE_WARRIOR_BERSERKER, 15)
                    .onPerMobKill(Mob.ZOMBIE_VANGUARD, 20)
                    .onPerMobKill(Mob.EVENT_EMBELLISHED_GRIMOIRE, 20)
                    .onPerMobKill(Mob.SLIME_GUARD, 25)
                    .onPerMobKill(Mob.ILLUMINATION, 25)
                    .onPerMobKill(Mob.INTERMEDIATE_WARRIOR_BERSERKER, 25)
                    .onPerMobKill(Mob.FIRE_SPLITTER, 40)
                    .onPerMobKill(Mob.EVENT_SCRIPTED_GRIMOIRE, 150)
                    .onPerMobKill(Mob.EVENT_NECRONOMICON_GRIMOIRE, 150)
                    .onPerMobKill(Mob.EVENT_ROUGE_GRIMOIRE, 500)
                    .onPerMobKill(Mob.EVENT_VIOLETTE_GRIMOIRE, 500)
                    .onPerMobKill(Mob.EVENT_BLEUE_GRIMOIRE, 500)
                    .onPerMobKill(Mob.EVENT_ORANGE_GRIMOIRE, 500)
                    .onPerMobKill(Mob.EVENT_THE_ARCHIVIST, 2500)
            );
            options.add(new CurrencyOnEventOption()
                    .startWith(120000)
                    .onKill(500)
                    .setPerWaveClear(5, 25000)
                    .onPerWaveClear(1, 500)
                    .onPerMobKill(Mob.ZOMBIE_LANCER, 5)
                    .onPerMobKill(Mob.SKELETAL_ENTROPY, 5)
                    .onPerMobKill(Mob.SLIMY_ANOMALY, 5)
                    .onPerMobKill(Mob.PIG_DISCIPLE, 10)
                    .onPerMobKill(Mob.EVENT_UNPUBLISHED_GRIMOIRE, 10)
                    .onPerMobKill(Mob.ZOMBIE_LAMENT, 10)
                    .onPerMobKill(Mob.ARACHNO_VENARI, 10)
                    .onPerMobKill(Mob.ZOMBIE_SWORDSMAN, 15)
                    .onPerMobKill(Mob.PIG_SHAMAN, 15)
                    .onPerMobKill(Mob.SKELETAL_ENTROPY, 15)
                    .onPerMobKill(Mob.SKELETAL_WARLOCK, 15)
                    .onPerMobKill(Mob.INTERMEDIATE_WARRIOR_BERSERKER, 15)
                    .onPerMobKill(Mob.ZOMBIE_VANGUARD, 20)
                    .onPerMobKill(Mob.EVENT_EMBELLISHED_GRIMOIRE, 20)
                    .onPerMobKill(Mob.SLIME_GUARD, 25)
                    .onPerMobKill(Mob.ILLUMINATION, 25)
                    .onPerMobKill(Mob.INTERMEDIATE_WARRIOR_BERSERKER, 25)
                    .onPerMobKill(Mob.FIRE_SPLITTER, 40)
                    .onPerMobKill(Mob.EVENT_SCRIPTED_GRIMOIRE, 150)
                    .onPerMobKill(Mob.EVENT_NECRONOMICON_GRIMOIRE, 5000)
                    .onPerMobKill(Mob.EVENT_ROUGE_GRIMOIRE, 5000)
                    .onPerMobKill(Mob.EVENT_VIOLETTE_GRIMOIRE, 5000)
                    .onPerMobKill(Mob.EVENT_BLEUE_GRIMOIRE, 5000)
                    .onPerMobKill(Mob.EVENT_ORANGE_GRIMOIRE, 5000)
                    .onPerMobKill(Mob.EVENT_THE_ARCHIVIST, 5000)
            );
            options.add(new CoinGainOption()
                    .clearMobCoinValueAndSet("Bosses Killed", new LinkedHashMap<>() {{
                        put("Rouge Grimoire", 1000L);
                        put("Violette Grimoire", 1000L);
                        put("Bleue Grimoire", 1000L);
                        put("Orange Grimoire", 1000L);
                        put("Necronomicon Grimoire", 1000L);
                        put("The Archivist", 1000L);
                    }})
                    .playerCoinPerXSec(150, 10)
                    .guildCoinInsigniaConvertBonus(1000)
                    .guildCoinPerXSec(1, 3)
                    .disableCoinConversionUpgrade()
            );
            options.add(new ExperienceGainOption()
                    .playerExpPerXSec(10, 10)
                    .guildExpPerXSec(20, 30)
            );
            options.add(new FieldEffectOption(options, FieldEffectOption.FieldEffect.ACCUMULATING_KNOWLEDGE));


            return options;
        }
    },
    FORGOTTEN_CODEX(
            "Forgotten Codex",
            6,
            2,
            120 * SECOND,
            "Forgotten",
            3,
            GameMode.EVENT_WAVE_DEFENSE
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TextOption.Type.CHAT_CENTERED.create(
                    Component.text(getMapName(), NamedTextColor.WHITE, TextDecoration.BOLD),
                    Component.empty(),
                    Component.text("Kill mobs to gain event points!", NamedTextColor.YELLOW, TextDecoration.BOLD),
                    Component.empty()
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    Component.text("GO!", NamedTextColor.GREEN),
                    Component.text("Kill as many mobs as possible!", NamedTextColor.YELLOW)
            ));


            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-4.5, 35, 7.5), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-4.5, 35, 7.5), Team.RED).asOption());

            options.add(SpawnpointOption.forTeam(loc.addXYZ(-4.5, 35, 7.5), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-4.5, 35, 7.5), Team.RED));

            options.add(new PowerupOption(loc.addXYZ(-4.5, 35, 7.5), PowerUp.HEALING, 90, 30));

            options.add(new RespawnWaveOption(2, 1, 20));
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

            options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                    .add(1, new SimpleWave(1, 5 * SECOND, Component.text("Boss"))
                            .add(1, Mob.EVENT_INQUISITEUR_EWA)
                            .add(1, Mob.EVENT_INQUISITEUR_EGA)
                            .add(1, Mob.EVENT_INQUISITEUR_VPA)
                    ),
                    DifficultyIndex.EVENT, 1
            ) {
                @Override
                public void register(@Nonnull Game game) {
                    super.register(game);
                    game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(SCOREBOARD_PRIORITY - 2, "wave") {
                        @Nonnull
                        @Override
                        public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                            return Collections.singletonList(Component.text("Event: ").append(Component.text(getMapName(), NamedTextColor.GREEN)));
                        }
                    });
                }

                @Override
                public float getSpawnCountMultiplier(int playerCount) {
                    return switch (playerCount) {
                        case 3 -> 1.2f;
                        case 4 -> 1.5f;
                        case 5 -> 1.9f;
                        case 6 -> 2.4f;
                        default -> 1;
                    };
                }

                @Override
                protected void modifyStats(WarlordsNPC warlordsNPC) {
                    warlordsNPC.getMob().onSpawn(this);

                    int playerCount = playerCount();
                    float healthMultiplier = switch (playerCount) {
                        case 3 -> 1.5f;
                        case 4 -> 2f;
                        case 5 -> 2.25f;
                        case 6 -> 2.5f;
                        default -> 1;
                    };
                    float damageMultiplier = playerCount >= 4 ? playerCount >= 6 ? 1.2f : 1.1f : 1f;
                    float newBaseHealth = warlordsNPC.getMaxBaseHealth() * healthMultiplier;
                    warlordsNPC.setMaxBaseHealth(newBaseHealth);
                    warlordsNPC.setMaxHealth(newBaseHealth);
                    warlordsNPC.setHealth(newBaseHealth);
                    warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                            "Scaling",
                            null,
                            GameMap.class,
                            null,
                            warlordsNPC,
                            CooldownTypes.INTERNAL,
                            cooldownManager -> {

                            },
                            false
                    ) {
                        @Override
                        public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                            return currentDamageValue * damageMultiplier;
                        }
                    });
                }
            });
            options.add(new ItemOption());
            options.add(new WinAfterTimeoutOption(600, 50, "spec"));
            options.add(new WinByMaxWaveClearOption());
            options.add(new ForgottenCodexOption());
            options.add(new EventPointsOption()
                    .reduceScoreOnAllDeath(30, Team.BLUE)
                    .onPerWaveClear(1, 500)
                    .onPerWaveClear(5, 2000)
                    .onPerMobKill(Mob.EVENT_UNPUBLISHED_GRIMOIRE, 10)
                    .onPerMobKill(Mob.ZOMBIE_SWORDSMAN, 15)
                    .onPerMobKill(Mob.PIG_SHAMAN, 15)
                    .onPerMobKill(Mob.GOLEM_APPRENTICE, 15)
                    .onPerMobKill(Mob.EVENT_EMBELLISHED_GRIMOIRE, 20)
                    .onPerMobKill(Mob.EVENT_SCRIPTED_GRIMOIRE, 150)
                    .onPerMobKill(Mob.EVENT_NECRONOMICON_GRIMOIRE, 150)
                    .onPerMobKill(Mob.EVENT_ROUGE_GRIMOIRE, 500)
                    .onPerMobKill(Mob.EVENT_VIOLETTE_GRIMOIRE, 500)
                    .onPerMobKill(Mob.EVENT_BLEUE_GRIMOIRE, 500)
                    .onPerMobKill(Mob.EVENT_ORANGE_GRIMOIRE, 500)
                    .onPerMobKill(Mob.EVENT_INQUISITEUR_EWA, 10_000)
                    .onPerMobKill(Mob.EVENT_INQUISITEUR_EGA, 10_000)
                    .onPerMobKill(Mob.EVENT_INQUISITEUR_VPA, 10_000)
            );
            options.add(new CurrencyOnEventOption()
                    .startWith(750000)
                    .onKill(500)
                    .onPerMobKill(Mob.EVENT_UNPUBLISHED_GRIMOIRE, 10)
                    .onPerMobKill(Mob.ZOMBIE_SWORDSMAN, 15)
                    .onPerMobKill(Mob.PIG_SHAMAN, 15)
                    .onPerMobKill(Mob.GOLEM_APPRENTICE, 15)
                    .onPerMobKill(Mob.EVENT_SCRIPTED_GRIMOIRE, 150)
                    .onPerMobKill(Mob.EVENT_NECRONOMICON_GRIMOIRE, 5000)
                    .onPerMobKill(Mob.EVENT_ROUGE_GRIMOIRE, 5000)
                    .onPerMobKill(Mob.EVENT_VIOLETTE_GRIMOIRE, 5000)
                    .onPerMobKill(Mob.EVENT_BLEUE_GRIMOIRE, 5000)
                    .onPerMobKill(Mob.EVENT_ORANGE_GRIMOIRE, 5000)
                    .onPerMobKill(Mob.EVENT_INQUISITEUR_EWA, 20_000)
                    .onPerMobKill(Mob.EVENT_INQUISITEUR_EGA, 20_000)
                    .onPerMobKill(Mob.EVENT_INQUISITEUR_VPA, 20_000)
            );
            options.add(new CoinGainOption()
                    .clearMobCoinValueAndSet("Greek Gods Killed", new LinkedHashMap<>() {{
                        put("Rouge Grimoire", 1000L);
                        put("Violette Grimoire", 1000L);
                        put("Bleue Grimoire", 1000L);
                        put("Orange Grimoire", 1000L);
                        put("Necronomicon Grimoire", 1000L);
                        put("Inquisiteur-EWA", 15000L);
                        put("Inquisiteur-EGA", 15000L);
                        put("Inquisiteur-VPA", 15000L);
                    }})
                    .playerCoinPerXSec(150, 5)
                    .guildCoinInsigniaConvertBonus(1000)
                    .guildCoinPerXSec(1, 1)
                    .disableCoinConversionUpgrade()
            );
            options.add(new ExperienceGainOption()
                    .playerExpPerXSec(10, 5)
                    .guildExpPerXSec(30, 30)
            );
            options.add(new FieldEffectOption(options, FieldEffectOption.FieldEffect.CODEX_COLLECTOR));

            return options;
        }
    },
    ILLUSION_PHANTOM(
            "Illusion Phantom",
            6,
            1,
            30 * SECOND,
            "IllusionPhantom",
            3,
            GameMode.ONSLAUGHT
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(0.5, 68, 0.5), Team.BLUE).asOption());
            options.add(SpawnpointOption.forTeam(loc.addXYZ(0, 68, 0), Team.BLUE));

            options.add(SpawnpointOption.forTeam(loc.addXYZ(12.5, 68, 11.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(16.5, 68, 15.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(16.5, 68, -15.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(12.5, 68, -12.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-11.5, 68, -12.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-15.5, 68, -16.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-11.5, 68, 11.5), Team.RED));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-15.5, 68, 15.5), Team.RED));

            options.add(new RespawnWaveOption(1, 20, 10));
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));
            options.add(new CurrencyOnEventOption()
                    .onKill(250, true)
                    .startWith(15000)
            );
            options.add(new OnslaughtOption(Team.RED, new StaticWaveList()
                    .add(0, new SimpleWave(Component.text("EASY", NamedTextColor.GREEN))
                            .add(0.8, Mob.ZOMBIE_LANCER)
                            .add(0.2, Mob.ZOMBIE_LAMENT)
                            .add(0.05, Mob.SKELETAL_MAGE)
                            .add(0.1, Mob.SLIMY_ANOMALY)
                            .add(0.02, Mob.GOLEM_APPRENTICE)
                    )
                    .add(5, new SimpleWave(Component.text("MEDIUM", NamedTextColor.YELLOW))
                            .add(0.6, Mob.ZOMBIE_LANCER)
                            .add(0.25, Mob.ZOMBIE_LAMENT)
                            .add(0.25, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.05, Mob.SKELETAL_MAGE)
                            .add(0.1, Mob.SLIMY_ANOMALY)
                            .add(0.02, Mob.CELESTIAL_OPUS)
                            .add(0.02, Mob.GOLEM_APPRENTICE)
                    )
                    .add(10, new SimpleWave(Component.text("HARD", NamedTextColor.GOLD))
                            .add(0.4, Mob.ZOMBIE_LANCER)
                            .add(0.3, Mob.ZOMBIE_LAMENT)
                            .add(0.3, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.02, Mob.SKELETAL_MESMER)
                            .add(0.02, Mob.OVERGROWN_ZOMBIE)
                            .add(0.02, Mob.CELESTIAL_SWORD_WIELDER)
                            .add(0.02, Mob.CELESTIAL_BOW_WIELDER)
                            .add(0.02, Mob.GOLEM_APPRENTICE)
                    )
                    .add(15, new SimpleWave(Component.text("INSANE", NamedTextColor.RED))
                            .add(0.4, Mob.ZOMBIE_LANCER)
                            .add(0.4, Mob.ZOMBIE_LAMENT)
                            .add(0.4, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.02, Mob.SKELETAL_MESMER)
                            .add(0.01, Mob.OVERGROWN_ZOMBIE)
                            .add(0.05, Mob.CELESTIAL_OPUS)
                            .add(0.02, Mob.CELESTIAL_SWORD_WIELDER)
                            .add(0.02, Mob.CELESTIAL_BOW_WIELDER)
                            .add(0.02, Mob.PIG_ALLEVIATOR)
                            .add(0.02, Mob.GOLEM_APPRENTICE)
                    )
                    .add(20, new SimpleWave(Component.text("EXTREME", NamedTextColor.DARK_RED))
                            .add(0.5, Mob.ZOMBIE_LAMENT)
                            .add(0.5, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.02, Mob.SKELETAL_MESMER)
                            .add(0.07, Mob.OVERGROWN_ZOMBIE)
                            .add(0.07, Mob.CELESTIAL_OPUS)
                            .add(0.07, Mob.CELESTIAL_SWORD_WIELDER)
                            .add(0.07, Mob.CELESTIAL_BOW_WIELDER)
                            .add(0.07, Mob.PIG_ALLEVIATOR)
                            .add(0.04, Mob.PIG_PARTICLE)
                            .add(0.07, Mob.GOLEM_APPRENTICE)
                    )
                    .add(25, new SimpleWave(Component.text("NIGHTMARE", NamedTextColor.LIGHT_PURPLE))
                            .add(0.3, Mob.SLIME_GUARD)
                            .add(0.5, Mob.ZOMBIE_SWORDSMAN)
                            .add(0.02, Mob.SKELETAL_MESMER)
                            .add(0.07, Mob.OVERGROWN_ZOMBIE)
                            .add(0.07, Mob.CELESTIAL_OPUS)
                            .add(0.07, Mob.CELESTIAL_SWORD_WIELDER)
                            .add(0.07, Mob.CELESTIAL_BOW_WIELDER)
                            .add(0.07, Mob.PIG_ALLEVIATOR)
                            .add(0.04, Mob.PIG_PARTICLE)
                            .add(0.07, Mob.GOLEM_APPRENTICE)
                            .add(0.07, Mob.ARACHNO_VENARI)
                    )
                    .add(30, new SimpleWave(Component.text("INSOMNIA", NamedTextColor.DARK_PURPLE))
                            .add(0.6, Mob.SLIME_GUARD)
                            .add(0.1, Mob.SCRUPULOUS_ZOMBIE)
                            .add(0.02, Mob.SKELETAL_MESMER)
                            .add(0.07, Mob.OVERGROWN_ZOMBIE)
                            .add(0.07, Mob.CELESTIAL_OPUS)
                            .add(0.07, Mob.CELESTIAL_SWORD_WIELDER)
                            .add(0.07, Mob.CELESTIAL_BOW_WIELDER)
                            .add(0.07, Mob.PIG_ALLEVIATOR)
                            .add(0.04, Mob.PIG_PARTICLE)
                            .add(0.07, Mob.GOLEM_APPRENTICE)
                            .add(0.07, Mob.ARACHNO_VENARI)
                    )
                    .add(35, new SimpleWave(0, 5 * SECOND, Component.text("VANGUARD", NamedTextColor.GRAY))
                            .add(0.4, Mob.SLIME_GUARD)
                            .add(0.1, Mob.SCRUPULOUS_ZOMBIE)
                            .add(0.02, Mob.SKELETAL_MESMER)
                            .add(0.07, Mob.OVERGROWN_ZOMBIE)
                            .add(0.07, Mob.CELESTIAL_OPUS)
                            .add(0.07, Mob.CELESTIAL_SWORD_WIELDER)
                            .add(0.07, Mob.CELESTIAL_BOW_WIELDER)
                            .add(0.05, Mob.PIG_ALLEVIATOR)
                            .add(0.02, Mob.PIG_PARTICLE)
                            .add(0.07, Mob.GOLEM_APPRENTICE)
                            .add(0.1, Mob.ZOMBIE_KNIGHT)
                            .add(0.1, Mob.RIFT_WALKER)
                    )
                    .add(40, new SimpleWave(0, 5 * SECOND, Component.text("DEMISE", NamedTextColor.RED, TextDecoration.BOLD))
                            .add(0.2, Mob.SLIME_GUARD)
                            .add(0.1, Mob.SCRUPULOUS_ZOMBIE)
                            .add(0.05, Mob.SKELETAL_MESMER)
                            .add(0.1, Mob.OVERGROWN_ZOMBIE)
                            .add(0.2, Mob.CELESTIAL_OPUS)
                            .add(0.07, Mob.CELESTIAL_SWORD_WIELDER)
                            .add(0.07, Mob.CELESTIAL_BOW_WIELDER)
                            .add(0.07, Mob.PIG_ALLEVIATOR)
                            .add(0.04, Mob.PIG_PARTICLE)
                            .add(0.1, Mob.GOLEM_APPRENTICE)
                            .add(0.2, Mob.ZOMBIE_KNIGHT)
                            .add(0.1, Mob.RIFT_WALKER)
                            .add(0.1, Mob.NIGHTMARE_ZOMBIE)
                    )
                    .add(45, new SimpleWave(0, 5 * SECOND, Component.text("??????", NamedTextColor.BLACK, TextDecoration.OBFUSCATED))
                            .add(0.3, Mob.SLIME_GUARD)
                            .add(0.1, Mob.SCRUPULOUS_ZOMBIE)
                            .add(0.05, Mob.SKELETAL_MESMER)
                            .add(0.1, Mob.OVERGROWN_ZOMBIE)
                            .add(0.2, Mob.CELESTIAL_OPUS)
                            .add(0.07, Mob.CELESTIAL_SWORD_WIELDER)
                            .add(0.07, Mob.CELESTIAL_BOW_WIELDER)
                            .add(0.05, Mob.PIG_ALLEVIATOR)
                            .add(0.02, Mob.PIG_PARTICLE)
                            .add(0.1, Mob.GOLEM_APPRENTICE)
                            .add(0.2, Mob.ZOMBIE_KNIGHT)
                            .add(0.1, Mob.RIFT_WALKER)
                            .add(0.2, Mob.NIGHTMARE_ZOMBIE)
                    )
            ));
            options.add(new ItemOption());
            options.add(new CoinGainOption()
                    .guildCoinInsigniaConvertBonus(1000)
            );
            options.add(new ExperienceGainOption()
                    .playerExpPer(160)
                    .guildExpPer(5)
            );

            return options;
        }

    },
    VOID_RIFT(
            "Void Rift",
            6,
            1,
            30 * SECOND,
            "VoidRift",
            3,
            GameMode.BOSS_RUSH
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(0.5, 68, 0.5), Team.BLUE).asOption());
            options.add(SpawnpointOption.forTeam(loc.addXYZ(0.5, 68, 0.5), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(0.5, 68, 0.5), Team.RED));

            options.add(new RespawnWaveOption(1, 20, 10));
            options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));
            options.add(new CurrencyOnEventOption()
                    .onPerWaveClear(1, 50000)
                    .startWith(50000)
            );
            options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                    .add(1, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.BOLTARO)
                    )
                    .add(2, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.GHOULCALLER)
                    )
                    .add(3, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.NARMER)
                    )
                    .add(4, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.MITHRA)
                    )
                    .add(5, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.ZENITH)
                    )
                    .add(6, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.CHESSKING)
                    )
                    .add(7, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.ILLUMINA)
                    )
                    .add(8, new SimpleWave(1, 10 * SECOND, Component.text("Boss"))
                            .add(Mob.TORMENT)
                    ),
                    DifficultyIndex.NORMAL
            ));
            options.add(new ItemOption());

            return options;
        }

    },
    TREASURE_HUNT(
            "Dual Descent",
            2,
            1,
            60 * SECOND,
            "TreasureHuntMap",
            3,
            GameMode.TREASURE_HUNT
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);

            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(0.5, 33, 0.5), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(0.5, 33, 0.5), Team.RED).asOption());

            options.add(SpawnpointOption.forTeam(loc.addXYZ(0.5, 33, 0.5), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(0.5, 33, 0.5), Team.RED));

            options.add(new BoundingBoxOption(new Location(loc.getWorld(), 0, 32, 0), new Location(loc.getWorld(), 255, 128, 255)));
            options.add(DungeonRoomMarker.create(
                    loc.getWorld(),
                    32, -48, -48,
                    47, -33, -33,
                    RoomType.START,
                    true,
                    true,
                    true,
                    true
            ).asOption());

            options.add(DungeonRoomMarker.create(
                    loc.getWorld(),
                    32, -48, -16,
                    47, -33, -1,
                    RoomType.END,
                    true,
                    true,
                    true,
                    true
            ).asOption());

            options.add(DungeonRoomMarker.create(
                    loc.getWorld(),
                    32, -48, 16,
                    47, -33, 31,
                    RoomType.TREASURE,
                    true,
                    true,
                    true,
                    true
            ).asOption());

            options.add(DungeonRoomMarker.create(
                    loc.getWorld(),
                    0, -48, 16,
                    15, -33, 31,
                    RoomType.NORMAL,
                    false,
                    true,
                    false,
                    true
            ).asOption());

            options.add(DungeonRoomMarker.create(
                    loc.getWorld(),
                    0, -48, -16,
                    15, -33, -1,
                    RoomType.NORMAL,
                    true,
                    false,
                    true,
                    false
            ).asOption());

            options.add(DungeonRoomMarker.create(
                    loc.getWorld(),
                    -32, -48, -48,
                    -17, -33, -33,
                    RoomType.NORMAL,
                    false,
                    false,
                    true,
                    true
            ).asOption());

            options.add(DungeonRoomMarker.create(
                    loc.getWorld(),
                    0, -48, -48,
                    15, -33, -33,
                    RoomType.NORMAL,
                    false,
                    true,
                    true,
                    false
            ).asOption());

            options.add(DungeonRoomMarker.create(
                    loc.getWorld(),
                    0, -48, -80,
                    15, -33, -65,
                    RoomType.NORMAL,
                    true,
                    true,
                    true,
                    false
            ).asOption());

            options.add(new GraveOption());
            options.add(new BasicScoreboardOption());
            options.add(new TreasureHuntOption(20));

            return options;
        }

    },
    PAYLOAD(
            "Payload",
            32,
            12,
            5 * SECOND,
            "Payload",
            3,
            GameMode.PAYLOAD
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);
            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());

            options.add(LobbyLocationMarker.create(loc.addXYZ(12.5, 1, -5.5, 90, 3), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(12.5, 1, -5.5, 90, 3), Team.RED).asOption());

            options.add(SpawnpointOption.forTeam(loc.addXYZ(12.5, 1, -5.5, 90, 3), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(12.5, 1, -5.5, 90, 3), Team.RED));

            World world = loc.getWorld();
            options.add(new PayloadOption(
                    loc.addXYZ(0.5, 0.5, -19.5),
                    new PayloadSpawns(Arrays.asList(
                            new Location(world, 4.5, 1, -25.5, 23, 35),
                            new Location(world, -5, 1, -25, -40, 32),
                            new Location(world, 11.5, 1, -17, 67, 18),
                            new Location(world, 8, 1, -10, 96, 17),
                            new Location(world, -5, 1, -5.5, -131, 10),
                            new Location(world, -14.5, 1, -12.5, -110, 12),
                            new Location(world, -17, 1, -19.5, -94, 12),
                            new Location(world, 5.5, 1, -30.5, 22, 10),
                            new Location(world, -7, 1, -32, -28, 14),
                            new Location(world, 8, 1, 2.5, 103, 24),
                            new Location(world, -0.5, 1, 7, 172, 21),
                            new Location(world, -10, 1, 12.5, -133, 11),
                            new Location(world, 3.5, 1, 18.5, 164, 16),
                            new Location(world, 14, 1, 16, 139, 15),
                            new Location(world, 15, 1, 9, 117, 14),
                            new Location(world, 18, 1, 0.5, 91, 19),
                            new Location(world, -13, 1, 14.5, -135, 5),
                            new Location(world, -7.5, 1, 20.5, -153, 0),
                            new Location(world, 4.5, 1, 22, 145, 10)
                    ), Arrays.asList(
                            new PayloadSpawns.TimedSpawnWave(20,
                                    new Pair<>(8, Mob.ZOMBIE_LANCER)
                            ),
                            new PayloadSpawns.TimedSpawnWave(30,
                                    new Pair<>(8, Mob.ZOMBIE_SWORDSMAN)
                            ),
                            new PayloadSpawns.TimedSpawnWave(60,
                                    new Pair<>(8, Mob.NIGHTMARE_ZOMBIE)
                            )
                    ), Arrays.asList(
                            new PayloadSpawns.PayloadSpawnWave(
                                    new Pair<>(8, Mob.ZOMBIE_LANCER),
                                    new Pair<>(8, Mob.PIG_DISCIPLE),
                                    new Pair<>(8, Mob.SKELETAL_MAGE),
                                    new Pair<>(8, Mob.BASIC_WARRIOR_BERSERKER)
                            ),
                            new PayloadSpawns.PayloadSpawnWave(
                                    new Pair<>(8, Mob.ZOMBIE_SWORDSMAN),
                                    new Pair<>(8, Mob.PIG_SHAMAN),
                                    new Pair<>(8, Mob.SKELETAL_WARLOCK),
                                    new Pair<>(8, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                            )
                    )),
                    Team.BLUE
            ));

            options.add(new RespawnWaveOption()); //TODO
            options.add(new RespawnProtectionOption());
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            options.add(new BoundingBoxOption(world, AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

            return options;
        }
    },
    PAYLOAD2(
            "Payload2",
            32,
            12,
            5 * SECOND,
            "Payload2",
            3,
            GameMode.SIEGE
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);
            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());

            options.add(LobbyLocationMarker.create(loc.addXYZ(0.5, 1, 18, 180, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(0.5, 1, -32.5, 0, 0), Team.RED).asOption());

            options.add(SpawnpointOption.forTeam(loc.addXYZ(0.5, 1, 18, 180, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(0.5, 1, -32.5, 0, 0), Team.RED));

            options.add(new PowerupOption(loc.addXYZ(10, 1.5, 0), PowerUp.PAYLOAD_BATTERY, 45, 10)); //120

            options.add(new SiegeOption(loc.addXYZ(0.5, 1, -7.5))
                    .addPayloadStart(Team.BLUE, loc.addXYZ(0.5, 1, -7.5, 180, 0))
                    .addPayloadStart(Team.RED, loc.addXYZ(0.5, 1, -7.5, 0, 0))
            );

            options.add(new GateOption(loc, -5, 1, 15, 5, 10, 15));
            options.add(new GateOption(loc, -5, 1, -30, 5, 10, -30));

            return options;
        }
    },
    GORGE_REMASTERED_SIEGE(
            "Gorge Remastered",
            12,
            8,
            60 * SECOND,
            "GorgeRemastered2",
            1,
            GameMode.SIEGE
    ) {
        @Override
        public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = category.initMap(this, loc, addons);
            options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());

            options.add(LobbyLocationMarker.create(loc.addXYZ(43.5, 76, -216.5, 90, 0), Team.BLUE).asOption());
            options.add(LobbyLocationMarker.create(loc.addXYZ(-134.5, 76, -216.5, -90, 0), Team.RED).asOption());

            options.add(SpawnpointOption.forTeam(loc.addXYZ(53.5, 82, -216.5, 90, 0), Team.BLUE));
            options.add(SpawnpointOption.forTeam(loc.addXYZ(-144.5, 82, -216.5, -90, 0), Team.RED));

            options.add(new GateOption(loc, -125.5, 81.5, -213.5, -125.5, 76, -219.5, Material.SPRUCE_FENCE));
            options.add(new GateOption(loc, 34.5, 80, -219.5, 34.5, 76, -213.5, Material.IRON_BARS));
            options.add(new GateOption(loc, -145.5, 76, -212.5, -143.5, 80, -212.5, Material.SPRUCE_FENCE));
            options.add(new GateOption(loc, 54.5, 76, -220.5, 52.5, 80, -220.5, Material.IRON_BARS));
            options.add(new GateOption(loc, -132.5, 76, -232.5, -132.5, 81, -234.5, Material.SPRUCE_FENCE));
            options.add(new GateOption(loc, 41.5, 76, -200.5, 41.5, 81, -198.5, Material.IRON_BARS));

//            options.add(SpawnpointOption.forTeam(loc.addXYZ(5.5, 71, -159.5, 135, 0), Team.BLUE));
//            options.add(SpawnpointOption.forTeam(loc.addXYZ(-97.5, 71, -274.5, -45, 0), Team.RED));

            options.add(new GateOption(loc, -125.5, 82.5, -213.5, -125.5, 76, -219.5, Material.SPRUCE_FENCE));
            options.add(new GateOption(loc, 34.5, 81, -219.5, 34.5, 76, -213.5, Material.IRON_BARS));

            options.add(new PowerupOption(loc.addXYZ(-2.5, 61.5, -236.5), PowerUp.ENERGY));
            options.add(new PowerupOption(loc.addXYZ(-88.5, 61.5, -196.5), PowerUp.ENERGY));

            options.add(new PowerupOption(loc.addXYZ(-152.5, 75.5, -208.5), PowerUp.SPEED));
            options.add(new PowerupOption(loc.addXYZ(60.5, 75.5, -224.5), PowerUp.SPEED));
            options.add(new PowerupOption(loc.addXYZ(-152.5, 76.5, -232.5), PowerUp.SPEED));
            options.add(new PowerupOption(loc.addXYZ(62.5, 76, -200.5), PowerUp.SPEED));

            options.add(new PowerupOption(loc.addXYZ(-12.5, 45.5, -194.5), PowerUp.HEALING));
            options.add(new PowerupOption(loc.addXYZ(-78.5, 45.5, -238.5), PowerUp.HEALING));

            options.add(new PowerupOption(loc.addXYZ(-32, 66.5, -165), PowerUp.PAYLOAD_BATTERY, 45, 120));
            options.add(new PowerupOption(loc.addXYZ(-59, 66.5, -268), PowerUp.PAYLOAD_BATTERY, 45, 120));

            options.add(new SiegeOption(loc.addXYZ(-45.5, 44, -216.5))
                    .addPayloadStart(Team.BLUE, loc.addXYZ(-45.5, 44, -216.5, 0, 0))
                    .addPayloadStart(Team.RED, loc.addXYZ(-45.5, 44, -216.5, 180, 0)));

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
        Set<String> addedMaps = new HashSet<>();
        for (GameMap map : VALUES) {
            if (map == MAIN_LOBBY) {
                gameManager.addGameHolder(map.fileName, map);
                continue;
            }
            for (int i = 0; i < map.numberOfMaps; i++) {
                String mapName = map.fileName + "-" + i;
                if (addedMaps.contains(mapName)) {
                    continue;
                }
                addedMaps.add(mapName);
                gameManager.addGameHolder(mapName, map);
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
        this.gameMode = List.of(gameMode);
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
