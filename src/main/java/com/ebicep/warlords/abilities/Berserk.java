package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.berserker.BerserkBranch;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Berserk extends AbstractAbility implements OrangeAbilityIcon, Duration, AbilityStats<Berserk, Berserk.BerserkStats> {

    private final BerserkStats stats = new BerserkStats();
    private int tickDuration = 360;
    private int speedBuff = 30;
    private float damageIncrease = 30;

    public Berserk() {
        super(AbstractAbilityBuilder.create("berserk").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.speedBuff = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("speedBuff"), int.class);
        this.damageIncrease = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageIncrease"), float.class);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("You go into a berserker rage, increasing your damage by ")
                                               .percent(damageIncrease, NamedTextColor.RED)
                                               .text(" and movement speed by ")
                                               .percent(speedBuff, NamedTextColor.WHITE)
                                               .text(". Lasts ")
                                               .durationTicks(tickDuration)
                                               .text(".")
                                               .build();
    }

    private float absorbedDamage = 0;
    private int cooldown = 0;

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "warrior.berserk.activation", 2, 1);
        wp.addSpeedModifier(wp, name, speedBuff, tickDuration);
        wp.getCooldownManager().removeCooldown(Berserk.class, false);
        List<FloatModifiable.FloatModifier> modifiers;
        if (pveMasterUpgrade2) {
            modifiers = wp.getAbilities()
                          .stream()
                          .filter(ability -> !(ability instanceof Berserk))
                          .map(ability -> ability.getCooldown().addMultiplicativeModifierMult(name + " Master", 0.8f))
                          .toList();
        } else {
            modifiers = Collections.emptyList();
        }
        RegularCooldown<Berserk> berserkCooldown = new RegularCooldown<>(
                name,
                "BERS",
                Berserk.class,
                null,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {},
                cooldownManager -> {
                    wp.getSpeed().removeModifier(name);
                    modifiers.forEach(FloatModifiable.FloatModifier::forceEnd);
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 3 == 0) {
                        EffectUtils.displayParticle(Particle.ANGRY_VILLAGER, wp.getLocation().add(0, 1.75, 0), 1, 0, 0, 0, 0.1F);
                    }
                })
        ) {
            int multiplier = 0;

            @Override
            public float addCritChanceFromAttacker(WarlordsDamageHealingEvent event, float currentCritChance) {
                if (pveMasterUpgrade) {
                    if (event.getCause().isEmpty() || event.getCause().equals("Time Warp")) {
                        return currentCritChance;
                    }
                    float critBoost = (0.2f * multiplier);
                    return currentCritChance + Math.min(60, critBoost);
                }
                return currentCritChance;
            }

            @Override
            public float addCritMultiplierFromAttacker(WarlordsDamageHealingEvent event, float currentCritMultiplier) {
                if (pveMasterUpgrade) {
                    if (event.getCause().isEmpty() || event.getCause().equals("Time Warp")) {
                        return currentCritMultiplier;
                    }
                    float critBoost = (0.2f * multiplier);
                    return currentCritMultiplier + Math.min(60, critBoost);
                }
                return currentCritMultiplier;
            }

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                stats.hitsDoneAmplified++;
                multiplier++;

                float damage = currentDamageValue * convertToMultiplicationDecimal(damageIncrease);
                absorbedDamage += damage;
                return damage;
            }
        };
        wp.getCooldownManager().addCooldown(berserkCooldown);
        if (pveMasterUpgrade2) {
            new GameRunnable(wp.getGame()) {
                @Override
                public void run() {
                    cooldown--;
                    if (!wp.getCooldownManager().hasCooldown(berserkCooldown)) {
                        absorbedDamage = 0;
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 20);
            addSecondaryAbility(1, () -> {

                if (cooldown > 0) {
                    wp.sendMessage(Component.text("This ability is still on cooldown!", NamedTextColor.RED));
                    return;
                }

                float finalValue = Math.min(20000, absorbedDamage);
                wp.addEnergy(wp, "Berserk Master Upgrade", finalValue * 0.005f);

                FallingBlockWaveEffect.create(wp.getLocation().clone().add(0, 1, 0), 10, 10, Material.AMETHYST_CLUSTER);
                        Utils.playGlobalSound(wp.getLocation(), "warrior.mortalstrike.impact", 2, 0.5f);
                EffectUtils.strikeLightning(wp.getLocation(), false);

                for (WarlordsEntity enemy : PlayerFilter
                        .entitiesAround(wp,10, 10, 10)
                        .aliveEnemiesOf(wp)
                ) {
                    enemy.addInstance(InstanceBuilder
                            .damage()
                            .cause("Berserk Unleashed")
                            .source(wp)
                            .value(finalValue * 0.4f)
                            .flags(InstanceFlags.IGNORE_DAMAGE_BOOST, InstanceFlags.NO_LUST_HEALING)
                    );
                }

                cooldown = 2;
                absorbedDamage = 0;
            },
                    true,
                    secondaryAbility -> !wp.getCooldownManager().hasCooldown(berserkCooldown)
            );
        }
        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new BerserkBranch(abilityTree, this);
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
    public BerserkStats getAbilityStats() {
        return stats;
    }

    public float getDamageIncrease() {
        return damageIncrease;
    }

    public void setDamageIncrease(float damageIncrease) {
        this.damageIncrease = damageIncrease;
    }

    public int getSpeedBuff() {
        return speedBuff;
    }

    public void setSpeedBuff(int speedBuff) {
        this.speedBuff = speedBuff;
    }

    public static class BerserkStats extends AbstractAbilityStats<Berserk, BerserkStats> {

        @Field("hits_done_amplified")
        private int hitsDoneAmplified = 0;

        @Field("hits_taken_amplified")
        private int hitsTakenAmplified = 0;

        @Override
        public Class<BerserkStats> getClazz() {
            return BerserkStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Hits Done Amplified", hitsDoneAmplified));
            statsDisplay.add(new AbilityStatDisplay("Hits Taken Amplified", hitsTakenAmplified));
            return statsDisplay;
        }

        @Override
        public BerserkStats merge(BerserkStats other, int multiplier) {
            BerserkStats stats = super.merge(other, multiplier);
            stats.hitsDoneAmplified = this.hitsDoneAmplified + other.hitsDoneAmplified * multiplier;
            stats.hitsTakenAmplified = this.hitsTakenAmplified + other.hitsTakenAmplified * multiplier;
            return stats;
        }

        @Override
        public BerserkStats create() {
            return new BerserkStats();
        }

    }

}
