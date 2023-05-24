package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.Duration;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.*;

public class BloodLust extends AbstractAbility implements Duration {

    public float amountHealed = 0;

    private int tickDuration = 300;
    private int damageConvertPercent = 65;
    private int healReductionPercent = 10;

    public BloodLust() {
        super("Blood Lust", 0, 0, 31.32f, 20);
    }


    @Override
    public void updateDescription(Player player) {
        description = "You lust for blood, healing yourself for §a" + damageConvertPercent + "% §7of all the damage you deal. Lasts §6" + format(tickDuration / 20f) + " §7seconds.";
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
                tickDuration,
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
            private final Set<UUID> abilitiesHit = new HashSet<>();

            @Override
            public boolean distinct() {
                return true;
            }

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (pveUpgrade) {
                    if (event.getWarlordsEntity().getCooldownManager().hasCooldown(WoundingStrikeBerserker.class)) {
                        return currentDamageValue * 1.4f;
                    }
                }
                return currentDamageValue;
            }

            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                WarlordsEntity attacker = event.getAttacker();
                float healAmount = currentDamageValue * (getDamageConvertPercent() / 100f);
                if (attacker.isInPve() && event.getUUID() != null) {
                    if (abilitiesHit.contains(event.getUUID())) {
                        healAmount *= healReductionPercent / 100f;
                    } else {
                        abilitiesHit.add(event.getUUID());
                    }
                }
                attacker.addHealingInstance(
                        attacker,
                        name,
                        healAmount,
                        healAmount,
                        0,
                        100,
                        false,
                        false
                );
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

    public float getAmountHealed() {
        return amountHealed;
    }

    public void addAmountHealed(float amountHealed) {
        this.amountHealed += amountHealed;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public int getHealReductionPercent() {
        return healReductionPercent;
    }

    public void setHealReductionPercent(int healReductionPercent) {
        this.healReductionPercent = healReductionPercent;
    }
}
