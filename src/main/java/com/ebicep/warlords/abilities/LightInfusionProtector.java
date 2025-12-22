package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.abilities.internal.AbstractLightInfusion;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.protector.LightInfusionBranchProtector;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class LightInfusionProtector extends AbstractLightInfusion {

    public LightInfusionProtector() {
        super(AbstractAbilityBuilder.create("lightInfusionProtector").pvp());
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        wp.addEnergy(wp, name, energyGiven);
        Utils.playGlobalSound(wp.getLocation(), "paladin.infusionoflight.activation", 2, 1);
        wp.addSpeedModifier(wp, name, speedBuff, tickDuration);
        wp.getCooldownManager().addRegularCooldown(
                name,
                "INF",
                LightInfusionProtector.class,
                null,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {},
                cooldownManager -> {
                    wp.getSpeed().removeModifier(name);
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 == 0) {
                        EffectUtils.displayParticle(Particle.EFFECT, wp.getLocation().add(0, 1.2, 0), 2, 0.3, 0.1, 0.3, 0.2);
                    }
                })
        );
        if (pveMasterUpgrade) {
            for (HolyRadianceProtector holyRadiance : wp.getAbilitiesMatching(HolyRadianceProtector.class)) {
                holyRadiance.setCurrentCooldown(0);
            }
            RegularCooldown<LightInfusionProtector> ornamentOfLightCooldown = new RegularCooldown<>(
                    "Ornament of Light",
                    "ORNA",
                    LightInfusionProtector.class,
                    null,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    4 * 20,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        if (ticksElapsed % 2 == 0) {
                            wp.getSpeed().removeNegativeModifiers();
                            wp.getCooldownManager().removeDebuffCooldowns();
                        }
                    })
            );
            ornamentOfLightCooldown.addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name, 0.1f);
                    }
            );
            wp.addKnockbackModifier(wp, "Ornament of Light", -50, ornamentOfLightCooldown);
            wp.getCooldownManager().addCooldown(ornamentOfLightCooldown);
        }

        if (pveMasterUpgrade2) {
            Utils.playGlobalSound(wp.getLocation(), "paladin.infusionoflight.activation", 2, 0.7f);
            AtomicInteger multiplier = new AtomicInteger(0);
            RegularCooldown<LightInfusionProtector> ornamentOfDarknessCooldown = new RegularCooldown<>(
                    "Ornament of Darkness",
                    "DARK",
                    LightInfusionProtector.class,
                    null,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    tickDuration,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {

                    })
            );
            ornamentOfDarknessCooldown.addModifier(
                    Modifier.MODIFY_OUTGOING_HEALING,
                    (event, currentDamageValue) -> {
                        currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name, 0.2f);
                    }
            );
            ornamentOfDarknessCooldown.addModifier(
                    Modifier.MODIFY_INCOMING_HEALING,
                    (event, currentDamageValue) -> {
                        currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name, 0.2f);
                    }
            );
            ornamentOfDarknessCooldown.addModifier(
                    Modifier.ON_OUTGOING_DAMAGE,
                    (event, currentDamageValue, isCrit) -> {
                        if (event.getFlags().contains(InstanceFlags.DOT)) {
                            return;
                        }
                        multiplier.getAndAdd(5);
                    }
            );
            wp.getCooldownManager().addCooldown(ornamentOfDarknessCooldown);

            addSecondaryAbility(20, () -> {
                        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Ornament of Darkness",
                                "CORRUPT " + Math.min(200, multiplier.get()),
                                LightInfusionProtector.class,
                                null,
                                wp,
                                CooldownTypes.ABILITY,
                                cooldownManager -> {
                                },
                                3 * 20,
                                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                    if (ticksElapsed % 5 == 0) {
                                        EffectUtils.drawRing(wp.getLocation(), 4, 2, Particle.ANGRY_VILLAGER);
                                    }
                                })
                        ).addModifier(
                                Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE,
                                (event, currentDamageValue) -> {
                                    currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                            "Ornament of Darkness",
                                            1 + Math.min(200, multiplier.get()) / 100f
                                    );
                                }
                        ).addModifier(
                                Modifier.MODIFY_OUTGOING_HEALING,
                                (event, currentDamageValue) -> {
                                    currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, "Ornament of Darkness", 0.2f);
                                }
                        ).addModifier(
                                Modifier.MODIFY_INCOMING_HEALING,
                                (event, currentDamageValue) -> {
                                    currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, "Ornament of Darkness", 0.2f);
                                }
                        ));
                    },
                    false,
                    secondaryAbility -> !wp.getCooldownManager().hasCooldown(ornamentOfDarknessCooldown)
            );
        }

        playCastEffect(wp);
        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new LightInfusionBranchProtector(abilityTree, this);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
    }

}
