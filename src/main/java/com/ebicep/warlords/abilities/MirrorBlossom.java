package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.OrbitingItemManager;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.assassin.MirrorBlossomBranch;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MirrorBlossom extends AbstractAbility implements BlueAbilityIcon, HitBox, Duration {

    private final FloatModifiable radius = new FloatModifiable(6);
    private int tickDuration = 100;
    private final int slowness = 25;
    private final int critMultiplierIncrease = 50;

    public MirrorBlossom() {
        super(AbstractAbilityBuilder.create("mirrorBlossom").pvp());
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {

        Location groundLocation = wp.getLocation().clone();
        float hitbox = radius.getCalculatedValue();
        EffectUtils.playHelixAnimation(wp.getLocation(), hitbox, Particle.WARPED_SPORE, 2, 20);
        Utils.playGlobalSound(wp.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 2, 0.7f);
        Utils.playGlobalSound(groundLocation, Sound.BLOCK_AMETHYST_CLUSTER_BREAK, 1, 0.5f);
        AtomicInteger stacks = new AtomicInteger(0);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "BLOSSOM",
                MirrorBlossom.class,
                null,
                wp,
                CooldownTypes.ABILITY,
                cm -> {
                    if (!pveMasterUpgrade2) {
                        return;
                    }
                    Utils.playGlobalSound(groundLocation, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 2, 0.5f);
                    EffectUtils.playBlossomAnimation(groundLocation, hitbox * 2, Particle.FLAME, 0);
                    EffectUtils.playFirework(groundLocation, FireworkEffect.builder()
                            .with(FireworkEffect.Type.BALL_LARGE)
                            .withColor(Color.SILVER)
                            .withTrail()
                            .build()
                    );
                    PlayerFilter.entitiesAround(groundLocation, hitbox, hitbox, hitbox)
                            .aliveEnemiesOf(wp)
                            .forEach(enemy -> {
                                enemy.addInstance(InstanceBuilder
                                        .damage()
                                        .cause("Mirror Blossom")
                                        .source(wp)
                                        .min(2205)
                                        .max(2989)
                                        .flags(InstanceFlags.PIERCE)
                                );
                            });
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % (pveMasterUpgrade ? 5 : 10) == 0) {
                        Utils.playGlobalSound(groundLocation, "notreadyalert", 2, 0.5f);
                        EffectUtils.playBlossomAnimation(groundLocation, hitbox, Particle.CHERRY_LEAVES, ticksElapsed);
                        PlayerFilter.entitiesAround(groundLocation, hitbox, hitbox, hitbox)
                                .aliveEnemiesOf(wp)
                                .forEach(enemy -> {
                                    enemy.addInstance(InstanceBuilder
                                            .damage()
                                            .cause("Mirror Blossom")
                                            .source(wp)
                                            .min(127)
                                            .max(163)
                                            .critChance(20)
                                            .critMultiplier(175)
                                    );
                                    enemy.addSpeedModifier(wp, getName(), -slowness, 20 * 2);

                                    enemy.getCooldownManager().limitCooldowns(RegularCooldown.class, MirrorBlossomData.class, 1);
                                    enemy.getCooldownManager().addCooldown(new RegularCooldown<>(
                                            name + " Debuff",
                                            null,
                                            MirrorBlossomData.class,
                                            null,
                                            wp,
                                            CooldownTypes.LOW_LEVEL_DEBUFF,
                                            cm -> {},
                                            10 * 20
                                    ).addModifier(Modifier.ON_INCOMING_DAMAGE, (event, currentDamageValue, isCrit) -> {
                                        if (event.getCause().contains("Mirror")) {
                                            stacks.getAndIncrement();
                                        }
                                    }).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                                        if (!pveMasterUpgrade) {
                                            return;
                                        }
                                        if (event.getCause().contains("Judgement")) {
                                            float multiplier = Math.clamp(1 + (stacks.get() * .05f), 1, 2);
                                            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name, multiplier);
                                        }
                                    }));
                                });
                    }

                })
        ).addModifier(Modifier.MODIFY_OUTGOING_CRIT_MULTIPLIER, (event, currentCritMultiplier) -> {
            currentCritMultiplier.addModifier(FloatModifiable.ModifierType.ADDITIVE, name, critMultiplierIncrease);
        }));

        // flying swords
        addSecondaryAbility(10, () -> {
            wp.setVelocity(name, new Vector(0, 1, 0), true);
            wp.getCooldownManager().removeStrongDebuffCooldowns();
            wp.getCooldownManager().removeDebuffCooldowns();
            Utils.playGlobalSound(wp.getLocation(), "warrior.revenant.orbsoflife", 2, 1.9f);
            var orbit = new OrbitingItemManager(
                    wp::getLocation,
                    hitbox,
                    1.5,
                    12,
                    1.5f,
                    wp,
                    Objects.requireNonNull(wp.getWeaponItem()).getType()
            );
            orbit.spawnItems(12);
            orbit.start();

            PlayerFilter.entitiesAround(groundLocation, hitbox, hitbox, hitbox)
                    .aliveEnemiesOf(wp)
                    .forEach(enemy -> {
                        enemy.addInstance(InstanceBuilder
                                .damage()
                                .cause("Mirror Blossom")
                                .source(wp)
                                .min(809)
                                .max(1205)
                                .critChance(20)
                                .critMultiplier(175)
                        );
                    });
            new GameRunnable(wp.getGame()) {
                @Override
                public void run() {
                    orbit.stop();
                }
            }.runTaskLater(15);
        }, false, secondaryAbility -> !wp.getCooldownManager().hasCooldown(MirrorBlossom.class));

        return true;
    }

    public static class MirrorBlossomData {

    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return radius;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Summon a Mirror Blossom for ")
                .durationTicks(tickDuration)
                .text(" on the ground dealing ")
                .text("127 - 163", NamedTextColor.RED)
                .text(" damage every 0.5 seconds to enemies within ")
                .text(radius, NamedTextColor.BLUE)
                .text(" blocks. Enemies hit are slowed by ")
                .percent(slowness, NamedTextColor.YELLOW)
                .text(". Additionally, increase your crit multiplier by ")
                .percent(critMultiplierIncrease, NamedTextColor.RED)
                .text(" while Mirror Blossom is active.")
                .emptyLine()
                .text("You may re-cast Mirror Blossom once to jump into the air, dealing")
                .text("809 - 1205", NamedTextColor.RED)
                .text(" damage to nearby enemies and cleanse yourself from all de-buffs.")
                .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new MirrorBlossomBranch(abilityTree, this);
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }
}
