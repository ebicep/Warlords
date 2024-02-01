package com.ebicep.warlords.game.maps;

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
import com.ebicep.warlords.game.option.pve.wavedefense.events.SafeZoneOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffectOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.TheBorderlineOfIllusionEvent;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.RandomSpawnWave;
import com.ebicep.warlords.game.option.pve.wavedefense.waves.StaticWaveList;
import com.ebicep.warlords.game.option.respawn.RespawnWaveOption;
import com.ebicep.warlords.game.option.win.WinAfterTimeoutOption;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public class TheBorderlineOfIllusion extends GameMap {

    public TheBorderlineOfIllusion() {
        super(
                "The Borderline of Illusion",
                4,
                1,
                120 * SECOND,
                "IllusionRiftEvent5",
                1,
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
        options.add(LobbyLocationMarker.create(loc.addXYZ(7.5, 22, 0.5), Team.BLUE).asOption());
        options.add(LobbyLocationMarker.create(loc.addXYZ(7.5, 22, 0.5), Team.RED).asOption());

        options.add(SpawnpointOption.forTeam(loc.addXYZ(7.5, 22, 0.5), Team.BLUE));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-9.5, 22, 0.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(7.5, 22, 0.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-17.5, 22, -4.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(6.5, 22, -7.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(8.5, 22, 6.5), Team.RED));
        options.add(SpawnpointOption.forTeam(loc.addXYZ(-6.5, 22, -6.5), Team.RED));

        options.add(new PowerupOption(loc.addXYZ(16.5, 24.5, 17.5), PowerupOption.PowerUp.COOLDOWN, 180, 30));
        options.add(new PowerupOption(loc.addXYZ(-15.5, 24.5, -18.5), PowerupOption.PowerUp.HEALING, 90, 30));

        //options.add(new RespawnOption(20));
        options.add(new RespawnWaveOption(2, 1, 20));
        options.add(new GraveOption());

        options.add(new BasicScoreboardOption());
        options.add(new BoundingBoxOption(loc.getWorld(), AbstractCuboidOption.MAX_WORLD_SIZE_MINI));

        Location bossSpawnLocation = new Location(loc.getWorld(), 0.5, 24.5, -0.5);
        options.add(new WaveDefenseOption(Team.RED, new StaticWaveList()
                .add(1, new RandomSpawnWave(16, 8 * SECOND, null)
                        .add(0.4, Mob.ZOMBIE_LAMENT)
                        .add(0.1, Mob.SLIMY_ANOMALY)
                        .add(0.2, Mob.SKELETAL_WARLOCK)
                        .add(0.1, Mob.PIG_SHAMAN)
                        .add(0.2, Mob.SLIME_GUARD)
                )
                .add(4, new RandomSpawnWave(16, 8 * SECOND, null)
                        .add(0.3, Mob.ZOMBIE_LAMENT)
                        .add(0.1, Mob.ADVANCED_WARRIOR_BERSERKER)
                        .add(0.2, Mob.ZOMBIE_SWORDSMAN)
                        .add(0.2, Mob.OVERGROWN_ZOMBIE)
                        .add(0.1, Mob.FIRE_SPLITTER)
                        .add(0.1, Mob.SKELETAL_MESMER)
                )
                .add(9, new RandomSpawnWave(16, 8 * SECOND, null)
                        .add(0.3, Mob.OVERGROWN_ZOMBIE)
                        .add(0.1, Mob.ADVANCED_WARRIOR_BERSERKER)
                        .add(0.1, Mob.SLIMY_ANOMALY)
                        .add(0.1, Mob.FIRE_SPLITTER)
                        .add(0.2, Mob.PIG_SHAMAN)
                        .add(0.2, Mob.ZOMBIE_VANGUARD)
                )
                .add(10, new RandomSpawnWave(1, 8 * SECOND, Component.text("Boss"))
                        .add(1, Mob.EVENT_ILLUSION_CORE, bossSpawnLocation)
                )
                .add(11, new RandomSpawnWave(15, 8 * SECOND, null)
                        .add(0.2, Mob.OVERGROWN_ZOMBIE)
                        .add(0.1, Mob.ADVANCED_WARRIOR_BERSERKER)
                        .add(0.2, Mob.SLIMY_ANOMALY)
                        .add(0.1, Mob.PIG_ALLEVIATOR)
                        .add(0.2, Mob.ZOMBIE_LAMENT)
                )
                .add(14, new RandomSpawnWave(18, 8 * SECOND, null)
                        .add(0.1, Mob.PIG_ALLEVIATOR)
                        .add(0.1, Mob.PIG_DISCIPLE)
                        .add(0.2, Mob.ARACHNO_VENARI)
                        .add(0.1, Mob.ZOMBIE_LAMENT)
                        .add(0.2, Mob.PIG_SHAMAN)
                        .add(0.3, Mob.SLIMY_ANOMALY)
                )
                .add(19, new RandomSpawnWave(20, 8 * SECOND, null)
                        .add(0.3, Mob.ZOMBIE_LAMENT)
                        .add(0.1, Mob.SKELETAL_SORCERER)
                        .add(0.1, Mob.ARACHNO_VENARI)
                        .add(0.1, Mob.ZOMBIE_LAMENT)
                        .add(0.1, Mob.PIG_SHAMAN)
                        .add(0.1, Mob.PIG_ALLEVIATOR)
                        .add(0.1, Mob.ZOMBIE_VANGUARD)
                        .add(0.1, Mob.SKELETAL_MESMER)
                )
                .add(20, new RandomSpawnWave(1, 8 * SECOND, Component.text("Boss"))
                        .add(1, Mob.EVENT_EXILED_CORE, bossSpawnLocation)
                )
                .add(21, new RandomSpawnWave(24, 8 * SECOND, null)
                        .add(0.1, Mob.EXTREME_ZEALOT)
                        .add(0.1, Mob.ZOMBIE_VANGUARD)
                        .add(0.2, Mob.SKELETAL_SORCERER)
                        .add(0.2, Mob.SLIMY_ANOMALY)
                        .add(0.6, Mob.SLIME_GUARD)
                )
                .add(22, new RandomSpawnWave(14, 8 * SECOND, null)
                        .add(0.1, Mob.EXTREME_ZEALOT)
                        .add(0.1, Mob.FIRE_SPLITTER)
                        .add(0.2, Mob.RIFT_WALKER)
                        .add(0.2, Mob.SKELETAL_SORCERER)
                        .add(0.1, Mob.ZOMBIE_KNIGHT)
                        .add(0.1, Mob.PIG_ALLEVIATOR)
                        .add(0.1, Mob.ZOMBIE_VANGUARD)
                        .add(0.1, Mob.SKELETAL_ENTROPY)
                )
                .add(25, new RandomSpawnWave(18, 8 * SECOND, null)
                        .add(0.05, Mob.ZOMBIE_LANCER)
                        .add(0.05, Mob.PIG_DISCIPLE)
                        .add(0.1, Mob.ZOMBIE_LAMENT)
                        .add(0.05, Mob.ARACHNO_VENARI)
                        .add(0.05, Mob.PIG_SHAMAN)
                        .add(0.2, Mob.PIG_ALLEVIATOR)
                        .add(0.05, Mob.ZOMBIE_VANGUARD)
                        .add(0.05, Mob.SLIME_GUARD)
                        .add(0.1, Mob.RIFT_WALKER)
                        .add(0.1, Mob.SKELETAL_SORCERER)
                        .add(0.1, Mob.ADVANCED_WARRIOR_BERSERKER)
                        .add(0.1, Mob.SLIMY_ANOMALY)
                )
                .add(29, new RandomSpawnWave(18, 8 * SECOND, null)
                        .add(0.2, Mob.SKELETAL_MESMER)
                        .add(0.3, Mob.OVERGROWN_ZOMBIE)
                        .add(0.1, Mob.RIFT_WALKER)
                        .add(0.1, Mob.FIRE_SPLITTER)
                        .add(0.2, Mob.SKELETAL_ENTROPY)
                        .add(0.1, Mob.SKELETAL_SORCERER)
                )
                .add(30, new RandomSpawnWave(1, 8 * SECOND, Component.text("Boss"))
                        .add(1, Mob.EVENT_ILLUMINA, bossSpawnLocation)
                )
                .add(31, new RandomSpawnWave(20, 8 * SECOND, null)
                        .add(0.2, Mob.SKELETAL_SORCERER)
                        .add(0.4, Mob.OVERGROWN_ZOMBIE)
                        .add(0.1, Mob.SLIMY_ANOMALY)
                        .add(0.1, Mob.ZOMBIE_KNIGHT)
                        .add(0.1, Mob.FIRE_SPLITTER)
                        .add(0.1, Mob.SLIME_GUARD)
                )
                .add(36, new RandomSpawnWave(24, 8 * SECOND, null)
                        .add(0.1, Mob.SKELETAL_MESMER)
                        .add(0.1, Mob.ADVANCED_WARRIOR_BERSERKER)
                        .add(0.1, Mob.ZOMBIE_LAMENT)
                        .add(0.1, Mob.ARACHNO_VENARI)
                        .add(0.1, Mob.PIG_SHAMAN)
                        .add(0.1, Mob.PIG_ALLEVIATOR)
                        .add(0.05, Mob.RIFT_WALKER)
                        .add(0.05, Mob.ZOMBIE_KNIGHT)
                        .add(0.1, Mob.ZOMBIE_SWORDSMAN)
                        .add(0.1, Mob.OVERGROWN_ZOMBIE)
                        .add(0.1, Mob.SLIMY_ANOMALY)
                )
                .add(40, new RandomSpawnWave(1, 8 * SECOND, Component.text("Boss"))
                        .add(1, Mob.EVENT_CALAMITY_CORE, bossSpawnLocation)
                )
                .loop(6, 36, 5)
                .loop(6, 40, 5)
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
                if (warlordsNPC.getMob() instanceof BossLike) {
                    float scaledHealth = (float) (warlordsNPC.getMaxHealth() * (.0625 * Math.pow(Math.E, 0.69314718056 * playerCount()))); // ln4/2 = 0.69314718056
                    warlordsNPC.setMaxHealthAndHeal(scaledHealth);
                    return;
                }
                int playerCount = playerCount();
                int wavesCleared = getWavesCleared();

                float healthMultiplier;
                float meleeDamageMultiplier = 1;

                float waveHealthMultiplier = 0;
                float waveMeleeDamageMultiplier = 0;
                switch (playerCount) {
                    case 1, 2 -> healthMultiplier = 1.1f;
                    case 3 -> healthMultiplier = 1.25f;
                    default -> healthMultiplier = 1.40f;
                }
                if (wavesCleared >= 10) {
                    waveHealthMultiplier += .05;
                }
                if (wavesCleared >= 20) {
                    waveHealthMultiplier += .05;
                    waveMeleeDamageMultiplier += .05;
                }
                if (wavesCleared >= 30) {
                    waveMeleeDamageMultiplier += .05;
                }
                if (wavesCleared >= 40) {
                    waveHealthMultiplier += .1;
                }
                healthMultiplier += waveHealthMultiplier;
                meleeDamageMultiplier += waveMeleeDamageMultiplier;

                float maxHealth = warlordsNPC.getMaxHealth();
                float minMeleeDamage = warlordsNPC.getMinMeleeDamage();
                float maxMeleeDamage = warlordsNPC.getMaxMeleeDamage();
                float newHealth = maxHealth * healthMultiplier;
                warlordsNPC.setMaxHealthAndHeal(newHealth);
                warlordsNPC.setMinMeleeDamage((int) (minMeleeDamage * meleeDamageMultiplier));
                warlordsNPC.setMaxMeleeDamage((int) (maxMeleeDamage * meleeDamageMultiplier));
            }
        });
        options.add(new ItemOption());
        options.add(new WinAfterTimeoutOption(900, 50, "spec"));
        options.add(new TheBorderlineOfIllusionEvent());
        options.add(new SafeZoneOption(1));
        options.add(new EventPointsOption()
                .reduceScoreOnAllDeath(35, Team.BLUE)
                .onPerWaveClear(1, 500)
                .onPerWaveClear(5, 2000)
                .onPerMobKill(Mob.ZOMBIE_LAMENT, 10)
                .onPerMobKill(Mob.ARACHNO_VENARI, 10)
                .onPerMobKill(Mob.SLIMY_ANOMALY, 10)
                .onPerMobKill(Mob.SKELETAL_WARLOCK, 15)
                .onPerMobKill(Mob.PIG_SHAMAN, 15)
                .onPerMobKill(Mob.PIG_ALLEVIATOR, 20)
                .onPerMobKill(Mob.ZOMBIE_VANGUARD, 20)
                .onPerMobKill(Mob.SLIME_GUARD, 25)
                .onPerMobKill(Mob.ZOMBIE_SWORDSMAN, 30)
                .onPerMobKill(Mob.SKELETAL_MESMER, 35)
                .onPerMobKill(Mob.ZOMBIE_KNIGHT, 35)
                .onPerMobKill(Mob.ADVANCED_WARRIOR_BERSERKER, 35)
                .onPerMobKill(Mob.OVERGROWN_ZOMBIE, 40)
                .onPerMobKill(Mob.RIFT_WALKER, 45)
                .onPerMobKill(Mob.EXTREME_ZEALOT, 45)
                .onPerMobKill(Mob.SKELETAL_SORCERER, 50)
                .onPerMobKill(Mob.EVENT_ILLUSION_CORE, 2500)
                .onPerMobKill(Mob.EVENT_EXILED_CORE, 2500)
                .onPerMobKill(Mob.EVENT_CALAMITY_CORE, 2500)
                .onPerMobKill(Mob.EVENT_ILLUMINA, 3000)
        );
        options.add(new CurrencyOnEventOption()
                .startWith(100000)
                .onKill(500)
                .setPerWaveClear(5, 25000)
                .disableGuildBonus()
        );
        options.add(new CoinGainOption()
                .playerCoinPerXSec(150, 10)
                .guildCoinInsigniaConvertBonus(1000)
                .guildCoinPerXSec(1, 1)
                .disableCoinConversionUpgrade()
        );
        options.add(new ExperienceGainOption()
                .playerExpPerXSec(10, 10)
                .guildExpPerXSec(20, 30)
        );
        options.add(new FieldEffectOption(options, FieldEffectOption.FieldEffect.LOST_BUFF, FieldEffectOption.FieldEffect.DUMB_DEBUFFS));

        return options;
    }

}