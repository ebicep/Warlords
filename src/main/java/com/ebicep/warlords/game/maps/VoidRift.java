package com.ebicep.warlords.game.maps;

import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;
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

public class VoidRift extends GameMap {

    public VoidRift() {
        super(
                "Void Rift",
                6,
                1,
                30 * SECOND,
                "VoidRift",
                3,
                GameMode.BOSS_RUSH
        );
    }

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
                .add(1, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.BOLTARO)
                )
                .add(2, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.GHOULCALLER)
                )
                .add(3, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.NARMER)
                )
                .add(4, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.MITHRA)
                )
                .add(5, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.ZENITH)
                )
                .add(6, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.CHESSKING)
                )
                .add(7, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.ILLUMINA)
                )
                .add(8, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.TORMENT)
                ),
                DifficultyIndex.NORMAL
        ));
        options.add(new ItemOption());

        return options;
    }

}