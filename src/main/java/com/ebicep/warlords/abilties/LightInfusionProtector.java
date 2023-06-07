package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractLightInfusionBase;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Collections;

public class LightInfusionProtector extends AbstractLightInfusionBase {

    public LightInfusionProtector(float cooldown) {
        super(cooldown);
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.addEnergy(wp, name, energyGiven);
        Utils.playGlobalSound(player.getLocation(), "paladin.infusionoflight.activation", 2, 1);

        Runnable cancelSpeed = wp.addSpeedModifier(wp, "Infusion", speedBuff, tickDuration, "BASE");

        LightInfusionProtector tempLightInfusion = new LightInfusionProtector(cooldown);
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
                        ParticleEffect.SPELL.display(
                                0.3f,
                                0.1f,
                                0.3f,
                                0.2f,
                                2,
                                wp.getLocation().add(0, 1.2, 0),
                                500
                        );
                    }
                })
        );

        if (pveUpgrade) {
            wp.setBlueCurrentCooldown(0);
            wp.getCooldownManager().addCooldown(new RegularCooldown<LightInfusionProtector>(
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
        }

        for (int i = 0; i < 10; i++) {
            ParticleEffect.SPELL.display(
                    1,
                    0,
                    1,
                    0.3f,
                    3,
                    wp.getLocation().add(0, 1.5, 0),
                    500
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

