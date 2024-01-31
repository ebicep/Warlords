package com.ebicep.warlords.game.maps;

import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.BasicScoreboardOption;
import com.ebicep.warlords.game.option.GraveOption;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.SpawnpointOption;
import com.ebicep.warlords.game.option.cuboid.AbstractCuboidOption;
import com.ebicep.warlords.game.option.cuboid.BoundingBoxOption;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.payload.PayloadOption;
import com.ebicep.warlords.game.option.payload.PayloadSpawns;
import com.ebicep.warlords.game.option.respawn.RespawnProtectionOption;
import com.ebicep.warlords.game.option.respawn.RespawnWaveOption;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public class Payload extends GameMap {

    public Payload() {
        super(
                "Payload",
                32,
                12,
                5 * SECOND,
                "Payload",
                3,
                GameMode.PAYLOAD
        );
    }

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
}