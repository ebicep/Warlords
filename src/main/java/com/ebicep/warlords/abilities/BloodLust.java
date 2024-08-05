package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.berserker.BloodlustBranch;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.*;

public class BloodLust extends AbstractAbility implements BlueAbilityIcon, Duration, AbilityStats<BloodLust, BloodLust.BloodLustStats> {

    private final BloodLustStats stats = new BloodLustStats();
    private int tickDuration = 300;
    private int damageConvertPercent = 65;
    private float healReductionPercent = 10;

    public BloodLust() {
        super("Blood Lust", 31.5f, 20);
    }

    @Override
    public void updateDescription(Player player) {
        if (inPve) {
            description = AbilityDescriptionBuilder
                    .create("You lust for blood, healing yourself for ")
                    .percent(damageConvertPercent, NamedTextColor.GREEN)
                    .text(" of all the damage you deal. All AOE damage done after the first hit reduces the healing to ")
                    .percent(healReductionPercent, NamedTextColor.GREEN)
                    .text(". Lasts ")
                    .durationTicks(tickDuration)
                    .text(".")
                    .build();
        } else {
            description = AbilityDescriptionBuilder
                    .create("You lust for blood, healing yourself for ")
                    .percent(damageConvertPercent, NamedTextColor.GREEN)
                    .text(" of all the damage you deal. Lasts ")
                    .durationTicks(tickDuration)
                    .text(".")
                    .build();
        }
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        Utils.playGlobalSound(wp.getLocation(), "warrior.bloodlust.activation", 2, 1);

        BloodLust tempBloodLust = new BloodLust();
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
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
                                EffectUtils.displayParticle(
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
                                        new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1)
                                );
                            }
                        }
                )
        ) {
            private final Set<UUID> abilitiesHit = new HashSet<>();
            private int timesBerserkReduced = 0;

            @Override
            public boolean distinct() {
                return true;
            }

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (pveMasterUpgrade) {
                    if (event.getWarlordsEntity().getCooldownManager().hasCooldownFromName("Wounding Strike")) {
                        return currentDamageValue * 1.3f;
                    }
                }
                return currentDamageValue;
            }

            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                WarlordsEntity attacker = event.getSource();
                float healAmount = currentDamageValue * convertToPercent(damageConvertPercent);
                if (attacker.isInPve() && event.getUUID() != null) {
                    if (abilitiesHit.contains(event.getUUID())) {
                        healAmount *= convertToPercent(healReductionPercent);
                    } else {
                        abilitiesHit.add(event.getUUID());
                    }
                }
                attacker.addInstance(InstanceBuilder
                        .healing()
                        .ability(BloodLust.this)
                        .source(attacker)
                        .value(healAmount)
                        .flags(InstanceFlags.NO_HIT_SOUND)
                ).ifPresent(finalEvent -> {
                    stats.amountHealed += finalEvent.getValue();
                });
            }

            @Override
            public void onDeathFromEnemies(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit, boolean isKiller) {
                if (pveMasterUpgrade2 && isKiller && timesBerserkReduced < 10) {
                    timesBerserkReduced++;
                    wp.getAbilitiesMatching(Berserk.class).forEach(berserk -> berserk.subtractCurrentCooldown(.5f));
                    playCooldownReductionEffect(event.getWarlordsEntity());
                }
            }
        });

        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new BloodlustBranch(abilityTree, this);
    }

    public int getDamageConvertPercent() {
        return damageConvertPercent;
    }

    public void setDamageConvertPercent(int damageConvertPercent) {
        this.damageConvertPercent = damageConvertPercent;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public float getHealReductionPercent() {
        return healReductionPercent;
    }

    public void setHealReductionPercent(float healReductionPercent) {
        this.healReductionPercent = healReductionPercent;
    }

    @Override
    public BloodLustStats getAbilityStats() {
        return stats;
    }

    public static class BloodLustStats extends AbstractAbilityStats<BloodLust, BloodLustStats> {

        @Field("amount_healed")
        private float amountHealed = 0;

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Amount Healed", amountHealed));
            return statsDisplay;
        }

        @Override
        public BloodLustStats merge(BloodLustStats other, int multiplier) {
            BloodLustStats stats = super.merge(other, multiplier);
            stats.amountHealed = this.amountHealed + other.amountHealed * multiplier;
            return stats;
        }

        @Override
        public Class<BloodLustStats> getClazz() {
            return BloodLustStats.class;
        }

        @Override
        public BloodLustStats create() {
            return new BloodLustStats();
        }

        public float getAmountHealed() {
            return amountHealed;
        }
    }
}