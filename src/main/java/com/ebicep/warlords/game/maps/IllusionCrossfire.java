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

public class IllusionCrossfire extends GameMap {

    public IllusionCrossfire() {
        super(
                "Illusion Crossfire",
                6,
                1,
                120 * SECOND,
                "IllusionCrossfire",
                7,
                GameMode.WAVE_DEFENSE
        );
    }

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
                .add(1, new RandomSpawnWave(10, 10 * SECOND, null)
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
                .add(5, new RandomSpawnWave(10, 10 * SECOND, null)
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
                .add(10, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.BOLTARO)
                )
                .add(11, new RandomSpawnWave(10, 10 * SECOND, null)
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
                .add(15, new RandomSpawnWave(20, 10 * SECOND, null)
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
                .add(20, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.GHOULCALLER)
                )
                .add(21, new RandomSpawnWave(25, 10 * SECOND, null)
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
                .add(25, new RandomSpawnWave(25, 10 * SECOND, null)
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
                .add(30, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.NARMER)
                )
                .add(31, new RandomSpawnWave(30, 10 * SECOND, null)
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
                .add(35, new RandomSpawnWave(30, 10 * SECOND, null)
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
                .add(40, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.MITHRA)
                )
                .add(41, new RandomSpawnWave(35, 10 * SECOND, null)
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
                .add(45, new RandomSpawnWave(35, 10 * SECOND, null)
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
                .add(50, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.ZENITH)
                )
                .add(51, new RandomSpawnWave(35, 5 * SECOND, null)
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
                .add(55, new RandomSpawnWave(35, 5 * SECOND, null)
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
                .add(60, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.CHESSKING)
                )
                .add(61, new RandomSpawnWave(40, 5 * SECOND, null)
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
                .add(65, new RandomSpawnWave(40, 5 * SECOND, null)
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
                .add(70, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.ILLUMINA)
                )
                .add(71, new RandomSpawnWave(45, 5 * SECOND, null)
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
                .add(75, new RandomSpawnWave(45, 5 * SECOND, null)
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
                .add(80, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.MAGMATIC_OOZE)
                )
                .add(81, new RandomSpawnWave(50, 5 * SECOND, null)
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
                .add(85, new RandomSpawnWave(50, 5 * SECOND, null)
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
                .add(90, new RandomSpawnWave(2, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.MITHRA)
                )
                .add(91, new RandomSpawnWave(50, 5 * SECOND, null)
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
                .add(95, new RandomSpawnWave(50, 5 * SECOND, null)
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
                .add(100, new RandomSpawnWave(1, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.VOID)
                )
                .add(101, new RandomSpawnWave(80, 2 * SECOND, null)
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
                .add(102, new RandomSpawnWave(80, 2 * SECOND, null)
                        .add(0.1, Mob.ILLUMINATION)
                        .add(1, Mob.GOLEM_APPRENTICE)
                        .add(0.1, Mob.WITCH_DEACON)
                )
                .add(103, new RandomSpawnWave(80, 2 * SECOND, null)
                        .add(0.2, Mob.ARACHNO_VENARI)
                        .add(1, Mob.SKELETAL_MESMER)
                )
                .add(104, new RandomSpawnWave(80, 2 * SECOND, null)
                        .add(0.5, Mob.SLIMY_ANOMALY)
                        .add(0.2, Mob.ILLUMINATION)
                        .add(0.2, Mob.SLIMY_CHESS)
                )
                .add(105, new RandomSpawnWave(80, 2 * SECOND, null)
                        .add(0.5, Mob.ARACHNO_VENARI)
                )
                .add(106, new RandomSpawnWave(20, 2 * SECOND, null)
                        .add(0.5, Mob.CELESTIAL_OPUS)
                )
                .add(107, new RandomSpawnWave(5, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.BOLTARO)
                )
                .add(108, new RandomSpawnWave(3, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.ILLUMINA)
                )
                .add(109, new RandomSpawnWave(3, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.NARMER)
                )
                .add(110, new RandomSpawnWave(5, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.GHOULCALLER)
                )
                .add(111, new RandomSpawnWave(5, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.ZENITH)
                )
                .add(112, new RandomSpawnWave(5, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.MITHRA)
                )
                .add(113, new RandomSpawnWave(3, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.CHESSKING)
                )
                .add(114, new RandomSpawnWave(3, 10 * SECOND, Component.text("Boss"))
                        .add(Mob.VOID)
                )
                .add(115, new RandomSpawnWave(50, 2 * SECOND, null)
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
}