package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.abilities.internal.AbstractLightInfusion;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.avenger.LightInfusionBranchAvenger;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class LightInfusionAvenger extends AbstractLightInfusion {

    public LightInfusionAvenger() {
        super(AbstractAbilityBuilder.create("lightInfusionAvenger").pvp());
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        // pveMasterUpgrade
        AtomicInteger strikesUsed = new AtomicInteger();
        wp.addEnergy(wp, name, energyGiven);
        Utils.playGlobalSound(wp.getLocation(), "paladin.infusionoflight.activation", 2, 1);
        wp.addSpeedModifier(wp, name, speedBuff, tickDuration);
        RegularCooldown<LightInfusionAvenger> infusionCooldown = new RegularCooldown<>(
                name,
                "INF",
                LightInfusionAvenger.class,
                null,
                wp,
                CooldownTypes.ABILITY,

                cooldownManager -> {
                    if (pveMasterUpgrade) {
                        wp.addEnergy(wp, name, 20 * strikesUsed.get());
                        wp.playSound(wp.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 0.9f);
                    }
                },
                cooldownManager -> {
                    wp.getSpeed().removeModifier(name);
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 == 0) {
                        EffectUtils.displayParticle(Particle.EFFECT, wp.getLocation().add(0, 1.2, 0), 2, 0.3, 0.1, 0.3, 0.2);
                    }
                })
        ) {

            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (pveMasterUpgrade) {
                    if (event.getCause().equals("Avenger's Strike")) {
                        strikesUsed.getAndIncrement();
                    }
                }
            }

        };
        if (pveMasterUpgrade2) {
            infusionCooldown.addModifier(Modifier.ENERGY_GAIN_PER_TICK, energyGainPerTick -> energyGainPerTick.addAdditiveModifier(name, 0.5f));
            infusionCooldown.addModifier(Modifier.ENERGY_GAIN_PER_HIT, energyGainPerTick -> energyGainPerTick.addAdditiveModifier(name, 20));
        }
        wp.getCooldownManager().addCooldown(infusionCooldown);
        playCastEffect(wp);
        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new LightInfusionBranchAvenger(abilityTree, this);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
    }

}
