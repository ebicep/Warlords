package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractLightInfusion;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
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

    public LightInfusionAvenger(float cooldown) {
        super(cooldown);
    }

    public LightInfusionAvenger() {
        super(15.5f);
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        // pveMasterUpgrade
        AtomicInteger strikesUsed = new AtomicInteger();

        wp.addEnergy(wp, name, energyGiven);
        Utils.playGlobalSound(wp.getLocation(), "paladin.infusionoflight.activation", 2, 1);

        Runnable cancelSpeed = wp.addSpeedModifier(wp, "Infusion", speedBuff, tickDuration, "BASE");

        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "INF",
                LightInfusionAvenger.class,
                null,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (pveMasterUpgrade) {
                        wp.addEnergy(wp, name, 30 * strikesUsed.get());
                        wp.playSound(wp.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 0.9f);
                    }
                },
                cooldownManager -> {
                    cancelSpeed.run();
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 == 0) {
                        wp.getWorld().spawnParticle(
                                Particle.EFFECT,
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
        ) {
            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (pveMasterUpgrade) {
                    if (event.getCause().equals("Avenger's Strike")) {
                        strikesUsed.getAndIncrement();
                    }
                }
            }

            @Override
            public float addEnergyGainPerTick(float energyGainPerTick) {
                if (pveMasterUpgrade2) {
                    return energyGainPerTick + 0.5f;
                }
                return energyGainPerTick;
            }

            @Override
            public float addEnergyPerHit(WarlordsEntity we, float energyPerHit) {
                if (pveMasterUpgrade2) {
                    return energyPerHit + 20;
                }
                return energyPerHit;
            }
        });

        playCastEffect(wp);

        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new LightInfusionBranchAvenger(abilityTree, this);
    }

}