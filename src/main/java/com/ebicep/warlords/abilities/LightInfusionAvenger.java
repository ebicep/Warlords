package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractLightInfusion;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.avenger.LightInfusionBranchAvenger;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LightInfusionAvenger extends AbstractLightInfusion {

    public LightInfusionAvenger(float cooldown) {
        super(cooldown);
    }

    public LightInfusionAvenger() {
        super(15.66f);
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        // pveMasterUpgrade
        AtomicInteger strikesUsed = new AtomicInteger();

        wp.addEnergy(wp, name, energyGiven);
        Utils.playGlobalSound(wp.getLocation(), "paladin.infusionoflight.activation", 2, 1);

        Runnable cancelSpeed = wp.addSpeedModifier(wp, "Infusion", speedBuff, tickDuration, "BASE");

        LightInfusionAvenger tempLightInfusion = new LightInfusionAvenger(cooldown.getBaseValue());
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "INF",
                LightInfusionAvenger.class,
                tempLightInfusion,
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
        ) {
            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (pveMasterUpgrade) {
                    if (event.getAbility().equals("Avenger's Strike")) {
                        strikesUsed.getAndIncrement();
                    }
                }
            }
        });

        if (pveMasterUpgrade2) {
            List<WarlordsEntity> teammates = PlayerFilter.entitiesAround(wp, 5, 5, 5)
                                                         .aliveTeammatesOfExcludingSelf(wp)
                                                         .toList();
            int duration = (5 + teammates.size()) * 20;
            for (WarlordsEntity teammate : teammates) {
                playCastEffect(teammate);
                teammate.getCooldownManager().addCooldown(new RegularCooldown<>(
                        "Stellar Light",
                        "STELLAR",
                        LightInfusionAvenger.class,
                        tempLightInfusion,
                        wp,
                        CooldownTypes.BUFF,
                        cooldownManager -> {
                        },
                        duration
                ) {
                    @Override
                    public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        return currentDamageValue * 1.1f;
                    }
                });
            }
        }

        playCastEffect(wp);

        return true;
    }


    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new LightInfusionBranchAvenger(abilityTree, this);
    }

}
