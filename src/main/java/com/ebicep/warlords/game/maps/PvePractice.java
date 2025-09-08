package com.ebicep.warlords.game.maps;

import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.BasicScoreboardOption;
import com.ebicep.warlords.game.option.DummySpawnOption;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.SpawnpointOption;
import com.ebicep.warlords.game.option.cuboid.AbstractCuboidOption;
import com.ebicep.warlords.game.option.cuboid.BoundingBoxOption;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.pve.CurrencyOnEventOption;
import com.ebicep.warlords.game.option.pve.ItemOption;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.RandomSpawnWave;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.StaticWaveList;
import com.ebicep.warlords.game.option.respawn.RespawnWaveOption;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import net.kyori.adventure.text.Component;

import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public class PvePractice extends GameMap {

    public PvePractice() {
        super(
                "Void Crossfire",
                10,
                1,
                30 * SECOND,
                "VoidCrossfire",
                1,
                GameMode.PVE_DEBUG
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(112.5, 11, 77.5), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(112.5, 11, 77.5), Team.RED).asOption());

        options.add(new DummySpawnOption(loc.addXYZ(104.5, 11, 53.5), Team.RED));
        options.add(new DummySpawnOption(loc.addXYZ(129.5, 12, 45.5), Team.RED));
        options.add(new DummySpawnOption(loc.addXYZ(97.5, 11, 62.5), Team.RED));
        options.add(new DummySpawnOption(loc.addXYZ(95.5, 12, 45.5), Team.RED));

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

        options.add(new BasicScoreboardOption());
        options.add(new RespawnWaveOption(1, 20, 10));
        options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));
        options.add(new CurrencyOnEventOption()
                .onPerWaveClear(1, 3000000)
                .startWith(5000000)
        );
        options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                .add(1, new RandomSpawnWave(1, 10 * SECOND, null)
                        .add(Mob.WITCH_DEACON)
                )
                ,

                DifficultyIndex.NORMAL
        ));
        options.add(new ItemOption());

        return options;
    }

}
