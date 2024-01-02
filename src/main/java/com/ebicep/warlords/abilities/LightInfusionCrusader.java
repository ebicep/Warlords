package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractLightInfusion;
import com.ebicep.warlords.abilities.internal.CanReduceCooldowns;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.crusader.LightInfusionBranchCrusader;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.Collections;

public class LightInfusionCrusader extends AbstractLightInfusion implements CanReduceCooldowns {

    public LightInfusionCrusader(float cooldown) {
        super(cooldown);
    }

    public LightInfusionCrusader() {
        super(15.66f);
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        wp.addEnergy(wp, name, energyGiven);
        Utils.playGlobalSound(wp.getLocation(), "paladin.infusionoflight.activation", 2, 1);

        Runnable cancelSpeed = wp.addSpeedModifier(wp, "Infusion", speedBuff, tickDuration, "BASE");

        LightInfusionCrusader tempLightInfusion = new LightInfusionCrusader(cooldown.getBaseValue());
        wp.getCooldownManager().addRegularCooldown(
                name,
                "INF",
                LightInfusionCrusader.class,
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
            for (WarlordsEntity infusionTarget : PlayerFilter
                    .entitiesAround(wp, 6, 6, 6)
                    .aliveTeammatesOfExcludingSelf(wp)
            ) {
                infusionTarget.addEnergy(wp, name, energyGiven / 2f);
                infusionTarget.getCooldownManager().addRegularCooldown(
                        name,
                        "INF",
                        LightInfusionCrusader.class,
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
            }
        } else if (pveMasterUpgrade2) {
            for (WarlordsEntity infusionTarget : PlayerFilter
                    .entitiesAround(wp, 6, 6, 6)
                    .aliveTeammatesOfExcludingSelf(wp)
            ) {
                playCastEffect(infusionTarget);
                infusionTarget.getSpec().decreaseAllCooldownTimersBy(2);
            }
        }

        playCastEffect(wp);

        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new LightInfusionBranchCrusader(abilityTree, this);
    }

    @Override
    public boolean canReduceCooldowns() {
        return pveMasterUpgrade2;
    }
}
