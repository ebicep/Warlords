package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractLightInfusionBase;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Collections;

public class LightInfusionAvenger extends AbstractLightInfusionBase {

    private int strikesUsed = 0;

    public LightInfusionAvenger(float cooldown) {
        super(cooldown);
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        strikesUsed = 0;
        wp.addEnergy(wp, name, energyGiven);
        Utils.playGlobalSound(player.getLocation(), "paladin.infusionoflight.activation", 2, 1);

        Runnable cancelSpeed = wp.addSpeedModifier(wp, "Infusion", speedBuff, tickDuration, "BASE");

        LightInfusionAvenger tempLightInfusion = new LightInfusionAvenger(cooldown);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "INF",
                LightInfusionAvenger.class,
                tempLightInfusion,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (pveUpgrade) {
                        wp.addEnergy(wp, name, 30 * strikesUsed);
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
                if (pveUpgrade) {
                    if (event.getAbility().equals("Avenger's Strike")) {
                        strikesUsed++;
                    }
                }
            }
        });

        for (int i = 0; i < 10; i++) {
            wp.getWorld().spawnParticle(
                    Particle.SPELL,
                    wp.getLocation().add(0, 1.5, 0),
                    3,
                    1,
                    0,
                    1,
                    0.3,
                    null,
                    true
            );
        }

        return true;
    }

    public int getSpeedBuff() {
        return speedBuff;
    }

    public void setSpeedBuff(int speedBuff) {
        this.speedBuff = speedBuff;
    }

    public int getEnergyGiven() {
        return energyGiven;
    }

    public void setEnergyGiven(int energyGiven) {
        this.energyGiven = energyGiven;
    }


}
