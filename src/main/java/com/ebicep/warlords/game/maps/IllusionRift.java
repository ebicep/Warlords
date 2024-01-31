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
import com.ebicep.warlords.game.option.pve.ItemOption;
import com.ebicep.warlords.game.option.pve.rewards.CoinGainOption;
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

public class IllusionRift extends GameMap {

    public IllusionRift() {
        super(
                "Illusion Rift",
                4,
                1,
                120 * SECOND,
                "IllusionRift",
                10,
                GameMode.WAVE_DEFENSE
        );
    }

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

        options.add(new PowerupOption(loc.addXYZ(16.5, 24.5, 17.5), PowerupOption.PowerUp.COOLDOWN, 180, 30));
        options.add(new PowerupOption(loc.addXYZ(-15.5, 24.5, -18.5), PowerupOption.PowerUp.HEALING, 90, 30));

        options.add(new RespawnWaveOption(1, 20, 20));
        options.add(new GraveOption());

        options.add(new BasicScoreboardOption());
        options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

        options.add(new CurrencyOnEventOption()
                .onKill(500)
        );
        options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                .add(1, new RandomSpawnWave(12, 10 * SECOND, null)
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
                .add(5, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.BOLTARO)
                )
                .add(6, new RandomSpawnWave(16, 10 * SECOND, null)
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
                .add(10, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.GHOULCALLER)
                )
                .add(11, new RandomSpawnWave(18, 10 * SECOND, null)
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
                .add(15, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.NARMER)
                )
                .add(16, new RandomSpawnWave(20, 10 * SECOND, null)
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
                .add(20, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.MITHRA)
                )
                .add(21, new RandomSpawnWave(25, 10 * SECOND, null)
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
                .add(25, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
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

}