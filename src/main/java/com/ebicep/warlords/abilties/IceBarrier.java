package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IceBarrier extends AbstractAbility {

    private int duration = 6;
    private int damageReductionPercent = 50;
    private int slownessOnMeleeHit = 20;

    public IceBarrier() {
        super("Ice Barrier", 0, 0, 46.98f, 0);
    }

    public IceBarrier(int damageReductionPercent) {
        super("Ice Barrier", 0, 0, 46.98f, 0);
        this.damageReductionPercent = damageReductionPercent;
    }

    @Override
    public void updateDescription(Player player) {
        description = "Surround yourself with a layer of of cold air, reducing damage taken by §c" + damageReductionPercent +
                "%§7, While active, taking melee damage reduces the attacker's movement speed by §e" + slownessOnMeleeHit +
                "% §7for §62 §7seconds. Lasts §6" + duration + " §7seconds.";
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
                duration * 20,
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
                                we.getSpec().setDamageResistance(we.getSpec().getDamageResistance() - 1);
                                we.addSpeedModifier(wp, "Ice Barrier Slowness", -80, 20);
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
                event.getPlayer().addAbsorbed(Math.abs(currentDamageValue - newDamageValue));
                return newDamageValue;
            }
        });

        return true;
    }

    public float getDamageReduction() {
        return (100 - damageReductionPercent) / 100f;
    }

    public int getDamageReductionPercent() {
        return damageReductionPercent;
    }

    public void setDamageReductionPercent(int damageReductionPercent) {
        this.damageReductionPercent = damageReductionPercent;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


    public int getSlownessOnMeleeHit() {
        return slownessOnMeleeHit;
    }

    public void setSlownessOnMeleeHit(int slownessOnMeleeHit) {
        this.slownessOnMeleeHit = slownessOnMeleeHit;
    }
}
