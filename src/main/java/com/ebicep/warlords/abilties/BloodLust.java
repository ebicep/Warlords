package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BloodLust extends AbstractAbility {
    private boolean pveUpgrade = false;

    private int duration = 15;
    private int damageConvertPercent = 65;

    private float amountHealed = 0;

    public BloodLust() {
        super("Blood Lust", 0, 0, 31.32f, 20);
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

        BloodLust tempBloodLust = new BloodLust();
        wp.getCooldownManager().addCooldown(new RegularCooldown<BloodLust>(
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
                                ParticleEffect.REDSTONE.display(
                                        new ParticleEffect.OrdinaryColor(255, 0, 0),
                                        wp.getLocation().add(
                                                (Math.random() - 0.5) * 1,
                                                1.2,
                                                (Math.random() - 0.5) * 1
                                        ),
                                        500
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
                        return currentDamageValue * 1.2f;
                    }
                }
                return currentDamageValue;
            }

            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                WarlordsEntity attacker = event.getAttacker();
                attacker.addHealingInstance(
                        attacker,
                        name,
                        currentDamageValue * (getDamageConvertPercent() / 100f),
                        currentDamageValue * (getDamageConvertPercent() / 100f),
                        -1,
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

    public int getDamageConvertPercent() {
        return damageConvertPercent;
    }

    public void setDamageConvertPercent(int damageConvertPercent) {
        this.damageConvertPercent = damageConvertPercent;
    }

    public void addAmountHealed(float amountHealed) {
        this.amountHealed += amountHealed;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getAmountHealed() {
        return amountHealed;
    }
}
