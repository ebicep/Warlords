package com.ebicep.warlords.game.option.raid.bosses;

import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.abilities.internal.PhysiraCheck;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.raid.BossAbilityPhase;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.pve.mobs.witherskeleton.CelestialOpus;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.cos;

public class Physira extends AbstractMob implements BossMob {

    private Listener listener;
    List<WarlordsNPC> pylons = new ArrayList<>();
    ;

    private BossAbilityPhase phaseOne;
    private BossAbilityPhase phaseTwo;
    private BossAbilityPhase phaseThree;

    public Physira(Location spawnLocation) {
        super(spawnLocation,
                "Physira",
                10000,
                0,
                0,
                3000,
                4000
        );
    }

    public Physira(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        );
    }

    @Override
    public Mob getMobRegistry() {
        return null;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        phaseOne = new BossAbilityPhase(warlordsNPC, 75, () -> {
            ChatUtils.sendTitleToGamePlayers(
                    warlordsNPC.getGame(),
                    Component.empty(),
                    Component.text("Destroy Physira's pylons before the time runs out!", NamedTextColor.GRAY),
                    20,
                    60,
                    20
            );

            Location loc = warlordsNPC.getLocation();
            Location crystalLoc = loc.clone();
            for (int j = 0; j < 6; j++) {
                double angle = j / 6D * Math.PI * 2;
                crystalLoc.setX(loc.getX() + Math.sin(angle) * 20);
                crystalLoc.setZ(loc.getZ() + cos(angle) * 20);
                PhysiraCrystal crystal = new PhysiraCrystal(crystalLoc, warlordsNPC, SpecType.VALUES[j % 3]);
                pylons.add(crystal.getWarlordsNPC());
                Bukkit.broadcast(Component.text("pylons: " + pylons.get(j)));
                pveOption.spawnNewMob(crystal, Team.RED);
            }

            AtomicInteger countdown = new AtomicInteger(30);
            new GameRunnable(warlordsNPC.getGame()) {
                int counter = 0;
                @Override
                public void run() {
                    if (counter % 20 == 0) {
                        countdown.getAndDecrement();
                        Utils.playGlobalSound(warlordsNPC.getLocation(), "warrior.laststand.activation", 500, 0.6f);
                    }

                    if (pylons.isEmpty() && countdown.get() > 0) {
                        EffectUtils.playFirework(
                                warlordsNPC.getLocation(),
                                FireworkEffect.builder()
                                        .with(FireworkEffect.Type.BALL_LARGE)
                                        .withColor(Color.WHITE)
                                        .withTrail()
                                        .build()
                        );
                        warlordsNPC.getGame().registerEvents(listener);
                        this.cancel();
                    }

                    if (countdown.get() <= 0) {
                        EffectUtils.strikeLightningTicks(warlordsNPC.getLocation(), true, 60);
                        EffectUtils.playFirework(
                                warlordsNPC.getLocation(),
                                FireworkEffect.builder()
                                        .with(FireworkEffect.Type.BALL_LARGE)
                                        .withColor(Color.RED)
                                        .withTrail()
                                        .build()
                        );

                        for (WarlordsEntity we : PlayerFilter
                                .playingGame(warlordsNPC.getGame())
                                .aliveEnemiesOf(warlordsNPC)
                        ) {
                            we.addInstance(InstanceBuilder
                                    .damage()
                                    .cause("Valerian Death")
                                    .source(warlordsNPC)
                                    .min(700 * 100)
                                    .max(1300 * 100)
                                    .critChance(100)
                                    .critMultiplier(300)
                                    .flags(InstanceFlags.TRUE_DAMAGE)
                            );
                            EffectUtils.strikeLightning(we.getLocation(), false);
                            EffectUtils.playParticleLinkAnimation(
                                    we.getLocation(),
                                    warlordsNPC.getLocation(),
                                    Particle.CHERRY_LEAVES
                            );
                        }

                        warlordsNPC.getGame().registerEvents(listener);
                        this.cancel();
                    }

                    ChatUtils.sendTitleToGamePlayers(
                            warlordsNPC.getGame(),
                            Component.text(countdown.get(), NamedTextColor.GOLD),
                            Component.empty(),
                            0, 4, 0
                    );

                    counter++;
                }
            }.runTaskTimer(60, 0);

            listener = new Listener() {
                @EventHandler
                private void onAllyDeath(WarlordsDeathEvent event) {
                    WarlordsEntity we = event.getWarlordsEntity();
                    pylons.remove(we);
                    Bukkit.broadcast(Component.text("pylon removed"));
                }
            };
        });

        phaseTwo = new BossAbilityPhase(warlordsNPC, 50, () -> {

            WarlordsEntity divineProtector = null;
            for (WarlordsEntity we : PlayerFilter
                    .playingGame(warlordsNPC.getGame())
                    .aliveEnemiesOf(warlordsNPC)
                    .limit(1)
            ) {
                divineProtector = we;
                ChatUtils.sendTitleToGamePlayers(
                        warlordsNPC.getGame(),
                        Component.text("RUN!", NamedTextColor.RED),
                        Component.text(we.getName() + " has been marked to give Divine Protection", NamedTextColor.GOLD),
                        20, 60, 20
                );
                we.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 540, 0, false));
                we.getCooldownManager().removeCooldown(DamageCheck.class, false);
                we.getCooldownManager().addCooldown(new RegularCooldown<>(
                        "Divine Protection",
                        "DIVINE PROTECTION",
                        DamageCheck.class,
                        DamageCheck.DAMAGE_CHECK,
                        warlordsNPC,
                        CooldownTypes.ABILITY,
                        cooldownManager -> {
                        },
                        28 * 20,
                        Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                            new CircleEffect(
                                    we.getGame(),
                                    we.getTeam(),
                                    we.getLocation().clone().add(0, 0.25, 0),
                                    8,
                                    new CircumferenceEffect(Particle.FIREWORKS_SPARK, Particle.FIREWORKS_SPARK).particlesPerCircumference(1),
                                    new DoubleLineEffect(Particle.SPELL)
                            ).playEffects();
                            if (ticksLeft % 2 == 0) {
                                for (WarlordsEntity ally : PlayerFilter
                                        .entitiesAround(we, 8, 100, 8)
                                        .aliveTeammatesOfExcludingSelf(we)
                                ) {
                                    ally.getCooldownManager().removeCooldown(PhysiraCheck.class, false);
                                    ally.getCooldownManager().addCooldown(new RegularCooldown<>(
                                            "Divine Protection",
                                            "DIVINE PROTECTION",
                                            PhysiraCheck.class,
                                            PhysiraCheck.PHYSIRA_CHECK,
                                            warlordsNPC,
                                            CooldownTypes.ABILITY,
                                            cooldownManager -> {
                                            },
                                            3
                                    ) {
                                        @Override
                                        public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                            return currentDamageValue * 0;
                                        }
                                    });
                                }
                            }
                        })
                ) {
                    @Override
                    public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        return currentDamageValue * 0.05f;
                    }
                });
            }

            WarlordsEntity finalDivineProtector = divineProtector;
            new GameRunnable(warlordsNPC.getGame()) {
                int counter = 0;
                @Override
                public void run() {
                    if (counter == 1) {
                        warlordsNPC.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 400, 0, false));
                        warlordsNPC.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 400, 0, false));
                    }

                    if (counter % 3 == 0) {
                        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 500, 0.2f);
                        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.AMBIENT_CRIMSON_FOREST_MOOD, 500, 1f);

                        EffectUtils.playSphereAnimation(
                                warlordsNPC.getLocation(),
                                1 + (0.1f * counter),
                                Particle.CHERRY_LEAVES,
                                1
                        );

                        for (WarlordsEntity we : PlayerFilter
                                .playingGame(warlordsNPC.getGame())
                                .aliveEnemiesOf(warlordsNPC)
                        ) {
                            we.addInstance(InstanceBuilder
                                    .damage()
                                    .cause("Divine Punishment")
                                    .source(warlordsNPC)
                                    .value(1000)
                                    .flags(InstanceFlags.NO_MESSAGE)
                            );
                            EffectUtils.playParticleLinkAnimation(
                                    we.getLocation(),
                                    warlordsNPC.getLocation(),
                                    255, 150, 150,
                                    2
                            );
                        }

                        if (finalDivineProtector == null) {
                            return;
                        }

                        for (WarlordsEntity we : PlayerFilter
                                .entitiesAround(finalDivineProtector, 100, 100, 100)
                                .aliveEnemiesOf(finalDivineProtector)
                        ) {
                            if (we instanceof WarlordsNPC) {
                                ((WarlordsNPC) we).getMob().setTarget(finalDivineProtector);
                            }
                        }
                    }

                    if (counter % 100 == 0) {
                        for (int i = 0; i < pveOption.playerCount(); i++) {
                            pveOption.spawnNewMob(new CelestialOpus(warlordsNPC.getLocation()));
                        }
                    }

                    if (counter == 400) {
                        this.cancel();
                    }

                    counter++;
                }
            }.runTaskTimer(140, 0);
        });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        float health = warlordsNPC.getCurrentHealth();
        phaseOne.initialize(health);
        phaseTwo.initialize(health);
        //phaseThree.initialize(health);
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        if (listener != null) {
            HandlerList.unregisterAll(listener);
        }
    }
}