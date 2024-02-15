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
import com.ebicep.warlords.game.option.pve.wavedefense.WinByMaxWaveClearOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.EventPointsOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffectOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.ForgottenCodexOption;
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

public class ForgottenCodex extends GameMap {

    public ForgottenCodex() {
        super(
                "Forgotten Codex",
                6,
                2,
                120 * SECOND,
                "Forgotten",
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
        options.add(LobbyLocationMarker.create(loc.addXYZ(-4.5, 35, 7.5), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(-4.5, 35, 7.5), Team.RED).asOption());

        options.add(SpawnpointOption.forTeam(loc.addXYZ(-4.5, 35, 7.5), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-4.5, 35, 7.5), Team.RED));

        options.add(new PowerupOption(loc.addXYZ(-4.5, 35.5, 7.5), PowerupOption.PowerUp.HEALING, 90, 30));

        options.add(new RespawnWaveOption(2, 1, 20));
        options.add(new GraveOption());

        options.add(new BasicScoreboardOption());
        options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

        options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                .add(1, new RandomSpawnWave(1, 5 * SECOND, Component.text("Boss"))
                        .add(1, Mob.EVENT_INQUISITEUR_EWA)
                        .add(1, Mob.EVENT_INQUISITEUR_EGA)
                        .add(1, Mob.EVENT_INQUISITEUR_VPA)
                ),
                DifficultyIndex.EVENT, 1
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
        options.add(new WinAfterTimeoutOption(600, 50, "spec"));
        options.add(new WinByMaxWaveClearOption());
        options.add(new EventPointsOption()
                .reduceScoreOnAllDeath(30, Team.BLUE)
                .onPerWaveClear(1, 500)
                .onPerWaveClear(5, 2000)
                .onPerMobKill(Mob.EVENT_UNPUBLISHED_GRIMOIRE, 10)
                .onPerMobKill(Mob.ZOMBIE_SWORDSMAN, 15)
                .onPerMobKill(Mob.PIG_SHAMAN, 15)
                .onPerMobKill(Mob.GOLEM_APPRENTICE, 15)
                .onPerMobKill(Mob.EVENT_EMBELLISHED_GRIMOIRE, 20)
                .onPerMobKill(Mob.EVENT_SCRIPTED_GRIMOIRE, 150)
                .onPerMobKill(Mob.EVENT_NECRONOMICON_GRIMOIRE, 150)
                .onPerMobKill(Mob.EVENT_ROUGE_GRIMOIRE, 500)
                .onPerMobKill(Mob.EVENT_VIOLETTE_GRIMOIRE, 500)
                .onPerMobKill(Mob.EVENT_BLEUE_GRIMOIRE, 500)
                .onPerMobKill(Mob.EVENT_ORANGE_GRIMOIRE, 500)
                .onPerMobKill(Mob.EVENT_INQUISITEUR_EWA, 10_000)
                .onPerMobKill(Mob.EVENT_INQUISITEUR_EGA, 10_000)
                .onPerMobKill(Mob.EVENT_INQUISITEUR_VPA, 10_000)
        );
        options.add(new CurrencyOnEventOption()
                .startWith(750000)
                .onKill(500)
                .onPerMobKill(Mob.EVENT_UNPUBLISHED_GRIMOIRE, 10)
                .onPerMobKill(Mob.ZOMBIE_SWORDSMAN, 15)
                .onPerMobKill(Mob.PIG_SHAMAN, 15)
                .onPerMobKill(Mob.GOLEM_APPRENTICE, 15)
                .onPerMobKill(Mob.EVENT_SCRIPTED_GRIMOIRE, 150)
                .onPerMobKill(Mob.EVENT_NECRONOMICON_GRIMOIRE, 5000)
                .onPerMobKill(Mob.EVENT_ROUGE_GRIMOIRE, 5000)
                .onPerMobKill(Mob.EVENT_VIOLETTE_GRIMOIRE, 5000)
                .onPerMobKill(Mob.EVENT_BLEUE_GRIMOIRE, 5000)
                .onPerMobKill(Mob.EVENT_ORANGE_GRIMOIRE, 5000)
                .onPerMobKill(Mob.EVENT_INQUISITEUR_EWA, 20_000)
                .onPerMobKill(Mob.EVENT_INQUISITEUR_EGA, 20_000)
                .onPerMobKill(Mob.EVENT_INQUISITEUR_VPA, 20_000)
        );
        options.add(new CoinGainOption()
                .clearMobCoinValueAndSet("Greek Gods Killed", new LinkedHashMap<>() {{
                    put("Rouge Grimoire", 1000L);
                    put("Violette Grimoire", 1000L);
                    put("Bleue Grimoire", 1000L);
                    put("Orange Grimoire", 1000L);
                    put("Necronomicon Grimoire", 1000L);
                    put("Inquisiteur-EWA", 15000L);
                    put("Inquisiteur-EGA", 15000L);
                    put("Inquisiteur-VPA", 15000L);
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
        options.add(new FieldEffectOption(options, FieldEffectOption.FieldEffect.CODEX_COLLECTOR));
        options.add(new ForgottenCodexOption());

        return options;
    }
}