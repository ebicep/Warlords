package com.ebicep.warlords.game.maps;

import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.*;
import com.ebicep.warlords.game.option.cuboid.AbstractCuboidOption;
import com.ebicep.warlords.game.option.cuboid.BoundingBoxOption;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.pve.CurrencyOnEventOption;
import com.ebicep.warlords.game.option.pve.NewItemOption;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.TartarusOption;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.RandomSpawnWave;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.StaticWaveList;
import com.ebicep.warlords.game.option.respawn.RespawnWaveOption;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.bukkit.LocationFactory;

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
                3,
                GameMode.PVE_DEBUG
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);

        options.add(TeamMarker.create(Team.BLUE, Team.RED).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(112.5, 11, 77.5), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(129.5, 12, 45.5), Team.RED).asOption());

        options.add(new PowerupOption(loc.addXYZ(114.5, 12.5, 67.5), PowerupOption.PowerUp.SELF_DAMAGE, 5, 5));
        options.add(new PowerupOption(loc.addXYZ(110.5, 12.5, 67.5), PowerupOption.PowerUp.SELF_HEAL, 5, 5));

        options.add(SpawnpointOption.forTeam(loc.addXYZ(112.5, 11, 77.5), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(129.5, 12, 45.5), Team.RED));
        options.add(new DummySpawnOption(loc.addXYZ(104.5, 11, 53.5), Team.RED));
        options.add(new DummySpawnOption(loc.addXYZ(97.5, 11, 62.5), Team.RED));
        options.add(new DummySpawnOption(loc.addXYZ(95.5, 12, 45.5), Team.RED));
        options.add(new DummySpawnOption(loc.addXYZ(101.5, 11, 62.5), Team.BLUE));
        options.add(new DummySpawnOption(loc.addXYZ(87.5, 11, 56.5), Team.BLUE));

        options.add(new BasicScoreboardOption());
        options.add(new RespawnWaveOption(1, 20, 10));
        options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));
        options.add(new CurrencyOnEventOption()
                .onPerWaveClear(1, 3000000)
                .startWith(5000000)
        );
        options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                .add(1, new RandomSpawnWave(50, 30 * SECOND, null)
                        .add(Mob.ZOMBIE_LANCER)
                )
                ,

                DifficultyIndex.NORMAL,
                100
        ));
        options.add(new NewItemOption());
        options.add(new TartarusOption());

        return options;
    }

}
