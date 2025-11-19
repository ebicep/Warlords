package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownUtils;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.apothecary.DrainingMiasmaBranch;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DrainingMiasma extends AbstractAbility implements OrangeAbilityIcon, Duration, Damages<DrainingMiasma.DamageValues>, AbilityStats<DrainingMiasma, DrainingMiasma.DrainingMiasmaStats> {

    private final DrainingMiasmaStats stats = new DrainingMiasmaStats();
    private final DamageValues damageValues = new DamageValues();
    private float maxHealthDamage = 3;
    private int tickDuration = 100;
    private int leechTickDuration = 5;
    private int radius = 8;
    private int slowness = 25;
    private int slownessDuration = 3;

    public DrainingMiasma() {
        super(AbstractAbilityBuilder.create("drainingMiasma").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.maxHealthDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxHealthDamage"), float.class);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.leechTickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("leechTickDuration"), int.class);
        this.radius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radius"), int.class);
        this.slowness = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("slowness"), int.class);
        this.slownessDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("slownessDuration"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "rogue.drainingmiasma.activation", 2, 1.7f);
        Utils.playGlobalSound(wp.getLocation(), "shaman.earthlivingweapon.activation", 2, 0.65f);
        EffectUtils.playSphereAnimation(wp.getLocation(), 6, Particle.ITEM_SLIME, 1);
        EffectUtils.playFirework(wp.getLocation(), FireworkEffect.builder().withColor(Color.LIME).with(FireworkEffect.Type.BALL_LARGE).build());

        if (pveMasterUpgrade) {
            Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_WITHER_SPAWN, 10, 1);
            EffectUtils.playSphereAnimation(wp.getLocation(), radius, Particle.ITEM_SLIME, 1);
            EffectUtils.playFirework(wp.getLocation(), FireworkEffect.builder().withColor(Color.WHITE).with(FireworkEffect.Type.BALL_LARGE).build());
        }

        DrainingMiasmaData data = new DrainingMiasmaData();
        for (WarlordsEntity miasmaTarget : PlayerFilter.entitiesAround(wp, getRadius(), getRadius(), getRadius()).isAlive()) {
            stats.targetsHit++;
            if (miasmaTarget.isEnemy(wp)) {
                miasmaTarget.addSpeedModifier(wp, "Draining Miasma Slow", -slowness, slownessDuration * 20);
                miasmaTarget.getCooldownManager().removeCooldown(DrainingMiasmaData.class, false);
                miasmaTarget.getCooldownManager().addCooldown(new RegularCooldown<>(
                        name,
                        "MIAS",
                        DrainingMiasmaData.class,
                        data,
                        wp,
                        CooldownTypes.HIGH_LEVEL_DEBUFF,
                        cooldownManager -> {
                        },
                        cooldownManager -> {
                            miasmaTarget.getSpeed().removeModifier("Draining Miasma Slow");
                            if (data.numberOfLeechProcd >= 150) {
                                ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.LIFELEECHER);
                            }
                        },
                        tickDuration,
                        Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                            if (ticksElapsed % 20 != 0) {
                                return;
                            }
                            Utils.playGlobalSound(miasmaTarget.getLocation(), Sound.BLOCK_SNOW_BREAK, 2, 0.4f);
                            for (int i = 0; i < 3; i++) {
                                EffectUtils.displayParticle(Particle.DUST,
                                        miasmaTarget.getLocation().clone().add((Math.random() * 2) - 1, 1.2 + (Math.random() * 2) - 1, (Math.random() * 2) - 1),
                                        1,
                                        0,
                                        0,
                                        0,
                                        0,
                                        new Particle.DustOptions(Color.fromRGB(30, 200, 30), 1)
                                );
                            }
                            float healthDamage = miasmaTarget.getMaxHealth() * maxHealthDamage / 100f;
                            healthDamage = DamageCheck.clamp(healthDamage);
                            miasmaTarget.addInstance(InstanceBuilder
                                    .damage()
                                    .ability(this)
                                    .source(wp)
                                    .value(damageValues.miasmaDamage.getValue() + healthDamage)
                                    .flags(InstanceFlags.DOT, InstanceFlags.IGNORE_SOURCE_DAMAGE_BOOST));
                        })
                ));
                if (pveMasterUpgrade) {
                    miasmaTarget.getCooldownManager().addCooldown(new PermanentCooldown<>("Liquidizing Miasma",
                            "LIQ",
                            DrainingMiasmaData.class,
                            data,
                            wp,
                            CooldownTypes.LOW_LEVEL_DEBUFF,
                            cooldownManager -> {
                                FallingBlockWaveEffect.create(miasmaTarget.getLocation(), 3, 6, Material.BIRCH_SAPLING);
                                for (WarlordsEntity target : PlayerFilter
                                        .entitiesAround(miasmaTarget, 6, 6, 6)
                                        .aliveEnemiesOf(wp)
                                ) {
                                    float healthDamage = miasmaTarget.getMaxHealth() * 0.01f;
                                    healthDamage = DamageCheck.clamp(healthDamage);
                                    target.addInstance(InstanceBuilder
                                            .damage()
                                            .ability(this)
                                            .source(wp)
                                            .value(healthDamage)
                                            .flags(InstanceFlags.DOT, InstanceFlags.IGNORE_SOURCE_DAMAGE_BOOST)
                                    );
                                }
                            },
                            true
                    ).addModifier(Modifier.DAMAGE_BEFORE_INTERVENE_ATTACKER, (event, currentDamageValue) -> {
                                currentDamageValue.addMultiplicativeModifierMult(name, 0.75f);
                            }
                    ));
                }
                Leech.giveLeechCooldown(Leech.LeechInstance
                        .create(wp, miasmaTarget)
                        .withImpalingStrike()
                        .withLeechTickDuration(leechTickDuration)
                        .withFinalEventConsumer(finalEvent -> {
                            data.numberOfLeechProcd++;
                        })
                );
            } else {
                if (pveMasterUpgrade2) {
                    miasmaTarget.getCooldownManager().addCooldown(new RegularCooldown<>(
                            "Toxic Immunity",
                            "MIAS",
                            DrainingMiasmaData.class,
                            data,
                            wp,
                            CooldownTypes.ABILITY,
                            cooldownManager -> {},
                            tickDuration,
                            Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                if (ticksElapsed % 20 != 0) {
                                    return;
                                }

                                float healing = miasmaTarget.getMaxHealth() * .02f;
                                miasmaTarget.addInstance(InstanceBuilder
                                        .healing()
                                        .ability(this)
                                        .source(wp)
                                        .value(healing)
                                        .flags(InstanceFlags.CAN_OVERHEAL_OTHERS)
                                );
                                Overheal.giveOverHeal(wp, miasmaTarget);
                            })
                    ) {
                        @Override
                        protected Listener getListener() {
                            return CooldownUtils.getFullDebuffImmunityListener(miasmaTarget);
                        }
                    });
                }
            }
        }
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Summon a toxin-filled cloud around you, poisoning all enemies inside the area. Poisoned enemies take ")
                                               .damage(damageValues.miasmaDamage)
                                               .text(" + ")
                                               .percent(maxHealthDamage, NamedTextColor.RED)
                                               .text(" of their max health as damage per second, for ")
                                               .durationTicks(tickDuration)
                                               .text(". Enemies poisoned by your Draining Miasma are slowed by ")
                                               .percent(slowness, NamedTextColor.WHITE)
                                               .text(" for ")
                                               .durationSeconds(slownessDuration)
                                               .text(" on cast. Has a radius of ")
                                               .blocks(radius)
                                               .text(".")
                                               .emptyLine()
                                               .text("Each enemy hit will be afflicted with ")
                                               .text("LEECH", NamedTextColor.DARK_GREEN)
                                               .text(" for ")
                                               .durationTicks(leechTickDuration)
                                               .text(".")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new DrainingMiasmaBranch(abilityTree, this);
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public DrainingMiasmaStats getAbilityStats() {
        return stats;
    }

    public int getLeechTickDuration() {
        return leechTickDuration;
    }

    public void setLeechTickDuration(int leechTickDuration) {
        this.leechTickDuration = leechTickDuration;
    }

    public float getMaxHealthDamage() {
        return maxHealthDamage;
    }

    public void setMaxHealthDamage(float maxHealthDamage) {
        this.maxHealthDamage = maxHealthDamage;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.SetValue miasmaDamage = new Value.SetValue(50);

        private List<Value> values = List.of(miasmaDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.miasmaDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("miasmaDamage"), Value.SetValue.class);
            this.values = List.of(miasmaDamage);
        }

        public Value.SetValue getMiasmaDamage() {
            return miasmaDamage;
        }

    }

    public static class DrainingMiasmaData {

        private int numberOfLeechProcd = 0;

    }

    public static class DrainingMiasmaStats extends AbstractAbilityStats<DrainingMiasma, DrainingMiasmaStats> {

        @Field("targets_hit")
        private int targetsHit = 0;

        @Override
        public Class<DrainingMiasmaStats> getClazz() {
            return DrainingMiasmaStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Hit", targetsHit));
            return statsDisplay;
        }

        @Override
        public DrainingMiasmaStats merge(DrainingMiasmaStats other, int multiplier) {
            DrainingMiasmaStats stats = super.merge(other, multiplier);
            stats.targetsHit = this.targetsHit + other.targetsHit * multiplier;
            return stats;
        }

        @Override
        public DrainingMiasmaStats create() {
            return new DrainingMiasmaStats();
        }

    }

}
