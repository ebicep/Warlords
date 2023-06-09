package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.Duration;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IceBarrier extends AbstractAbility implements Duration {

    private int tickDuration = 120;
    private float damageReductionPercent = 50;
    private int slownessOnMeleeHit = 20;

    public IceBarrier() {
        super("Ice Barrier", 0, 0, 46.98f, 0);
    }

    public IceBarrier(float damageReductionPercent) {
        super("Ice Barrier", 0, 0, 46.98f, 0);
        this.damageReductionPercent = damageReductionPercent;
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Surround yourself with a layer of cold air, reducing damage taken by ")
                               .append(Component.text(format(damageReductionPercent) + "%", NamedTextColor.RED))
                               .append(Component.text(", while active, taking melee damage reduces the attacker's movement speed by "))
                               .append(Component.text(slownessOnMeleeHit + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" for "))
                               .append(Component.text("2", NamedTextColor.GOLD))
                               .append(Component.text(" seconds. Lasts "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        Utils.playGlobalSound(player.getLocation(), "mage.icebarrier.activation", 2, 1);

        IceBarrier tempIceBarrier = new IceBarrier(damageReductionPercent);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "ICE",
                IceBarrier.class,
                tempIceBarrier,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 5 == 0) {
                        Location particleLoc = wp.getLocation().add(0, 1.5, 0);

                        particleLoc.getWorld().spawnParticle(Particle.CLOUD, particleLoc, 1, 0.2, 0.2, 0.2, 0.001, null, true);
                        particleLoc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, particleLoc, 1, 0.3, 0.2, 0.3, 0.0001, null, true);

                        if (pveUpgrade) {
                            Utils.playGlobalSound(particleLoc, Sound.BLOCK_GLASS_BREAK, 1, 1.35f);
                            EffectUtils.playHelixAnimation(
                                    particleLoc.add(0, -1.25, 0),
                                    6,
                                    Particle.FIREWORKS_SPARK,
                                    1,
                                    8
                            );

                            for (WarlordsEntity we : PlayerFilter
                                    .entitiesAround(wp, 6, 6, 6)
                                    .aliveEnemiesOf(wp)
                                    .closestFirst(wp)
                            ) {
                                we.setDamageResistance(we.getSpec().getDamageResistance() - 1);
                                we.addSpeedModifier(wp, "Ice Barrier Slowness", -75, 20);
                            }
                        }

                        for (WarlordsEntity we : PlayerFilter
                                .entitiesAround(wp, 15, 15, 15)
                                .aliveEnemiesOf(wp)
                                .closestFirst(wp)
                        ) {
                            if (we instanceof WarlordsNPC) {
                                ((WarlordsNPC) we).getMob().setTarget(wp);
                            }
                        }
                    }
                })
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                float newDamageValue = currentDamageValue * getDamageReduction();
                event.getWarlordsEntity().addAbsorbed(Math.abs(currentDamageValue - newDamageValue));
                return newDamageValue;
            }

            @Override
            public void multiplyKB(Vector currentVector) {
                if (pveUpgrade) {
                    currentVector.multiply(0.7);
                }
            }
        });

        return true;
    }

    public float getDamageReduction() {
        return (100 - damageReductionPercent) / 100f;
    }

    public float getDamageReductionPercent() {
        return damageReductionPercent;
    }

    public void setDamageReductionPercent(float damageReductionPercent) {
        this.damageReductionPercent = damageReductionPercent;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }


    public int getSlownessOnMeleeHit() {
        return slownessOnMeleeHit;
    }

    public void setSlownessOnMeleeHit(int slownessOnMeleeHit) {
        this.slownessOnMeleeHit = slownessOnMeleeHit;
    }
}
