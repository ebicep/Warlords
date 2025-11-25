package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.abilities.SoulShackle;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.*;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.FrostVeil;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;

import javax.annotation.Nonnull;
import java.util.Collections;

public class Orbyz extends AbstractMob implements BossMob {

    private Location mapCenter;
    private RotatingRadialLasersAbility rotatingRadialLasersAbility;
    private ConvergingShockwavesAbility convergingShockwavesAbility;
    private HeavenlySpearAbility heavenlySpearAbilityOne;
    private HeavenlySpearAbility heavenlySpearAbilityTwo;
    private HeavenlySpearAbility heavenlySpearAbilityInterval;
    private SummoningCirclesAbility summoningCirclesAbility;
    private MarkedForDeathAbility markedForDeathAbility;
    private EmpoweringRelicsAbility empoweringRelicsAbility;
    private BossAbilityPhase phaseOne;
    private BossAbilityPhase phaseTwo;
    private BossAbilityPhase phaseThree;
    private BossAbilityPhase phaseFour;
    private BossAbilityPhase phaseFive;
    private BossAbilityPhase phaseSix;

    private boolean preventMarkForDeath = false;
    private boolean preventRelic = false;

    public Orbyz(Location spawnLocation) {
        super(
                spawnLocation,
                "Orbyz",
                240000,
                0.3f,
                40,
                1200,
                2000
        );
    }

    public Orbyz(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 500, 0.5f);
        EffectUtils.playFirework(warlordsNPC.getLocation(), FireworkEffect.builder()
                .withColor(Color.AQUA)
                .with(FireworkEffect.Type.BALL_LARGE)
                .withTrail()
                .build());
        FallingBlockWaveEffect.create(
                warlordsNPC.getLocation(),
                15,
                12,
                Material.PACKED_ICE
        );

        new GameRunnable(warlordsNPC.getGame()) {
            @Override
            public void run() {
                for (WarlordsEntity we : PlayerFilter
                        .entitiesAround(warlordsNPC, 6, 6, 6)
                        .aliveEnemiesOf(warlordsNPC)
                ) {
                    if (we.getCooldownManager().hasCooldownFromName("Empowering Relic")) {
                        we.addInstance(InstanceBuilder
                                .damage()
                                .min(800)
                                .max(1200)
                                .source(warlordsNPC)
                                .cause("Blizzard")
                                .flag(InstanceFlags.TRUE_DAMAGE, true)
                        );
                    }
                }

                if (warlordsNPC.isDead()) {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 10);

        mapCenter = new Location(warlordsNPC.getWorld(), 112.5, 11, 62.5);

        rotatingRadialLasersAbility = new RotatingRadialLasersAbility(warlordsNPC, () -> mapCenter, 6, 45, 1, 80, 240, 1.2, 2, 1000, 2, 1, Color.AQUA, Color.RED);
        convergingShockwavesAbility = new ConvergingShockwavesAbility(warlordsNPC, () -> mapCenter, 38, 10, 60, 20, 0.3, 1.5, 1, 1000, 2, 1, Color.PURPLE, Color.BLUE);
        heavenlySpearAbilityOne = new HeavenlySpearAbility(warlordsNPC, () -> mapCenter, 6 * option.playerCount(), 38, 30, 5, 4000, 60, 35, 2.5, Material.PACKED_ICE, Particle.ITEM_SNOWBALL, Particle.SNOWFLAKE, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, Sound.BLOCK_GLASS_BREAK);
        heavenlySpearAbilityTwo = new HeavenlySpearAbility(warlordsNPC, () -> mapCenter, 10 * option.playerCount(), 38, 20, 5, 4000, 20, 35, 2.5, Material.PACKED_ICE, Particle.ITEM_SNOWBALL, Particle.SNOWFLAKE, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, Sound.BLOCK_GLASS_BREAK);
        heavenlySpearAbilityInterval = new HeavenlySpearAbility(warlordsNPC, () -> mapCenter, 2 + option.playerCount(), 36, 50, 8, 5000, 400, 35, 3.5, Material.SNOW_BLOCK, Particle.ITEM_SNOWBALL, Particle.SNOWFLAKE, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, Sound.BLOCK_GLASS_BREAK);
        summoningCirclesAbility = new SummoningCirclesAbility(warlordsNPC, () -> mapCenter, 2, 28, 300, 5, 5, option);

        markedForDeathAbility = new MarkedForDeathAbility(
                warlordsNPC,
                3,
                80,
                30,
                5,
                8000,
                1,
                2
        );

        empoweringRelicsAbility = new EmpoweringRelicsAbility(
                warlordsNPC,
                () -> mapCenter,
                1,
                30,
                4,
                true,
                1.5,
                300,
                7,
                1,
                2,
                null,
                1,
                1.5f,
                1,
                warlordsEntity -> {
                    ChatUtils.sendTitleToGamePlayers(
                            warlordsNPC.getGame(),
                            Component.empty(),
                            Component.text(warlordsEntity.getName() + " has picked up the empowering relic! Use it against the boss!", NamedTextColor.GOLD),
                            20,
                            60,
                            20
                    );
                    warlordsEntity.sendMessage(Component.text("You may use this relic to make Orbyz vulnerable against your allies' attacks.", NamedTextColor.GOLD));
                    // relic AoE
                    int radius = 9;
                    warlordsEntity.getCooldownManager().addCooldown(new RegularCooldown<>(
                            "Empowering Relic",
                            "RELIC",
                            DamageCheck.class,
                            DamageCheck.DAMAGE_CHECK,
                            warlordsEntity,
                            CooldownTypes.BUFF,
                            cooldownManager -> {
                            },
                            15 * 20,
                            Collections.singletonList((cooldown, ticksLeft, ticksElapsed2) -> {
                                if (ticksElapsed2 % 3 == 0) {
                                    new CircleEffect(
                                            warlordsEntity.getGame(),
                                            warlordsEntity.getTeam(),
                                            warlordsEntity.getLocation().clone().add(0, 0.25, 0),
                                            radius,
                                            new CircumferenceEffect(Particle.FIREWORK, Particle.FIREWORK).particlesPerCircumference(1.2)
                                    ).playEffects();
                                    for (WarlordsEntity ally : PlayerFilter
                                            .entitiesAround(warlordsEntity, radius, radius, radius)
                                            .aliveTeammatesOfExcludingSelf(warlordsEntity)
                                    ) {
                                        ally.getCooldownManager().addCooldown(new RegularCooldown<>(
                                                "Empowering Allies",
                                                "RELIC BUFF",
                                                DamageCheck.class,
                                                DamageCheck.DAMAGE_CHECK,
                                                warlordsEntity,
                                                CooldownTypes.ABILITY,
                                                cooldownManager -> {
                                                },
                                                4
                                        ));
                                    }
                                }
                            })
                    ));
                },
                warlordsEntity -> {
                    warlordsEntity.getCooldownManager().removeCooldown(DamageCheck.class, false);
                }
        );

        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Damage Check",
                null,
                DamageCheck.class,
                DamageCheck.DAMAGE_CHECK,
                warlordsNPC,
                CooldownTypes.BUFF,
                cooldownManager -> {},
                true
        ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_ALL_MODIFIERS, (event, currentDamageValue, isCrit) -> {
                    if (event.getSource().getCooldownManager().hasCooldownFromName("Empowering Allies")) {
                        currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, name, 1.2f);
                    } else {
                        currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, name, 0.1f);
                    }
                }
        ));

        phaseOne = new BossAbilityPhase(warlordsNPC, 80, () -> {
            castRotatingLasers();
        });

        phaseTwo = new BossAbilityPhase(warlordsNPC, 60, () -> {
            ChatUtils.sendTitleToGamePlayers(
                    warlordsNPC.getGame(),
                    Component.empty(),
                    Component.text("Seems as if the floor is quite icy...", NamedTextColor.AQUA),
                    20,
                    60,
                    20
            );
            convergingShockwavesAbility.start(warlordsNPC.getGame());
        });

        phaseThree = new BossAbilityPhase(warlordsNPC, 50, () -> {
            castRotatingLasers();
        });

        phaseFour = new BossAbilityPhase(warlordsNPC, 40, () -> {
            preventRelic = true;
            preventMarkForDeath = true;
            ChatUtils.sendTitleToGamePlayers(
                    warlordsNPC.getGame(),
                    Component.empty(),
                    Component.text("You hear distant howls in the sky about to rain down...", NamedTextColor.AQUA),
                    20,
                    60,
                    20
            );
            new GameRunnable(warlordsNPC.getGame()) {
                int t = 0;
                @Override
                public void run() {
                    t++;
                    if (t % 60 == 0) {
                        heavenlySpearAbilityOne.start(warlordsNPC.getGame());
                    }

                    if (t == 601) {
                        preventRelic = false;
                        preventMarkForDeath = false;
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 0);
        });

        phaseFive = new BossAbilityPhase(warlordsNPC, 25, () -> {
            castRotatingLasers();
        });

        phaseSix = new BossAbilityPhase(warlordsNPC, 15, () -> {
            preventRelic = true;
            preventMarkForDeath = true;
            ChatUtils.sendTitleToGamePlayers(
                    warlordsNPC.getGame(),
                    Component.empty(),
                    Component.text("As you near the end... once more...", NamedTextColor.AQUA),
                    20,
                    60,
                    20
            );
            Utils.playGlobalSound(mapCenter, Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, 500, 0.3f);
            new GameRunnable(warlordsNPC.getGame()) {
                int t = 0;
                @Override
                public void run() {
                    t++;
                    if (t % 15 == 0) {
                        heavenlySpearAbilityTwo.start(warlordsNPC.getGame());
                    }

                    if (t == 451) {
                        preventRelic = false;
                        preventMarkForDeath = false;
                        this.cancel();
                    }
                }
            }.runTaskTimer(20, 0);
        });

    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        warlordsNPC.getCooldownManager().removeCooldown(SoulShackle.class, false);
        if (ticksElapsed % 20 == 0) {
            EffectUtils.playCrownAnimation(warlordsNPC.getLocation(), Particle.SNOWFLAKE);
            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(warlordsNPC, 6, 6, 6)
                    .aliveEnemiesOf(warlordsNPC)
            ) {
                enemy.addSpeedModifier(warlordsNPC, "Permanent Orbyz", -60, 2 * 20);
            }

            EffectUtils.playCircularShieldAnimation(warlordsNPC.getLocation(), Particle.END_ROD, 8, 1, 5);
        }

        if (ticksElapsed % 160 == 0 && ticksElapsed > 0) {
            Location loc = warlordsNPC.getLocation();
            Utils.playGlobalSound(loc, Sound.BLOCK_GLASS_BREAK, 500, 0.4f);
            FallingBlockWaveEffect.create(loc.add(0, 1, 0), 7, 6, Material.PACKED_ICE);
            for (WarlordsEntity we : PlayerFilterGeneric
                    .entitiesAround(warlordsNPC, 7, 7, 7)
                    .aliveEnemiesOf(warlordsNPC)
            ) {
                Utils.addKnockback(name, loc, we, -3, 0.3);
                we.addSpeedModifier(warlordsNPC, "Orbyz Slowness", -70, 30);
                we.addInstance(InstanceBuilder
                        .damage()
                        .cause("Blizzard Impendus")
                        .source(warlordsNPC)
                        .value(3000)
                );
            }
        }

        if (ticksElapsed % 825 == 0) {
            for (int i = 0; i < option.playerCount(); i++) {
                option.spawnNewMob(new FrostVeil(option.getRandomSpawnLocation(warlordsNPC)));
            }
        }

        if (ticksElapsed % 1600 == 0 && ticksElapsed > 0) {
            summoningCirclesAbility.start(warlordsNPC.getGame());
        }

        if (ticksElapsed % 630 == 0 && ticksElapsed > 0 && !preventMarkForDeath) {
            markedForDeathAbility.start(warlordsNPC.getGame());
        }

        if (ticksElapsed % 470 == 0 && ticksElapsed > 0 && !preventMarkForDeath) {
            heavenlySpearAbilityInterval.start(warlordsNPC.getGame());
        }

        if (ticksElapsed % 400 == 0 && ticksElapsed > 0 && !preventRelic) {
            empoweringRelicsAbility.start(warlordsNPC.getGame());
        }

        float health = warlordsNPC.getCurrentHealth();
        phaseOne.initialize(health);
        phaseTwo.initialize(health);
        phaseThree.initialize(health);
        phaseFour.initialize(health);
        phaseFive.initialize(health);
        phaseSix.initialize(health);
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_HIT, 500, 0.3f);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 500, 2f);
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), false);
        EffectUtils.playFirework(warlordsNPC.getLocation(), FireworkEffect.builder()
                .withColor(Color.AQUA)
                .with(FireworkEffect.Type.BALL_LARGE)
                .withTrail()
                .build());
        FallingBlockWaveEffect.create(
                warlordsNPC.getLocation(),
                15,
                12,
                Material.PACKED_ICE
        );
    }

    private void castRotatingLasers() {
        ChatUtils.sendTitleToGamePlayers(
                warlordsNPC.getGame(),
                Component.empty(),
                Component.text("Little puppets running in circles...", NamedTextColor.AQUA),
                20,
                60,
                20
        );
        Utils.playGlobalSound(mapCenter, Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, 500, 0.3f);
        rotatingRadialLasersAbility.start(warlordsNPC.getGame());
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.ORBYZ;
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.AQUA;
    }

    @Override
    public Component getDescription() {
        return Component.text("Once Frozen in Time", NamedTextColor.WHITE);
    }
}
