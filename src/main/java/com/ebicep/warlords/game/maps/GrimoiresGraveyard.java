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
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.CoinGainOption;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.pve.wavedefense.WinByMaxWaveClearOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.EventPointsOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffectOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.GrimoiresGraveyardOption;
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
import com.ebicep.warlords.pve.mobs.events.libraryarchives.EventScriptedGrimoire;
import com.ebicep.warlords.pve.mobs.events.libraryarchives.EventTheArchivist;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public class GrimoiresGraveyard extends GameMap {

    public GrimoiresGraveyard() {
        super(
                "Grimoire’s Graveyard",
                6,
                2,
                120 * SECOND,
                "Grimoires",
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
        options.add(LobbyLocationMarker.create(loc.addXYZ(16, 27, 1.5), Team.BLUE).asOption()); //TODO
        options.add(LobbyLocationMarker.create(loc.addXYZ(16, 27, 1.5), Team.RED).asOption());

        options.add(SpawnpointOption.forTeam(loc.addXYZ(16, 27, 1.5), Team.BLUE)); //TODO

        options.add(SpawnpointOption.forTeam(loc.addXYZ(2.5, 27, 17.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-10.5, 27, 16.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-18.5, 27, 8.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-18.5, 27, -5.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-10.5, 27, -13.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(3.5, 27, -13.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(11.5, 27, -5.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(11.5, 27, 8.5), Team.RED));

        List<Location> necronomiconSpawnLocations = List.of(
                loc.addXYZ(-14.5, 24, 16.5, -90, 0),
                loc.addXYZ(7.5, 24, 16.5, 90, 0),
                loc.addXYZ(11.5, 24, 12.5, 180, 0),
                loc.addXYZ(11.5, 24, -9.5, 0, 0),
                loc.addXYZ(7.5, 24, -13.5, 90, 0),
                loc.addXYZ(-14.5, 24, -13.5, -90, 0),
                loc.addXYZ(-18.5, 24, -9.5, 0, 0),
                loc.addXYZ(-18.5, 24, 12.5, 180, 0)
        );

        options.add(new PowerupOption(loc.addXYZ(16, 25.5, 1.5), PowerupOption.PowerUp.DAMAGE, 180, 30));
        options.add(new PowerupOption(loc.addXYZ(-23.5, 25.5, 1.5), PowerupOption.PowerUp.DAMAGE, 180, 30));
        options.add(new PowerupOption(loc.addXYZ(-3.5, 25.5, 21.5), PowerupOption.PowerUp.HEALING, 90, 30));
        options.add(new PowerupOption(loc.addXYZ(-3.5, 25.5, -18.5), PowerupOption.PowerUp.HEALING, 90, 30));

        options.add(new RespawnWaveOption(2, 1, 20));
        options.add(new GraveOption());

        options.add(new BasicScoreboardOption());
        options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

        options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                .add(1, new RandomSpawnWave(8, 5 * SECOND, null)
                        .add(0.4, Mob.ZOMBIE_LANCER)
                        .add(0.4, Mob.PIG_DISCIPLE)
                        .add(0.2, Mob.SLIMY_ANOMALY)
                )
                .add(2, new RandomSpawnWave(8, 5 * SECOND, null)
                        .add(0.5, Mob.ZOMBIE_LANCER)
                        .add(0.4, Mob.PIG_DISCIPLE)
                        .add(0.1, Mob.ZOMBIE_SWORDSMAN)
                )
                .add(4, new RandomSpawnWave(10, 5 * SECOND, null)
                        .add(0.3, Mob.ZOMBIE_LANCER)
                        .add(0.3, Mob.PIG_DISCIPLE)
                        .add(0.1, Mob.SLIMY_ANOMALY)
                        .add(0.2, Mob.ZOMBIE_SWORDSMAN)
                        .add(0.1, Mob.EVENT_UNPUBLISHED_GRIMOIRE)
                )
                .add(5, new RandomSpawnWave(6, 5 * SECOND, Component.text("Boss"))
                        .add(1, 1, Mob.EVENT_ROUGE_GRIMOIRE)
                        .add(1, 1, Mob.EVENT_VIOLETTE_GRIMOIRE)
                        .add(1, 1, Mob.EVENT_BLEUE_GRIMOIRE)
                        .add(1, 1, Mob.EVENT_ORANGE_GRIMOIRE)
                        .add(1, 1, Mob.EVENT_UNPUBLISHED_GRIMOIRE)
                        .add(1, 1, Mob.EVENT_EMBELLISHED_GRIMOIRE, necronomiconSpawnLocations)
                )
                .add(6, new RandomSpawnWave(12, 5 * SECOND, null)
                        .add(0.3, Mob.PIG_DISCIPLE)
                        .add(0.1, Mob.SLIMY_ANOMALY)
                        .add(0.2, Mob.EVENT_UNPUBLISHED_GRIMOIRE)
                        .add(0.2, Mob.PIG_SHAMAN)
                        .add(0.1, Mob.SKELETAL_WARLOCK)
                        .add(0.1, Mob.EVENT_EMBELLISHED_GRIMOIRE)
                )
                .add(8, new RandomSpawnWave(12, 5 * SECOND, null)
                        .add(0.3, Mob.ZOMBIE_LAMENT)
                        .add(0.1, Mob.PIG_SHAMAN)
                        .add(0.1, Mob.SKELETAL_WARLOCK)
                        .add(0.2, Mob.EVENT_UNPUBLISHED_GRIMOIRE)
                        .add(0.2, Mob.EVENT_EMBELLISHED_GRIMOIRE)
                        .add(0.1, 1, Mob.EVENT_NECRONOMICON_GRIMOIRE, necronomiconSpawnLocations)
                )
                .add(10, new RandomSpawnWave(13, 5 * SECOND, Component.text("Boss"))
                        .add(1, 2, Mob.EVENT_ROUGE_GRIMOIRE)
                        .add(1, 2, Mob.EVENT_VIOLETTE_GRIMOIRE)
                        .add(1, 2, Mob.EVENT_BLEUE_GRIMOIRE)
                        .add(1, 2, Mob.EVENT_ORANGE_GRIMOIRE)
                        .add(1, 2, Mob.EVENT_UNPUBLISHED_GRIMOIRE)
                        .add(1, 2, Mob.EVENT_EMBELLISHED_GRIMOIRE)
                        .add(1, 1, Mob.EVENT_NECRONOMICON_GRIMOIRE, necronomiconSpawnLocations)
                )
                .add(11, new RandomSpawnWave(14, 5 * SECOND, null)
                        .add(0.05, Mob.ZOMBIE_LAMENT)
                        .add(0.05, Mob.SLIMY_ANOMALY)
                        .add(0.2, Mob.EVENT_UNPUBLISHED_GRIMOIRE)
                        .add(0.3, Mob.ARACHNO_VENARI)
                        .add(0.1, Mob.SKELETAL_ENTROPY)
                        .add(0.2, Mob.EVENT_EMBELLISHED_GRIMOIRE)
                        .add(0.1, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                )
                .add(13, new RandomSpawnWave(14, 5 * SECOND, null)
                        .add(0.05, Mob.ZOMBIE_LAMENT)
                        .add(0.05, Mob.SLIMY_ANOMALY)
                        .add(0.2, Mob.ARACHNO_VENARI)
                        .add(0.2, Mob.EVENT_UNPUBLISHED_GRIMOIRE)
                        .add(0.2, Mob.SKELETAL_ENTROPY)
                        .add(0.1, Mob.EVENT_EMBELLISHED_GRIMOIRE)
                        .add(0.1, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                        .add(0.1, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                )
                .add(15, new RandomSpawnWave(19, 5 * SECOND, Component.text("Boss"))
                        .add(1, 3, Mob.EVENT_ROUGE_GRIMOIRE)
                        .add(1, 3, Mob.EVENT_VIOLETTE_GRIMOIRE)
                        .add(1, 3, Mob.EVENT_BLEUE_GRIMOIRE)
                        .add(1, 3, Mob.EVENT_ORANGE_GRIMOIRE)
                        .add(1, 3, Mob.EVENT_UNPUBLISHED_GRIMOIRE)
                        .add(1, 3, Mob.EVENT_EMBELLISHED_GRIMOIRE)
                        .add(1, 1, Mob.EVENT_SCRIPTED_GRIMOIRE)
                )
                .add(16, new RandomSpawnWave(16, 5 * SECOND, null)
                        .add(0.1, Mob.ZOMBIE_LAMENT)
                        .add(0.1, Mob.ARACHNO_VENARI)
                        .add(0.1, Mob.EVENT_UNPUBLISHED_GRIMOIRE)
                        .add(0.2, Mob.EVENT_EMBELLISHED_GRIMOIRE)
                        .add(0.1, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                        .add(0.1, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                        .add(0.05, Mob.ZOMBIE_VANGUARD)
                        .add(0.05, Mob.SLIME_GUARD)
                        .add(0.2, 1, Mob.EVENT_NECRONOMICON_GRIMOIRE, necronomiconSpawnLocations)
                )
                .add(20, new RandomSpawnWave(26, 5 * SECOND, Component.text("Boss"))
                        .add(1, 4, Mob.EVENT_ROUGE_GRIMOIRE)
                        .add(1, 4, Mob.EVENT_VIOLETTE_GRIMOIRE)
                        .add(1, 4, Mob.EVENT_BLEUE_GRIMOIRE)
                        .add(1, 4, Mob.EVENT_ORANGE_GRIMOIRE)
                        .add(1, 3, Mob.EVENT_UNPUBLISHED_GRIMOIRE)
                        .add(1, 3, Mob.EVENT_EMBELLISHED_GRIMOIRE)
                        .add(1, 2, Mob.EVENT_SCRIPTED_GRIMOIRE)
                        .add(1, 2, Mob.EVENT_NECRONOMICON_GRIMOIRE, necronomiconSpawnLocations)
                )
                .add(21, new RandomSpawnWave(18, 5 * SECOND, null)
                        .add(0.2, Mob.EVENT_UNPUBLISHED_GRIMOIRE)
                        .add(0.2, Mob.EVENT_EMBELLISHED_GRIMOIRE)
                        .add(0.1, Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                        .add(0.1, Mob.ZOMBIE_VANGUARD)
                        .add(0.1, Mob.SLIME_GUARD)
                        .add(0.2, Mob.ILLUMINATION)
                        .add(0.1, Mob.FIRE_SPLITTER)
                )
                .add(25, new RandomSpawnWave(200, 5 * SECOND, Component.text("Boss")) {
                    final LocationBuilder spawnLocation = loc.addXYZ(-3.5, 25, 1.5);

                    EventTheArchivist archivist;

                    @Override
                    public void tick(PveOption pveOption, int ticksElapsed) {
                        if (ticksElapsed == 10 * 20) {
                            archivist = new EventTheArchivist(spawnLocation);
                            pveOption.spawnNewMob(archivist);
                        }
                        if (archivist == null || archivist.getWarlordsNPC().isAlive()) {
                            if (ticksElapsed % 340 == 0) {
                                Mob minionGrimoire = switch (ThreadLocalRandom.current().nextInt(4)) {
                                    case 0 -> Mob.EVENT_ROUGE_GRIMOIRE;
                                    case 1 -> Mob.EVENT_VIOLETTE_GRIMOIRE;
                                    case 2 -> Mob.EVENT_BLEUE_GRIMOIRE;
                                    default -> Mob.EVENT_ORANGE_GRIMOIRE;
                                };
                                pveOption.spawnNewMob(minionGrimoire.createMob(getSpawnLocation()));
                            }
                            if (ticksElapsed % 200 == 0) {
                                pveOption.spawnNewMob(new EventScriptedGrimoire(getSpawnLocation()));
                            }
                        } else {
                            if (archivist != null && pveOption instanceof WaveDefenseOption waveDefenseOption && pveOption.getMobs()
                                                                                                                          .stream()
                                                                                                                          .noneMatch(mob -> mob.getWarlordsNPC()
                                                                                                                                               .getTeam() == Team.RED)) {
                                waveDefenseOption.setSpawnCount(0);
                            }
                        }
                    }

                    private Location getSpawnLocation() {
                        double randomXOffset = ThreadLocalRandom.current().nextDouble(-2, 2);
                        double randomZOffset = ThreadLocalRandom.current().nextDouble(-2, 2);
                        if (randomXOffset == 0 && randomZOffset == 0) {
                            if (ThreadLocalRandom.current().nextBoolean()) {
                                randomXOffset = 1;
                            } else {
                                randomZOffset = 1;
                            }
                                }
                                return spawnLocation.clone().add(
                                        randomXOffset,
                                        -0.9,
                                        randomZOffset
                                );
                            }
                        }
                )
                ,
                DifficultyIndex.EVENT, 25
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
                    case 5 -> 1.9f;
                    case 6 -> 2.4f;
                    default -> 1;
                };
            }

            @Override
            protected void modifyStats(WarlordsNPC warlordsNPC) {
                warlordsNPC.getMob().onSpawn(this);

                int playerCount = playerCount();
                float healthMultiplier = switch (playerCount) {
                    case 3 -> 1.5f;
                    case 4 -> 2f;
                    case 5 -> 2.25f;
                    case 6 -> 2.5f;
                    default -> 1;
                };
                float damageMultiplier = playerCount >= 4 ? playerCount >= 6 ? 1.2f : 1.1f : 1f;
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
        options.add(new WinAfterTimeoutOption(900, 50, "spec"));
        options.add(new WinByMaxWaveClearOption());
        options.add(new GrimoiresGraveyardOption());
        options.add(new EventPointsOption()
                .reduceScoreOnAllDeath(30, Team.BLUE)
                .onPerWaveClear(1, 500)
                .onPerWaveClear(5, 2000)
                .onPerMobKill(Mob.ZOMBIE_LANCER, 5)
                .onPerMobKill(Mob.SKELETAL_ENTROPY, 5)
                .onPerMobKill(Mob.SLIMY_ANOMALY, 5)
                .onPerMobKill(Mob.PIG_DISCIPLE, 10)
                .onPerMobKill(Mob.EVENT_UNPUBLISHED_GRIMOIRE, 10)
                .onPerMobKill(Mob.ZOMBIE_LAMENT, 10)
                .onPerMobKill(Mob.ARACHNO_VENARI, 10)
                .onPerMobKill(Mob.ZOMBIE_SWORDSMAN, 15)
                .onPerMobKill(Mob.PIG_SHAMAN, 15)
                .onPerMobKill(Mob.SKELETAL_ENTROPY, 15)
                .onPerMobKill(Mob.SKELETAL_WARLOCK, 15)
                .onPerMobKill(Mob.INTERMEDIATE_WARRIOR_BERSERKER, 15)
                .onPerMobKill(Mob.ZOMBIE_VANGUARD, 20)
                .onPerMobKill(Mob.EVENT_EMBELLISHED_GRIMOIRE, 20)
                .onPerMobKill(Mob.SLIME_GUARD, 25)
                .onPerMobKill(Mob.ILLUMINATION, 25)
                .onPerMobKill(Mob.INTERMEDIATE_WARRIOR_BERSERKER, 25)
                .onPerMobKill(Mob.FIRE_SPLITTER, 40)
                .onPerMobKill(Mob.EVENT_SCRIPTED_GRIMOIRE, 150)
                .onPerMobKill(Mob.EVENT_NECRONOMICON_GRIMOIRE, 150)
                .onPerMobKill(Mob.EVENT_ROUGE_GRIMOIRE, 500)
                .onPerMobKill(Mob.EVENT_VIOLETTE_GRIMOIRE, 500)
                .onPerMobKill(Mob.EVENT_BLEUE_GRIMOIRE, 500)
                .onPerMobKill(Mob.EVENT_ORANGE_GRIMOIRE, 500)
                .onPerMobKill(Mob.EVENT_THE_ARCHIVIST, 2500)
        );
        options.add(new CurrencyOnEventOption()
                .startWith(120000)
                .onKill(500)
                .setPerWaveClear(5, 25000)
                .onPerWaveClear(1, 500)
                .onPerMobKill(Mob.ZOMBIE_LANCER, 5)
                .onPerMobKill(Mob.SKELETAL_ENTROPY, 5)
                .onPerMobKill(Mob.SLIMY_ANOMALY, 5)
                .onPerMobKill(Mob.PIG_DISCIPLE, 10)
                .onPerMobKill(Mob.EVENT_UNPUBLISHED_GRIMOIRE, 10)
                .onPerMobKill(Mob.ZOMBIE_LAMENT, 10)
                .onPerMobKill(Mob.ARACHNO_VENARI, 10)
                .onPerMobKill(Mob.ZOMBIE_SWORDSMAN, 15)
                .onPerMobKill(Mob.PIG_SHAMAN, 15)
                .onPerMobKill(Mob.SKELETAL_ENTROPY, 15)
                .onPerMobKill(Mob.SKELETAL_WARLOCK, 15)
                .onPerMobKill(Mob.INTERMEDIATE_WARRIOR_BERSERKER, 15)
                .onPerMobKill(Mob.ZOMBIE_VANGUARD, 20)
                .onPerMobKill(Mob.EVENT_EMBELLISHED_GRIMOIRE, 20)
                .onPerMobKill(Mob.SLIME_GUARD, 25)
                .onPerMobKill(Mob.ILLUMINATION, 25)
                .onPerMobKill(Mob.INTERMEDIATE_WARRIOR_BERSERKER, 25)
                .onPerMobKill(Mob.FIRE_SPLITTER, 40)
                .onPerMobKill(Mob.EVENT_SCRIPTED_GRIMOIRE, 150)
                .onPerMobKill(Mob.EVENT_NECRONOMICON_GRIMOIRE, 5000)
                .onPerMobKill(Mob.EVENT_ROUGE_GRIMOIRE, 5000)
                .onPerMobKill(Mob.EVENT_VIOLETTE_GRIMOIRE, 5000)
                .onPerMobKill(Mob.EVENT_BLEUE_GRIMOIRE, 5000)
                .onPerMobKill(Mob.EVENT_ORANGE_GRIMOIRE, 5000)
                .onPerMobKill(Mob.EVENT_THE_ARCHIVIST, 5000)
        );
        options.add(new CoinGainOption()
                .clearMobCoinValueAndSet("Bosses Killed", new LinkedHashMap<>() {{
                    put("Rouge Grimoire", 1000L);
                    put("Violette Grimoire", 1000L);
                    put("Bleue Grimoire", 1000L);
                    put("Orange Grimoire", 1000L);
                    put("Necronomicon Grimoire", 1000L);
                    put("The Archivist", 1000L);
                }})
                .playerCoinPerXSec(150, 10)
                .guildCoinInsigniaConvertBonus(1000)
                .guildCoinPerXSec(1, 3)
                .disableCoinConversionUpgrade()
        );
        options.add(new ExperienceGainOption()
                .playerExpPerXSec(10, 10)
                .guildExpPerXSec(20, 30)
        );
        options.add(new FieldEffectOption(options, FieldEffectOption.FieldEffect.ACCUMULATING_KNOWLEDGE));


        return options;
    }
}