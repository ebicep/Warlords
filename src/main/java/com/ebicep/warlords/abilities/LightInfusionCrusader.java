package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.abilities.internal.AbstractLightInfusion;
import com.ebicep.warlords.abilities.internal.CanReduceCooldowns;
import com.ebicep.warlords.effects.EffectUtils;
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

    public LightInfusionCrusader() {
        super(AbstractAbilityBuilder.create("lightInfusionCrusader").pvp());
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        wp.addEnergy(wp, name, energyGiven);
        Utils.playGlobalSound(wp.getLocation(), "paladin.infusionoflight.activation", 2, 1);
        wp.addSpeedModifier(wp, name, speedBuff, tickDuration);
        wp.getCooldownManager().addRegularCooldown(name, "INF", LightInfusionCrusader.class, null, wp, CooldownTypes.ABILITY, cooldownManager -> {
                }, cooldownManager -> {
            wp.getSpeed().removeModifier(name);
                }, tickDuration, Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 == 0) {
                        EffectUtils.displayParticle(Particle.EFFECT, wp.getLocation().add(0, 1.2, 0), 2, 0.3, 0.1, 0.3, 0.2);
                    }
                })
        );
        if (pveMasterUpgrade) {
            for (WarlordsEntity infusionTarget : PlayerFilter.entitiesAround(wp, 6, 6, 6).aliveTeammatesOfExcludingSelf(wp)) {
                infusionTarget.addSpeedModifier(wp, name, speedBuff, tickDuration);
                infusionTarget.addEnergy(wp, name, energyGiven / 2f);
                infusionTarget.getCooldownManager().addRegularCooldown(name, "INF", LightInfusionCrusader.class, null, wp, CooldownTypes.ABILITY, cooldownManager -> {
                        }, cooldownManager -> {
                    infusionTarget.getSpeed().removeModifier(name);
                        }, tickDuration, Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                            if (ticksElapsed % 4 == 0) {
                                EffectUtils.displayParticle(Particle.EFFECT, wp.getLocation().add(0, 1.2, 0), 2, 0.3, 0.1, 0.3, 0.2);
                            }
                        })
                );
            }
        } else if (pveMasterUpgrade2) {
            for (WarlordsEntity infusionTarget : PlayerFilter.entitiesAround(wp, 6, 6, 6).aliveTeammatesOfExcludingSelf(wp)) {
                playCastEffect(infusionTarget);
                infusionTarget.getSpec().decreaseAllCooldownTimersBy(2);
                infusionTarget.addEnergy(wp, name, energyGiven / 4f);
            }
        }
        playCastEffect(wp);
        return true;
    }

    @Override
    public boolean canReduceCooldowns() {
        return pveMasterUpgrade2;
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new LightInfusionBranchCrusader(abilityTree, this);
    }

}
