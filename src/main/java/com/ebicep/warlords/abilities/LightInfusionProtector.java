package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractLightInfusion;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.protector.LightInfusionBranchProtector;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Particle;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Collections;

public class LightInfusionProtector extends AbstractLightInfusion {

    public LightInfusionProtector(float cooldown) {
        super(cooldown);
    }

    public LightInfusionProtector() {
        super(15.66f);
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        wp.addEnergy(wp, name, energyGiven);
        Utils.playGlobalSound(wp.getLocation(), "paladin.infusionoflight.activation", 2, 1);

        Runnable cancelSpeed = wp.addSpeedModifier(wp, "Infusion", speedBuff, tickDuration, "BASE");

        LightInfusionProtector tempLightInfusion = new LightInfusionProtector(cooldown.getBaseValue());
        wp.getCooldownManager().addRegularCooldown(
                name,
                "INF",
                LightInfusionProtector.class,
                tempLightInfusion,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    cancelSpeed.run();
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 == 0) {
                        wp.getWorld().spawnParticle(
                                Particle.SPELL,
                                wp.getLocation().add(0, 1.2, 0),
                                2,
                                0.3,
                                0.1,
                                0.3,
                                0.2,
                                null,
                                true
                        );
                    }
                })
        );

        if (pveMasterUpgrade) {
            for (HolyRadianceProtector holyRadiance : wp.getAbilitiesMatching(HolyRadianceProtector.class)) {
                holyRadiance.setCurrentCooldown(0);
            }
            wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                    name,
                    "INF GRACE",
                    LightInfusionProtector.class,
                    tempLightInfusion,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    4 * 20,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        if (ticksElapsed % 2 == 0) {
                            wp.getSpeed().removeSlownessModifiers();
                            wp.getCooldownManager().removeDebuffCooldowns();
                        }
                    })
            ) {
                @Override
                public void multiplyKB(Vector currentVector) {
                    currentVector.multiply(0.5);
                }

                @Override
                public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * 0.1f;
                }
            });
        } else if (pveMasterUpgrade2) {
            wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                    "Chiron Light",
                    "CHIRON",
                    LightInfusionProtector.class,
                    tempLightInfusion,
                    wp,
                    CooldownTypes.BUFF,
                    cooldownManager -> {
                    },
                    tickDuration
            ) {
                @Override
                public float modifyHealingFromAttacker(WarlordsDamageHealingEvent event, float currentHealValue) {
                    if (event.getAbility().equals("Protector's Strike")) {
                        return currentHealValue * 1.25f;
                    }
                    return currentHealValue;
                }
            });
            for (WarlordsEntity infusionTarget : PlayerFilter
                    .entitiesAround(wp, 5, 5, 5)
                    .aliveTeammatesOfExcludingSelf(wp)
            ) {
                playCastEffect(infusionTarget);
                infusionTarget.getSpeed().removeSlownessModifiers();
                infusionTarget.getCooldownManager().removeDebuffCooldowns();
                infusionTarget.addSpeedModifier(wp, "Chiron Light", speedBuff, tickDuration);
                infusionTarget.getCooldownManager().addCooldown(new RegularCooldown<>(
                        "Chiron Light",
                        "CHIRON",
                        LightInfusionProtector.class,
                        tempLightInfusion,
                        wp,
                        CooldownTypes.ABILITY,
                        cooldownManager -> {
                        },
                        4 * 20
                ) {
                    @Override
                    protected Listener getListener() {
                        return CooldownManager.getDefaultDebuffImmunityListener();
                    }
                });
            }
        }

        playCastEffect(wp);

        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new LightInfusionBranchProtector(abilityTree, this);
    }

}

