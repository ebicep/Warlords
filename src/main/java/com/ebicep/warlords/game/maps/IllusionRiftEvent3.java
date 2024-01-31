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
import com.ebicep.warlords.game.option.pve.wavedefense.events.EventPointsOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.SafeZoneOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffectOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.NarmersTombOption;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.RandomSpawnWave;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.StaticWaveList;
import com.ebicep.warlords.game.option.respawn.RespawnWaveOption;
import com.ebicep.warlords.game.option.win.WinAfterTimeoutOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public class IllusionRiftEvent3 extends GameMap {

    public IllusionRiftEvent3() {
        super(
                "Acolyte Archives",
                4,
                1,
                120 * SECOND,
                "IllusionRiftEvent3",
                1,
                GameMode.EVENT_WAVE_DEFENSE
        );
    }

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

        options.add(new PowerupOption(loc.addXYZ(16.5, 24.5, 17.5), PowerupOption.PowerUp.COOLDOWN, 180, 30));
        options.add(new PowerupOption(loc.addXYZ(-15.5, 24.5, -18.5), PowerupOption.PowerUp.HEALING, 90, 30));

        //options.add(new RespawnOption(20));
        options.add(new RespawnWaveOption(2, 1, 20));
        options.add(new GraveOption());

        options.add(new BasicScoreboardOption());
        options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

        options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                .add(1, new RandomSpawnWave(8, 5 * SECOND, null)
                        .add(0.4, Mob.ZOMBIE_LANCER)
                        .add(0.5, Mob.PIG_DISCIPLE)
                        .add(0.1, Mob.BASIC_WARRIOR_BERSERKER)
                )
                .add(2, new RandomSpawnWave(8, SECOND, null)
                        .add(0.4, Mob.ZOMBIE_LANCER)
                        .add(0.4, Mob.PIG_DISCIPLE)
                        .add(0.1, Mob.BASIC_WARRIOR_BERSERKER)
                        .add(0.1, Mob.ZOMBIE_LAMENT)
                )
                .add(4, new RandomSpawnWave(8, SECOND, null)
                        .add(0.4, Mob.ZOMBIE_LANCER)
                        .add(0.3, Mob.PIG_DISCIPLE)
                        .add(0.1, Mob.BASIC_WARRIOR_BERSERKER)
                        .add(0.1, Mob.ZOMBIE_LAMENT)
                        .add(0.1, Mob.GOLEM_APPRENTICE)
                )
                .add(5, new RandomSpawnWave(1, SECOND, Component.text("Boss"))
                        .add(Mob.EVENT_NARMER)
                )
                .add(6, new RandomSpawnWave(12, SECOND, null)
                        .add(0.4, Mob.ZOMBIE_LANCER)
                        .add(0.3, Mob.PIG_DISCIPLE)
                        .add(0.1, Mob.BASIC_WARRIOR_BERSERKER)
                        .add(0.1, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                        .add(0.05, Mob.ZOMBIE_LAMENT)
                        .add(0.05, Mob.GOLEM_APPRENTICE)
                )
                .add(7, new RandomSpawnWave(12, SECOND, null)
                        .add(0.3, Mob.ZOMBIE_LANCER)
                        .add(0.3, Mob.BASIC_WARRIOR_BERSERKER)
                        .add(0.1, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                        .add(0.1, Mob.ZOMBIE_LAMENT)
                        .add(0.1, Mob.PIG_SHAMAN)
                        .add(0.1, Mob.GOLEM_APPRENTICE)
                )
                .add(8, new RandomSpawnWave(12, SECOND, null)
                        .add(0.2, Mob.ZOMBIE_LANCER)
                        .add(0.2, Mob.BASIC_WARRIOR_BERSERKER)
                        .add(0.2, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                        .add(0.1, Mob.ZOMBIE_LAMENT)
                        .add(0.1, Mob.PIG_SHAMAN)
                        .add(0.1, Mob.GOLEM_APPRENTICE)
                        .add(0.1, Mob.ZOMBIE_SWORDSMAN)
                )
                .add(10, new RandomSpawnWave(1, SECOND, Component.text("Boss"))
                        .add(Mob.EVENT_NARMER)
                )
                .add(11, new RandomSpawnWave(16, SECOND, null)
                        .add(0.1, Mob.ZOMBIE_LANCER)
                        .add(0.1, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                        .add(0.1, Mob.ZOMBIE_LAMENT)
                        .add(0.1, Mob.PIG_SHAMAN)
                        .add(0.3, Mob.GOLEM_APPRENTICE)
                        .add(0.2, Mob.ZOMBIE_SWORDSMAN)
                        .add(0.05, Mob.ADVANCED_WARRIOR_BERSERKER)
                        .add(0.05, Mob.ZOMBIE_VANGUARD)
                )
                .add(12, new RandomSpawnWave(16, SECOND, null)
                        .add(0.1, Mob.ZOMBIE_LANCER)
                        .add(0.1, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                        .add(0.1, Mob.ZOMBIE_LAMENT)
                        .add(0.1, Mob.PIG_SHAMAN)
                        .add(0.02, Mob.GOLEM_APPRENTICE)
                        .add(0.01, Mob.ZOMBIE_SWORDSMAN)
                        .add(0.2, Mob.ADVANCED_WARRIOR_BERSERKER)
                        .add(0.1, Mob.ZOMBIE_VANGUARD)
                )
                .add(13, new RandomSpawnWave(16, SECOND, null)
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
                .add(15, new RandomSpawnWave(1, SECOND, Component.text("Boss"))
                        .add(Mob.EVENT_NARMER)
                )
                .add(16, new RandomSpawnWave(20, SECOND, null)
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
                .add(20, new RandomSpawnWave(1, SECOND, Component.text("Boss"))
                        .add(Mob.EVENT_NARMER)
                )
                .add(21, new RandomSpawnWave(20, SECOND, null)
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
                .add(25, new RandomSpawnWave(1, SECOND, Component.text("Boss"))
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

}