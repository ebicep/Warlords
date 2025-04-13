package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.assassin.ShadowStepBranch;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShadowStep extends AbstractAbility implements PurpleAbilityIcon, Damages<ShadowStep.DamageValues>, AbilityStats<ShadowStep, ShadowStep.ShadowStepStats> {


    private final DamageValues damageValues = new DamageValues();
    private final ShadowStepStats stats = new ShadowStepStats();
    private int fallDamageNegation = 10;

    public ShadowStep() {
        super("Shadow Step", 12, 20);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Leap forward, dealing ")
                .damage(damageValues.shadowStepDamage)
                .text(" damage to all enemies close on cast or when landing on the ground. You take reduced fall damage while leaping.")
                .emptyLine()
                .text("Shadow Step has reduced range when holding a flag.")
                .build();
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        Location playerLoc = wp.getLocation();

        Utils.playGlobalSound(playerLoc, "rogue.drainingmiasma.activation", 1, 2);
        Utils.playGlobalSound(playerLoc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2, 2);

        wp.setFlagPickCooldown(2);

        EffectUtils.playFirework(
                wp.getLocation().add(0, pveMasterUpgrade2 ? 1 : 0, 0),
                FireworkEffect.builder()
                              .withColor(Color.BLACK)
                              .with(FireworkEffect.Type.BALL)
                              .build()
        );

        if (wp.onHorse()) {
            wp.removeHorse();
        }

        if (pveMasterUpgrade2) {
            doShadowDash(wp);
        } else {
            if (wp.getCarriedFlag() != null) {
                wp.setVelocity(name, playerLoc.getDirection().multiply(1).setY(0.35), true);
                wp.setFallDistance(-fallDamageNegation);
            } else {
                wp.setVelocity(name, playerLoc.getDirection().multiply(1.5).setY(0.7), true);
                wp.setFallDistance(-fallDamageNegation);
            }

            doShadowStep(wp, playerLoc);
        }

        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new ShadowStepBranch(abilityTree, this);
    }

    private void doShadowDash(@Nonnull WarlordsEntity wp) {
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Shadow Dash Damage Res",
                null,
                ShadowStep.class,
                new ShadowStep(),
                wp,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                2
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * .25f;
            }
        });
        Set<WarlordsEntity> hit = new HashSet<>();
        LocationBuilder locationBuilder = new LocationBuilder(wp.getEyeLocation());
        for (Block ignored : Utils.getTargetBlockInBetween(wp.getEyeLocation(), 8)) {
            if (!Utils.getTargetBlock(locationBuilder, 1).getType().isAir() ||
                    !locationBuilder.getBlock().getType().isAir() ||
                    !locationBuilder.clone()
                                    .addY(1)
                                    .getBlock()
                                    .getType()
                                    .isAir()
            ) {
                locationBuilder.centerXZBlock();
                boolean isSlab = locationBuilder.clone().addY(-1).getBlock().getBlockData() instanceof Slab;
                locationBuilder.addY(isSlab ? -0.5 : 0);
                break;
            }
            PlayerFilter.entitiesAround(locationBuilder.clone().addY(-1), 2, 2, 2)
                        .aliveEnemiesOf(wp)
                        .excluding(hit)
                        .forEach(warlordsEntity -> {
                            hit.add(warlordsEntity);
                            warlordsEntity.addInstance(InstanceBuilder
                                    .damage()
                                    .cause("Shadow Dash")
                                    .source(wp)
                                    .value(damageValues.shadowStepDamage)
                            );
                        });
            locationBuilder = locationBuilder.forward(1);
            EffectUtils.displayParticle(
                    Particle.SMOKE,
                    locationBuilder.clone().addY(-.5),
                    10,
                    .1,
                    .1,
                    .1,
                    0
            );
        }
        Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2, 1.5f);
        wp.teleportLocationOnly(locationBuilder);

        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Shadow Dash CC",
                null,
                ShadowStep.class,
                new ShadowStep(),
                wp,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                5 * 20
        ) {
            @Override
            public float addCritMultiplierFromAttacker(WarlordsDamageHealingEvent event, float currentCritMultiplier) {
                return currentCritMultiplier * convertToMultiplicationDecimal(Math.min(2f * hit.size(), 20));
            }
        });
    }

    private void doShadowStep(@Nonnull WarlordsEntity wp, Location playerLoc) {
        List<WarlordsEntity> playersHit = new ArrayList<>();
        for (WarlordsEntity assaultTarget : PlayerFilter
                .entitiesAround(wp, 5, 5, 5)
                .aliveEnemiesOf(wp)
        ) {
            stats.totalTargetsHit++;
            assaultTarget.addInstance(InstanceBuilder
                    .damage()
                    .ability(this)
                    .source(wp)
                    .value(damageValues.shadowStepDamage)
            );
            Utils.playGlobalSound(playerLoc, "warrior.revenant.orbsoflife", 2, 1.9f);
            playersHit.add(assaultTarget);
        }

        new GameRunnable(wp.getGame()) {
            double y = playerLoc.getY();
            int counter = 0;

            @Override
            public void run() {
                counter++;
                // if player never lands in the span of 10 seconds, remove damage.
                if (counter == 200 || wp.isDead()) {
                    this.cancel();
                }

                wp.getLocation(playerLoc);
                boolean hitGround = wp.getEntity().isOnGround() || wp.onHorse();
                y = playerLoc.getY();

                if (hitGround) {
                    for (WarlordsEntity landingTarget : PlayerFilter
                            .entitiesAround(wp, 5, 5, 5)
                            .aliveEnemiesOf(wp)
                            .excluding(playersHit)
                    ) {
                        stats.totalTargetsHit++;
                        landingTarget.addInstance(InstanceBuilder
                                .damage()
                                .ability(ShadowStep.this)
                                .source(wp)
                                .value(damageValues.shadowStepDamage)
                        );
                        Utils.playGlobalSound(playerLoc, "warrior.revenant.orbsoflife", 2, 1.9f);
                    }

                    if (pveMasterUpgrade) {
                        pveMasterOnLand(wp);
                    }

                    FireWorkEffectPlayer.playFirework(wp.getLocation(), FireworkEffect.builder()
                                                                                      .withColor(Color.BLACK)
                                                                                      .with(FireworkEffect.Type.BALL)
                                                                                      .build());

                    this.cancel();
                }
            }
        }.runTaskTimer(10, 0);
    }

    private void pveMasterOnLand(WarlordsEntity we) {
        we.addSpeedModifier(we, name, 80, 5 * 20);
        we.getCooldownManager().removeCooldown(ShadowStep.class, false);
        we.getCooldownManager().addCooldown(new RegularCooldown<>(
                "STEP KB",
                "STEP KB",
                ShadowStep.class,
                new ShadowStep(),
                we,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                5 * 20
        ) {
            @Override
            public void multiplyKB(Vector currentVector) {
                currentVector.multiply(0.2);
            }
        });
        for (IncendiaryCurse incendiaryCurse : we.getAbilitiesMatching(IncendiaryCurse.class)) {
            incendiaryCurse.onImpact(we, we.getLocation());
            break;
        }
    }

    public void setFallDamageNegation(int fallDamageNegation) {
        this.fallDamageNegation = fallDamageNegation;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public ShadowStepStats getAbilityStats() {
        return stats;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable shadowStepDamage = new Value.RangedValueCritable(466, 598, 15, 175);
        private final List<Value> values = List.of(shadowStepDamage);

        public Value.RangedValueCritable getShadowStepDamage() {
            return shadowStepDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

    public static class ShadowStepStats extends AbstractAbilityStats<ShadowStep, ShadowStepStats> {

        @Field("total_targets_hit")
        private int totalTargetsHit = 0;

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Hit", totalTargetsHit));
            return statsDisplay;
        }

        @Override
        public ShadowStepStats merge(ShadowStepStats other, int multiplier) {
            ShadowStepStats stats = super.merge(other, multiplier);
            stats.totalTargetsHit = this.totalTargetsHit + other.totalTargetsHit * multiplier;
            return stats;
        }

        @Override
        public Class<ShadowStepStats> getClazz() {
            return ShadowStepStats.class;
        }

        @Override
        public ShadowStepStats create() {
            return new ShadowStepStats();
        }
    }
}