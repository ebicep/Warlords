package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IceBarrier extends AbstractAbility {
    private boolean pveUpgrade = false;

    private int duration = 6;
    private int damageReductionPercent = 50;
    private int slownessOnMeleeHit = 20;

    public float getDamageReduction() {
        return (100 - damageReductionPercent) / 100f;
    }

    public IceBarrier() {
        super("Ice Barrier", 0, 0, 46.98f, 0, 0, 0);
    }

    public IceBarrier(int damageReductionPercent) {
        super("Ice Barrier", 0, 0, 46.98f, 0, 0, 0);
        this.damageReductionPercent = damageReductionPercent;
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Surround yourself with a layer of\n" +
                "§7of cold air, reducing damage taken by\n" +
                "§c" + damageReductionPercent + "%§7, While active, taking melee\n" +
                "§7damage reduces the attacker's movement\n" +
                "§7speed by §e" + slownessOnMeleeHit + "% §7for §62 §7seconds. Lasts\n" +
                "§6" + duration + " §7seconds.";
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
        wp.getCooldownManager().addCooldown(new RegularCooldown<IceBarrier>(
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
                        ParticleEffect.CLOUD.display(
                                0.2f,
                                0.2f,
                                0.2f,
                                0.001f,
                                1,
                                particleLoc,
                                500
                        );
                        ParticleEffect.FIREWORKS_SPARK.display(
                                0.3f,
                                0.2f,
                                0.3f,
                                0.0001f,
                                1,
                                particleLoc,
                                500
                        );

                        if (pveUpgrade) {
                            Utils.playGlobalSound(particleLoc, Sound.GLASS, 1, 1.35f);
                            EffectUtils.playHelixAnimation(
                                    particleLoc.add(0, -1.25, 0),
                                    6,
                                    ParticleEffect.FIREWORKS_SPARK,
                                    1,
                                    8
                            );

                            for (WarlordsEntity we : PlayerFilter
                                    .entitiesAround(wp, 6, 6, 6)
                                    .aliveEnemiesOf(wp)
                                    .closestFirst(wp)
                            ) {
                                we.getSpec().setDamageResistance(we.getSpec().getDamageResistance() - 1);
                                we.getSpeed().addSpeedModifier("Ice Barrier Slowness", -80, 20);
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

        for (WarlordsEntity we : PlayerFilter
                .entitiesAround(wp, 10, 10, 10)
                .aliveEnemiesOf(wp)
                .closestFirst(wp)
        ) {
            if (we instanceof WarlordsNPC) {
                ((WarlordsNPC) we).getMob().setTarget(wp);
            }
        }

        return true;
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

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }

    public int getSlownessOnMeleeHit() {
        return slownessOnMeleeHit;
    }

    public void setSlownessOnMeleeHit(int slownessOnMeleeHit) {
        this.slownessOnMeleeHit = slownessOnMeleeHit;
    }
}
