package com.ebicep.warlords.game.maps;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.*;
import com.ebicep.warlords.game.option.*;
import com.ebicep.warlords.game.option.cuboid.AbstractCuboidOption;
import com.ebicep.warlords.game.option.cuboid.BoundingBoxOption;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.option.pve.CurrencyOnEventOption;
import com.ebicep.warlords.game.option.pve.ItemOption;
import com.ebicep.warlords.game.option.pve.rewards.CoinGainOption;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.EventPointsOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffectOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.TheAcropolisOption;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.RandomSpawnWave;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.StaticWaveList;
import com.ebicep.warlords.game.option.respawn.RespawnWaveOption;
import com.ebicep.warlords.game.option.win.WinAfterTimeoutOption;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public class Acropolis extends GameMap {

    public Acropolis() {
        super(
                "Acropolis",
                4,
                1,
                120 * SECOND,
                "Acropolis",
                3,
                GameMode.EVENT_WAVE_DEFENSE
        );
    }

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

        options.add(new PowerupOption(loc.addXYZ(14.5, 24.5, 16.5), PowerupOption.PowerUp.COOLDOWN, 180, 30));
        options.add(new PowerupOption(loc.addXYZ(-13.5, 24.5, -23.5), PowerupOption.PowerUp.HEALING, 90, 30));

        options.add(new RespawnWaveOption(2, 1, 20));
        options.add(new GraveOption());

        options.add(new BasicScoreboardOption());
        options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

        options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                .add(1, new RandomSpawnWave(8, 5 * SECOND, null)
                        .add(0.4, Mob.ZOMBIE_LANCER)
                        .add(0.5, Mob.PIG_DISCIPLE)
                        .add(0.1, Mob.ZOMBIE_LAMENT)
                )
                .add(2, new RandomSpawnWave(8, 5 * SECOND, null)
                        .add(0.4, Mob.ZOMBIE_LANCER)
                        .add(0.4, Mob.PIG_DISCIPLE)
                        .add(0.2, Mob.ZOMBIE_LAMENT)
                )
                .add(4, new RandomSpawnWave(8, 5 * SECOND, null)
                        .add(0.4, Mob.ZOMBIE_LANCER)
                        .add(0.3, Mob.PIG_DISCIPLE)
                        .add(0.2, Mob.ARACHNO_VENARI)
                        .add(0.05, Mob.SKELETAL_WARLOCK)
                        .add(0.05, Mob.PIG_SHAMAN)
                )
                .add(5, new RandomSpawnWave(1, 5 * SECOND, Component.text("Boss"))
                        .add(1, Mob.EVENT_APOLLO)
                )
                .add(6, new RandomSpawnWave(12, 5 * SECOND, null)
                        .add(0.4, Mob.ZOMBIE_LANCER)
                        .add(0.2, Mob.PIG_DISCIPLE)
                        .add(0.2, Mob.ARACHNO_VENARI)
                        .add(0.1, Mob.ZOMBIE_LAMENT)
                        .add(0.05, Mob.SKELETAL_WARLOCK)
                        .add(0.05, Mob.PIG_ALLEVIATOR)
                )
                .add(8, new RandomSpawnWave(12, 5 * SECOND, null)
                        .add(0.2, Mob.ZOMBIE_LANCER)
                        .add(0.2, Mob.PIG_DISCIPLE)
                        .add(0.2, Mob.ARACHNO_VENARI)
                        .add(0.1, Mob.ZOMBIE_LAMENT)
                        .add(0.2, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                        .add(0.05, Mob.PIG_ALLEVIATOR)
                        .add(0.05, Mob.ZOMBIE_VANGUARD)
                )
                .add(10, new RandomSpawnWave(1, 5 * SECOND, Component.text("Boss"))
                        .add(1, Mob.EVENT_ARES)
                )
                .add(11, new RandomSpawnWave(16, 5 * SECOND, null)
                        .add(0.1, Mob.ZOMBIE_LANCER)
                        .add(0.1, Mob.PIG_DISCIPLE)
                        .add(0.2, Mob.ZOMBIE_LAMENT)
                        .add(0.2, Mob.ARACHNO_VENARI)
                        .add(0.1, Mob.PIG_SHAMAN)
                        .add(0.2, Mob.PIG_ALLEVIATOR)
                        .add(0.05, Mob.ZOMBIE_VANGUARD)
                        .add(0.05, Mob.SLIME_GUARD)
                )
                .add(13, new RandomSpawnWave(16, 5 * SECOND, null)
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
                .add(15, new RandomSpawnWave(1, 5 * SECOND, Component.text("Boss"))
                        .add(1, Mob.EVENT_PROMETHEUS)
                )
                .add(16, new RandomSpawnWave(20, 5 * SECOND, null)
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
                .add(20, new RandomSpawnWave(1, 5 * SECOND, Component.text("Boss"))
                        .add(1, Mob.EVENT_ATHENA)
                )
                .add(21, new RandomSpawnWave(24, 5 * SECOND, null)
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
                .add(25, new RandomSpawnWave(1, 5 * SECOND, Component.text("Boss"))
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
                warlordsNPC.setMaxHealthAndHeal(newBaseHealth);
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

}