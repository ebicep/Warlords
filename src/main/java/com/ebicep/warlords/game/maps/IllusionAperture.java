package com.ebicep.warlords.game.maps;

import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.*;
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

public class IllusionAperture extends GameMap {

    public IllusionAperture() {
        super(
                "Illusion Aperture",
                4,
                1,
                60 * SECOND,
                "IllusionAperture",
                10,
                GameMode.WAVE_DEFENSE
        );
    }

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

        options.add(new PowerupOption(loc.addXYZ(618.5, 19.5, 223.5), PowerupOption.PowerUp.COOLDOWN, 180, 30));
        options.add(new PowerupOption(loc.addXYZ(581.5, 19.5, 250.5), PowerupOption.PowerUp.HEALING, 90, 30));

        options.add(new GraveOption());

        options.add(new BasicScoreboardOption());
        options.add(new BoundingBoxOption(loc.getWorld()));

        options.add(new RespawnWaveOption(1, 20, 20));
        options.add(new CurrencyOnEventOption()
                .onKill(1000)
        );
        options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                .add(1, new RandomSpawnWave(8, 10 * SECOND, null)
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
                .add(5, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.BOLTARO)
                )
                .add(6, new RandomSpawnWave(10, 10 * SECOND, null)
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
                .add(10, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.GHOULCALLER)
                )
                .add(11, new RandomSpawnWave(12, 10 * SECOND, null)
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
                .add(15, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.NARMER)
                )
                .add(16, new RandomSpawnWave(15, 10 * SECOND, null)
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
                .add(20, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.MITHRA)
                )
                .add(21, new RandomSpawnWave(18, 10 * SECOND, null)
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
                .add(25, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
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

}