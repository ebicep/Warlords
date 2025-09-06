package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Clairvoyance extends AbstractAbility implements PurpleAbilityIcon, Duration, Heals<Clairvoyance.HealingValues>, AbilityStats<Clairvoyance, Clairvoyance.ClairvoyanceStats> {

    private final ClairvoyanceStats stats = new ClairvoyanceStats();
    private final HealingValues healingValues = new HealingValues();
    private float healingIncreasePercent = 35;
    private int speedIncrease = 40;
    private int tickDuration = 100;

    public Clairvoyance() {
        super(AbstractAbilityBuilder.create("clairvoyance").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.healingIncreasePercent = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("healingIncreasePercent"), float.class);
        this.speedIncrease = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("speedIncrease"), int.class);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
    }


    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "mage.timewarp.activation", 3, 1.5f);
        wp.addSpeedModifier(wp, name, speedIncrease, tickDuration);
        wp.addInstance(InstanceBuilder.healing().ability(this).source(wp).value(healingValues.healing));
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "CLAIR",
                Clairvoyance.class,
                null,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    wp.getSpeed().removeModifier(name);
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {

                })
        ).addModifier(Modifier.HEALING_MODIFY_ATTACKER, (event, currentHealValue) -> {
                    // TODO contribution stats.healingIncreased += currentHealValue * healingIncreasePercent / 100f;
                    currentHealValue.addMultiplicativeModifierMult(name, convertToMultiplicationDecimal(healingIncreasePercent));
                }
        ));
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Heal for ")
                                               .heal(healingValues.healing)
                                               .text(" health and increase your healing output by ")
                                               .percent(healingIncreasePercent, NamedTextColor.GREEN)
                                               .text(" and speed by")
                                               .percent(speedIncrease, NamedTextColor.WHITE)
                                               .text(" for ")
                                               .durationTicks(tickDuration)
                                               .text(".")
                                               .build();
    }

    @Override
    public ClairvoyanceStats getAbilityStats() {
        return stats;
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
    public Clairvoyance.HealingValues getHealValues() {
        return healingValues;
    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.SetValue healing = new Value.SetValue(800);

        private List<Value> values = List.of(healing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.healing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameHealing("healing"),
                    Value.SetValue.class
            );
            this.values = List.of(healing);
        }

    }


    public static class ClairvoyanceStats extends AbstractAbilityStats<Clairvoyance, ClairvoyanceStats> {

        @Field("healing_increased")
        private float healingIncreased = 0;

        @Override
        public Class<ClairvoyanceStats> getClazz() {
            return ClairvoyanceStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Healing Increased", healingIncreased));
            return statsDisplay;
        }

        @Override
        public ClairvoyanceStats merge(ClairvoyanceStats other, int multiplier) {
            ClairvoyanceStats stats = super.merge(other, multiplier);
            stats.healingIncreased = this.healingIncreased + other.healingIncreased * multiplier;
            return stats;
        }

        @Override
        public ClairvoyanceStats create() {
            return new ClairvoyanceStats();
        }

    }

}
