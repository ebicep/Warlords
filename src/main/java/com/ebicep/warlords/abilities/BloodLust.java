package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.CustomInstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.berserker.BloodlustBranch;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.*;

public class BloodLust extends AbstractAbility implements BlueAbilityIcon, Duration, AbilityStats<BloodLust, BloodLust.BloodLustStats> {

    private final BloodLustStats stats = new BloodLustStats();
    private int tickDuration = 300;
    private int damageConvertPercent = 65;
    private float healReductionPercent = 10;

    public BloodLust() {
        super(AbstractAbilityBuilder.create("bloodLust").pvp());
    }

    public BloodLust(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.damageConvertPercent = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageConvertPercent"), int.class);
        this.healReductionPercent = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("healReductionPercent"), float.class);
    }

    @Override
    public void updateDescription(Player player) {
        if (inPve) {
            description = AbilityDescriptionBuilder.create("You lust for blood, healing yourself for ")
                                                   .percent(damageConvertPercent, NamedTextColor.GREEN)
                                                   .text(" of all the damage you deal. All AOE damage done after the first hit reduces the healing to ")
                                                   .percent(healReductionPercent, NamedTextColor.GREEN)
                                                   .text(". Lasts ")
                                                   .durationTicks(tickDuration)
                                                   .text(".")
                                                   .build();
        } else {
            description = AbilityDescriptionBuilder.create("You lust for blood, healing yourself for ")
                                                   .percent(damageConvertPercent, NamedTextColor.GREEN)
                                                   .text(" of all the damage you deal. Lasts ")
                                                   .durationTicks(tickDuration)
                                                   .text(".")
                                                   .build();
        }
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "warrior.bloodlust.activation", 2, 1);
        BloodLustData data = new BloodLustData();
        wp.getCooldownManager().removeCooldown(BloodLustData.class, false);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "LUST",
                BloodLustData.class,
                data,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 3 == 0) {
                        EffectUtils.displayParticle(Particle.DUST,
                                wp.getLocation().add((Math.random() - 0.5) * 1, 1.2, (Math.random() - 0.5) * 1),
                                1,
                                0,
                                0,
                                0,
                                0,
                                new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1)
                        );
                    }
                })
        ) {

            private final Set<UUID> abilitiesHit = new HashSet<>();

            @Override
            protected Listener getListener() {
                return new Listener() {
                    @EventHandler
                    public void onDamageHealFinalEvent(WarlordsDamageHealingFinalEvent event) {
                        if (event.getSource() != wp) {
                            return;
                        }
                        if (!event.isDamageInstance()) {
                            return;
                        }
                        WarlordsDamageHealingEvent damageHealingEvent = event.getWarlordsDamageHealingEvent();
                        float value = event.getValue();
                        if (event.getFinalEventFlag() != WarlordsDamageHealingFinalEvent.FinalEventFlag.REGULAR) {
                            value = 0;
                        }
                        EnumSet<InstanceFlags> flags = damageHealingEvent.getFlags();
                        if (pveMasterUpgrade2 && event.getCause().equals("Wounding Strike") && !flags.contains(InstanceFlags.RECURSIVE)) {
                            event.getWarlordsEntity().addInstance(InstanceBuilder
                                    .damage()
                                    .cause(event.getCause())
                                    .source(wp)
                                    .value(value * 0.2f)
                                    .showAsCrit(event.isCrit())
                                    .flags(InstanceFlags.RECURSIVE, InstanceFlags.NO_LUST_HEALING)
                                    .customFlags(new CustomInstanceFlags.FinalEventInstanceFlag(event))
                            );
                        }
                        if (flags.contains(InstanceFlags.NO_LUST_HEALING)) {
                            return;
                        }
                        WarlordsEntity attacker = event.getSource();
                        float healAmount = value * convertToPercent(damageConvertPercent);
                        UUID uuid = damageHealingEvent.getUUID();
                        if (attacker.isInPve() && uuid != null) {
                            if (abilitiesHit.contains(uuid)) {
                                healAmount *= convertToPercent(healReductionPercent);
                            } else {
                                abilitiesHit.add(uuid);
                            }
                        }
                        attacker.addInstance(InstanceBuilder
                                .healing()
                                .ability(BloodLust.this)
                                .source(attacker)
                                .value(healAmount)
                                .flags(InstanceFlags.NO_HIT_SOUND)
                                .customFlags(new CustomInstanceFlags.FinalEventInstanceFlag(event))
                        ).ifPresent(finalEvent -> {
                            stats.amountHealed += finalEvent.getValue();
                            data.amountHealed += finalEvent.getValue();
                        });
                    }
                };
            }

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                float damageMultiplier = 1;
                CooldownManager cooldownManager = event.getWarlordsEntity().getCooldownManager();
                if (pveMasterUpgrade) {
                    if (cooldownManager.hasCooldown(WoundingData.class)) {
                        damageMultiplier += 0.3f;
                    }
                } else if (pveMasterUpgrade2) {
                    if (cooldownManager.hasCooldownFromName("Bleed")) {
                        damageMultiplier += 0.3f;
                    }
                }
                return currentDamageValue * damageMultiplier;
            }

        });
        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new BloodlustBranch(abilityTree, this);
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    @Override
    public BloodLustStats getAbilityStats() {
        return stats;
    }

    public int getDamageConvertPercent() {
        return damageConvertPercent;
    }

    public void setDamageConvertPercent(int damageConvertPercent) {
        this.damageConvertPercent = damageConvertPercent;
    }

    public float getHealReductionPercent() {
        return healReductionPercent;
    }

    public void setHealReductionPercent(float healReductionPercent) {
        this.healReductionPercent = healReductionPercent;
    }

    public static class BloodLustData {

        private float amountHealed = 0;

        public float getAmountHealed() {
            return amountHealed;
        }

    }

    public static class BloodLustStats extends AbstractAbilityStats<BloodLust, BloodLustStats> {

        @Field("amount_healed")
        private float amountHealed = 0;

        @Override
        public Class<BloodLustStats> getClazz() {
            return BloodLustStats.class;
        }

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
        public BloodLustStats create() {
            return new BloodLustStats();
        }

        public float getAmountHealed() {
            return amountHealed;
        }

    }

}
