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
import com.ebicep.warlords.game.option.pve.CurrencyOnEventOption;
import com.ebicep.warlords.game.option.pve.NewItemOption;
import com.ebicep.warlords.game.option.pve.darkdescent.DarkDescentOption;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.RandomSpawnWave;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.StaticWaveList;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public class DarkDescentMap extends GameMap {

    private static final int[] FLOOR_Y = {
            240, 228, 216, 204, 192, 180, 168, 156, 144, 132,
            120, 108, 96, 84, 72, 60, 48, 36, 24, 12
    };
    private static final List<Mob> MINI_BOSSES = List.of(
            Mob.LANTERN_DREDGER,
            Mob.BARNACLE_BRUTE,
            Mob.ABYSS_WATCHER,
            Mob.VOID_JAILER
    );

    public DarkDescentMap() {
        super("Dark Descent", 1, 1, 30 * SECOND, "DarkDescent", 3, GameMode.DARK_DESCENT);
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);
        List<Location> floorCenters = Arrays.stream(FLOOR_Y).<Location>mapToObj(y -> loc.addXYZ(0.5, y, 0.5)).toList();
        Location spawn = floorCenters.getFirst();

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(spawn, Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(spawn, Team.RED).asOption());
        options.add(SpawnpointOption.forTeam(spawn, Team.BLUE));
        options.add(SpawnpointOption.forTeam(spawn, Team.RED));
        options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));
        options.add(new GraveOption());
        options.add(new BasicScoreboardOption());
        options.add(new NewItemOption());
        options.add(new CurrencyOnEventOption()
                .onKill(0)
                .setPerWaveClear(1, 10_000)
        );
        // floors without an entry reuse the previous floor's table
        options.add(new DarkDescentOption(floorCenters, new StaticWaveList()
                .add(1, new RandomSpawnWave(8, 5 * SECOND, null)
                        .add(0.5, Mob.ZOMBIE_LANCER)
                        .add(0.2, Mob.SLIMY_ANOMALY)
                        .add(0.2, Mob.ARACHNO_VENARI)
                        .add(0.1, Mob.SKELETAL_MAGE)
                )
                .add(4, new RandomSpawnWave(10, 5 * SECOND, null)
                        .add(0.4, Mob.ZOMBIE_LANCER)
                        .add(0.2, Mob.PIG_DISCIPLE)
                        .add(0.2, Mob.ZOMBIE_SWORDSMAN)
                        .add(0.1, Mob.SKELETAL_WARLOCK)
                        .add(0.1, Mob.GOLEM_APPRENTICE)
                )
                .add(7, new RandomSpawnWave(12, 5 * SECOND, null)
                        .add(0.3, Mob.ZOMBIE_SWORDSMAN)
                        .add(0.2, Mob.PIG_SHAMAN)
                        .add(0.2, Mob.SKELETAL_WARLOCK)
                        .add(0.2, Mob.GOLEM_APPRENTICE)
                        .add(0.1, Mob.WITCH_DEACON)
                )
                .add(11, new RandomSpawnWave(14, 5 * SECOND, null)
                        .add(0.3, Mob.ZOMBIE_VANGUARD)
                        .add(0.2, Mob.SKELETAL_ENTROPY)
                        .add(0.2, Mob.ZOMBIE_KNIGHT)
                        .add(0.2, Mob.VOID_ZOMBIE)
                        .add(0.1, Mob.SKELETAL_MESMER)
                )
                .add(14, new RandomSpawnWave(16, 5 * SECOND, null)
                        .add(0.3, Mob.ZOMBIE_KNIGHT)
                        .add(0.2, Mob.SKELETAL_SORCERER)
                        .add(0.2, Mob.VOID_ZOMBIE)
                        .add(0.2, Mob.SKELETAL_MESMER)
                        .add(0.1, Mob.FIRE_SPLITTER)
                )
                .add(17, new RandomSpawnWave(18, 5 * SECOND, null)
                        .add(0.3, Mob.SKELETAL_MESMER)
                        .add(0.3, Mob.FIRE_SPLITTER)
                        .add(0.3, Mob.RIFT_WALKER)
                        .add(0.1, Mob.SKELETAL_SORCERER)
                ),
                MINI_BOSSES,
                Map.of(
                        10, Mob.UMBRA_DESCENDANT,
                        20, Mob.ABYSS_DESCENDANT
                )
        ));
        return options;
    }
}
