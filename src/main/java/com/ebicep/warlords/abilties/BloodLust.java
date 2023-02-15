package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BloodLust extends AbstractAbility {

    public float amountHealed = 0;

    private int duration = 15;
    private int damageConvertPercent = 65;
    private float maxConversionAmount = 400;
    private int maxConversionPercent = 100;

    public BloodLust() {
        super("Blood Lust", 0, 0, 31.32f, 20);
    }

    public BloodLust(float maxConversionAmount) {
        this();
        this.maxConversionAmount = maxConversionAmount;
    }

    @Override
    public void updateDescription(Player player) {
        description = "You lust for blood, healing yourself for §a" + damageConvertPercent + "% §7of all the damage you deal. Lasts §6" + duration + " §7seconds.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player p) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(p.getLocation(), "warrior.bloodlust.activation", 2, 1);

        BloodLust tempBloodLust = new BloodLust(maxConversionAmount);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "LUST",
                BloodLust.class,
                tempBloodLust,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                duration * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                            if (ticksElapsed % 3 == 0) {
                                wp.getLocation().getWorld().spawnParticle(
                                        Particle.REDSTONE,
                                        wp.getLocation().add(
                                                (Math.random() - 0.5) * 1,
                                                1.2,
                                                (Math.random() - 0.5) * 1
                                        ),
                                        1,
                                        0,
                                        0,
                                        0,
                                        0,
                                        new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1),
                                        true
                                );
                            }
                        }
                )
        ) {
            @Override
            public boolean distinct() {
                return true;
            }

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (pveUpgrade) {
                    if (event.getPlayer().getCooldownManager().hasCooldown(WoundingStrikeBerserker.class)) {
                        return currentDamageValue * 1.4f;
                    }
                }
                return currentDamageValue;
            }

            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                WarlordsEntity attacker = event.getAttacker();
                if (attacker.isInPve() && tempBloodLust.getAmountHealed() > tempBloodLust.getMaxConversionAmount()) {
                    return;
                }
                float healAmount = currentDamageValue * (getDamageConvertPercent() / 100f);
                attacker.addHealingInstance(
                        attacker,
                        name,
                        Math.min(healAmount, tempBloodLust.getMaxConversionAmount() - tempBloodLust.getAmountHealed()),
                        Math.min(healAmount, tempBloodLust.getMaxConversionAmount() - tempBloodLust.getAmountHealed()),
                        0,
                        100,
                        false,
                        false
                ).ifPresent(warlordsDamageHealingFinalEvent -> {
                    tempBloodLust.addAmountHealed(warlordsDamageHealingFinalEvent.getValue());
                });
            }
        });

        return true;
    }

    public float getAmountHealed() {
        return amountHealed;
    }

    public int getDamageConvertPercent() {
        return damageConvertPercent;
    }

    public void setDamageConvertPercent(int damageConvertPercent) {
        this.damageConvertPercent = damageConvertPercent;
    }

    public void addAmountHealed(float amountHealed) {
        this.amountHealed += amountHealed;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public void updateCustomStats(AbstractPlayerClass apc) {
        if (apc != null) {
            setMaxConversionAmount(apc.getMaxHealth() * (getMaxConversionPercent() / 100f));
            updateDescription(null);
        }
    }

    public int getMaxConversionPercent() {
        return maxConversionPercent;
    }

    public void setMaxConversionPercent(int maxConversionPercent) {
        this.maxConversionPercent = maxConversionPercent;
    }

    public float getMaxConversionAmount() {
        return maxConversionAmount;
    }

    public void setMaxConversionAmount(float maxConversionAmount) {
        this.maxConversionAmount = maxConversionAmount;
    }
}
