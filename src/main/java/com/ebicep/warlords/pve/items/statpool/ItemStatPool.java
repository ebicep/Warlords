package com.ebicep.warlords.pve.items.statpool;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.custom.ItemAdditiveCooldown;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;

import java.util.HashMap;

public enum ItemStatPool {

    HP("Health") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            warlordsPlayer.setMaxBaseHealth(warlordsPlayer.getMaxBaseHealth() + value);
        }

        @Override
        public Operation getOperation() {
            return Operation.ADD;
        }

        @Override
        public DecimalPlace getDecimalPlace() {
            return DecimalPlace.ONES;
        }
    },
    MAX_ENERGY("Max Energy") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            AbstractPlayerClass playerClass = warlordsPlayer.getSpec();
            playerClass.setMaxEnergy(playerClass.getMaxEnergy() + (int) value);
        }

        @Override
        public Operation getOperation() {
            return Operation.ADD;
        }

        @Override
        public DecimalPlace getDecimalPlace() {
            return DecimalPlace.ONES;
        }
    },
    EPH("Energy Per Hit") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            AbstractPlayerClass playerClass = warlordsPlayer.getSpec();
            playerClass.setEnergyPerHit(playerClass.getEnergyPerHit() + value);
        }

        @Override
        public Operation getOperation() {
            return Operation.ADD;
        }

        @Override
        public DecimalPlace getDecimalPlace() {
            return DecimalPlace.ONES;
        }
    },
    SPEED("Speed") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            warlordsPlayer.getSpeed().addBaseModifier(value);
        }
    },
    DAMAGE("Damage") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            ItemAdditiveCooldown.increaseDamage(warlordsPlayer, value);
        }
    },
    HEALING("Healing") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            ItemAdditiveCooldown.increaseHealing(warlordsPlayer, value);
        }
    },
    CRIT_CHANCE("Crit Chance") {
        @Override
        public void applyToAbility(AbstractAbility ability, float value, ItemTier highestTier) {
            float calculatedValue = 1 + value / 100f;
            ability.setCritChance(ability.getCritChance() * calculatedValue);
        }
    },
    CRIT_MULTI("Crit Multiplier") {
        @Override
        public void applyToAbility(AbstractAbility ability, float value, ItemTier highestTier) {
            float calculatedValue = 1 + value / 100f;
            ability.setCritMultiplier(ability.getCritMultiplier() * calculatedValue);
        }
    },
    AGGRO_PRIO("Aggression Priority") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            warlordsPlayer.setBonusAgroWeight(warlordsPlayer.getBonusAgroWeight() + value);
        }

        @Override
        public Operation getOperation() {
            return Operation.ADD;
        }
    },
    THORNS("Thorns") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            ItemAdditiveCooldown.increaseThorns(warlordsPlayer, value, highestTier.maxThornsDamage);
        }
    },
    KB_RES("Knockback Resistance") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            ItemAdditiveCooldown.increaseKBRes(warlordsPlayer, value);
        }
    },
    REGEN_TIMER("Shorter Regen Timer") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            warlordsPlayer.setRegenTickTimerModifier(1 - value / 100f);
        }
    },

    ;

    public static final ItemStatPool[] VALUES = values();
    public static final HashMap<ItemStatPool, ItemTier.StatRange> STAT_RANGES = new HashMap<>() {{
        put(HP, new ItemTier.StatRange(75, 450));
        put(MAX_ENERGY, new ItemTier.StatRange(10, 30));
        put(EPH, new ItemTier.StatRange(2, 10));
        put(SPEED, new ItemTier.StatRange(2, 10));

        put(DAMAGE, new ItemTier.StatRange(5, 25));
        put(HEALING, new ItemTier.StatRange(5, 15));
        put(CRIT_CHANCE, new ItemTier.StatRange(2, 10));
        put(CRIT_MULTI, new ItemTier.StatRange(5, 25));

        put(AGGRO_PRIO, new ItemTier.StatRange(3, 15));
        put(THORNS, new ItemTier.StatRange(3, 15));
        put(KB_RES, new ItemTier.StatRange(3, 15));
        put(REGEN_TIMER, new ItemTier.StatRange(5, 45));
    }};
    public final String name;

    ItemStatPool(String name) {
        this.name = name;
    }

    public void applyToAbility(AbstractAbility ability, float value, ItemTier highestTier) {

    }

    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {

    }

    public String getValueFormatted(float value) {
        return ChatColor.GREEN + formatValue(value) + getOperation().prepend + " " + ChatColor.GRAY + getName();
    }

    public String formatValue(float value) {
        return NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format(value / getDecimalPlace().value);
    }


    public Operation getOperation() {
        return Operation.MULTIPLY;
    }

    public String getName() {
        return name;
    }

    public DecimalPlace getDecimalPlace() {
        return DecimalPlace.TENTHS;
    }


    public enum Operation {
        ADD(""),
        MULTIPLY("%");

        public final String prepend;

        Operation(String prepend) {
            this.prepend = prepend;
        }
    }

    public enum DecimalPlace {

        TENTHS(10),
        ONES(1);

        public final int value;

        DecimalPlace(int value) {
            this.value = value;
        }
    }

}
