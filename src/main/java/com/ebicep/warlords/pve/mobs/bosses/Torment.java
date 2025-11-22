package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.abilities.internal.PhysiraCheck;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.BossAbilityPhase;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.SoulOfGradient;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.pve.mobs.witherskeleton.CelestialOpus;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.Collections;

public class Torment extends AbstractMob implements BossMob {

    private BossAbilityPhase suctionPhase;
    private BossAbilityPhase suctionPhaseTwo;
    private BossAbilityPhase divinePhase;
    private boolean preventMarking = false;

    public Torment(Location spawnLocation) {
        super(
                spawnLocation,
                "Torment",
                130000,
                0.18f,
                20,
                2400,
                3600
        );
    }

    public Torment(
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
    public Mob getMobRegistry() {
        return Mob.TORMENT;
    }

    @Override
    public Component getDescription() {
        return Component.text("Corrupted Soul", NamedTextColor.WHITE);
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.RED;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        ChatUtils.sendTitleToGamePlayers(
                warlordsNPC.getGame(),
                Component.text("Torment", NamedTextColor.RED),
                Component.text("Corrupted Soul", NamedTextColor.WHITE)
        );
        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Damage Check",
                null,
                DamageCheck.class,
                DamageCheck.DAMAGE_CHECK,
                warlordsNPC,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                true
        ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_ALL_MODIFIERS, (event, currentDamageValue, isCrit) -> {
                    if (event.getSource().getCooldownManager().hasCooldown(DamageCheck.class)) {
                        currentDamageValue.addMultiplicativeModifierMult(name, 3);
                    } else {
                        EffectUtils.playParticleLinkAnimation(
                                warlordsNPC.getLocation(),
                                event.getSource().getLocation(),
                                Particle.SCULK_SOUL
                        );
                        currentDamageValue.addMultiplicativeModifierMult(name, 0.4f);
                    }
                }
        ));

        Location loc = new Location(warlordsNPC.getWorld(), 112.5, 13, 62.5);
        suctionPhase = new BossAbilityPhase(warlordsNPC, 80, () -> {
            suction(loc);
        }
        );

        suctionPhaseTwo = new BossAbilityPhase(warlordsNPC, 50, () -> {
            suction(loc);
        }
        );

        divinePhase = new BossAbilityPhase(warlordsNPC, 25, () -> {

            preventMarking = true;
            WarlordsEntity divineProtector = null;
            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(warlordsNPC, 100, 100, 100)
                    .aliveEnemiesOf(warlordsNPC)
                    .excludingAlliedMobs()
                    .limit(1)
            ) {
                divineProtector = we;
                ChatUtils.sendTitleToGamePlayers(
                        warlordsNPC.getGame(),
                        Component.text("RUN!", NamedTextColor.RED),
                        Component.text(we.getName() + " has been marked to give Divine Protection. Keep them alive!", NamedTextColor.GOLD),
                        20, 60, 20
                );
                we.sendMessage(Component.text("You have been chosen to give Divine Protection. Stay alive!", NamedTextColor.GOLD));
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
                                    6,
                                    new CircumferenceEffect(Particle.FIREWORK, Particle.FIREWORK).particlesPerCircumference(0.8),
                                    new DoubleLineEffect(Particle.EFFECT)
                            ).playEffects();
                            if (ticksLeft % 2 == 0) {
                                for (WarlordsEntity ally : PlayerFilter
                                        .entitiesAround(we, 6, 100, 6)
                                        .aliveTeammatesOfExcludingSelf(we)
                                ) {
                                    Utils.addKnockback("KB", we.getLocation(), ally, -0.05, 0, true);
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
                                    ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                                                currentDamageValue.addOverridingModifier(name, 0);
                                            }
                                    ));
                                }
                            }
                        })
                ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                            currentDamageValue.addMultiplicativeModifierMult(name, 0.05f);
                        }
                ));
            }

            WarlordsEntity finalDivineProtector = divineProtector;
            new GameRunnable(warlordsNPC.getGame()) {
                int counter = 0;

                @Override
                public void run() {
                    if (warlordsNPC.isDead()) {
                        this.cancel();
                        return;
                    }

                    if (counter == 1) {
                        warlordsNPC.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 400, 0, false));
                        warlordsNPC.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 400, 0, false));
                    }

                    if (counter % 4 == 0) {
                        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_RAVAGER_ROAR, 500, 0.2f);
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
                            pveOption.spawnNewMob(new CelestialOpus(pveOption.getRandomSpawnLocation(warlordsNPC)));
                        }
                    }

                    if (counter == 400) {
                        this.cancel();
                        preventMarking = false;
                    }

                    counter++;
                }
            }.runTaskTimer(140, 0);
        }
        );
    }

    private void suction(Location loc) {
        preventMarking = true;

        for (int i = 0; i < 5; i++) {
            Utils.playGlobalSound(loc, Sound.ENTITY_ALLAY_ITEM_THROWN, 500f, 0.4f);
            EffectUtils.strikeLightning(loc, false);
        }

        ChatUtils.sendTitleToGamePlayers(
                warlordsNPC.getGame(),
                Component.text("Whispers...", NamedTextColor.GRAY),
                Component.text("Of Tormented Souls", NamedTextColor.WHITE)
        );
        for (WarlordsEntity we : PlayerFilter
                .entitiesAround(warlordsNPC, 25, 25, 25)
                .aliveEnemiesOf(warlordsNPC)
        ) {
            we.addInstance(InstanceBuilder
                    .damage()
                    .cause("Gradient Curse")
                    .source(warlordsNPC)
                    .min(2000)
                    .max(3000)
            );
            Utils.addKnockback("KB", warlordsNPC.getLocation(), we, -5, 0.2, true);
        }

        new GameRunnable(warlordsNPC.getGame()) {
            int counter = 0;

            @Override
            public void run() {
                counter++;
                new CircleEffect(
                        warlordsNPC.getGame(),
                        warlordsNPC.getTeam(),
                        loc.clone().add(0, 1.5, 0),
                        10,
                        new CircumferenceEffect(Particle.WITCH, Particle.PORTAL).particlesPerCircumference(1.5),
                        new DoubleLineEffect(Particle.WITCH)
                ).playEffects();
                if (counter % 12 == 0) {
                    Utils.playGlobalSound(loc, Sound.ENTITY_WARDEN_ROAR, 10f, 0.4f);
                    Utils.playGlobalSound(loc, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 10f, 0.6f);
                    for (WarlordsEntity we : PlayerFilter
                            .entitiesAround(loc, 100, 100, 100)
                            .aliveEnemiesOf(warlordsNPC)
                    ) {
                        EffectUtils.playParticleLinkAnimation(we.getLocation(), loc, Particle.SCULK_SOUL);
                        Utils.addKnockback("KB", loc, we, 1.15, 0, true);
                    }
                }

                if (counter % 64 == 0) {
                    pveOption.spawnNewMob(new CelestialOpus(pveOption.getRandomSpawnLocation(warlordsNPC)));
                }

                if (counter % 5 == 0) {
                    for (WarlordsEntity we : PlayerFilter
                            .entitiesAround(loc, 10, 10, 10)
                            .aliveEnemiesOf(warlordsNPC)
                    ) {
                        we.addInstance(InstanceBuilder
                                .damage()
                                .cause("Whispers of Tormented Souls")
                                .source(warlordsNPC)
                                .value(1000)
                                .flag(InstanceFlags.TRUE_DAMAGE, true)
                        );
                    }
                }

                if (counter == 400) {
                    EffectUtils.strikeLightningInCylinder(loc, 8, false);
                    for (WarlordsEntity we : PlayerFilter
                            .entitiesAround(loc, 15, 15, 15)
                            .aliveEnemiesOf(warlordsNPC)
                    ) {
                        we.addInstance(InstanceBuilder
                                .damage()
                                .cause("Whispers of Tormented Souls")
                                .source(warlordsNPC)
                                .value(3000)
                                .flag(InstanceFlags.TRUE_DAMAGE, true)
                        );
                        Utils.addKnockback("KB", loc, we, -10, 0.2, true);
                    }
                    this.cancel();
                    preventMarking = false;
                }
            }
        }.runTaskTimer(100, 0);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        float health = warlordsNPC.getCurrentHealth();
        suctionPhase.initialize(health);
        suctionPhaseTwo.initialize(health);
        divinePhase.initialize(health);

        warlordsNPC.getSpeed().removeNegativeModifiers();

        if (ticksElapsed % 20 == 0) {
            EffectUtils.playCrownAnimation(warlordsNPC.getLocation(), Particle.SCULK_SOUL);
        }

        if (ticksElapsed % 200 == 0) {
            FallingBlockWaveEffect.create(warlordsNPC.getLocation().clone().add(0, 1, 0), 12, 4, Material.SOUL_FIRE);
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ITEM_FIRECHARGE_USE, 500, 0.2f);
            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(warlordsNPC, 12, 12, 12)
                    .aliveEnemiesOf(warlordsNPC)
            ) {
                we.addInstance(InstanceBuilder
                        .damage()
                        .cause("Soul Fire")
                        .source(warlordsNPC)
                        .min(1000)
                        .max(1600)
                );
            }
        }

        if (ticksElapsed % 1200 == 0) {
            new GameRunnable(warlordsNPC.getGame()) {
                int counter = 0;

                @Override
                public void run() {
                    if (warlordsNPC.isDead()) {
                        this.cancel();
                        return;
                    }

                    EffectUtils.playCylinderAnimation(warlordsNPC.getLocation(), 0.3 * counter, 255, 30, 30, counter, 4);
                    counter++;
                    if (counter == 40) {
                        for (int i = 0; i < option.playerCount(); i++) {
                            option.spawnNewMob(new SoulOfGradient(pveOption.getRandomSpawnLocation(warlordsNPC)));
                        }
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 0);
        }

        if (ticksElapsed % 600 == 0 && ticksElapsed > 0 && !preventMarking) {
            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(warlordsNPC, 100, 100, 100)
                    .aliveEnemiesOf(warlordsNPC)
                    .excludingAlliedMobs()
                    .limit(1)
            ) {
                ChatUtils.sendTitleToGamePlayers(
                        warlordsNPC.getGame(),
                        Component.empty(),
                        Component.text(we.getName(), NamedTextColor.GOLD)
                                 .append(Component.text(" has been marked by Torment!", NamedTextColor.RED))
                );
                Utils.addKnockback(name, warlordsNPC.getLocation(), we, 2, 0.35);
                we.getCooldownManager().removeCooldown(DamageCheck.class, false);
                we.getCooldownManager().addCooldown(new RegularCooldown<>(
                        "Tormenting Mark",
                        "MARK",
                        DamageCheck.class,
                        DamageCheck.DAMAGE_CHECK,
                        warlordsNPC,
                        CooldownTypes.ABILITY,
                        cooldownManager -> {
                        },
                        15 * 20,
                        Collections.singletonList((cooldown, ticksLeft, ticksElapsed2) -> {
                            if (ticksLeft % 10 == 0) {
                                EffectUtils.playParticleLinkAnimation(warlordsNPC.getLocation(), we.getLocation(), Particle.DRIPPING_LAVA);
                                EffectUtils.playSphereAnimation(we.getLocation(), 7, Particle.FLAME, 1);
                            }

                            if (ticksLeft % 5 == 0) {
                                for (WarlordsEntity ally : PlayerFilter
                                        .entitiesAround(we, 7, 7, 7)
                                        .aliveTeammatesOfExcludingSelf(we)
                                ) {
                                    ally.addInstance(InstanceBuilder
                                            .damage()
                                            .cause("Tormenting Mark")
                                            .source(warlordsNPC)
                                            .value(1000)
                                    );
                                }
                            }
                        })
                ));
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        Utils.playGlobalSound(self.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 2, 0.2f);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
    }

}
