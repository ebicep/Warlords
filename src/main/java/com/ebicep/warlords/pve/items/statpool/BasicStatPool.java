package com.ebicep.warlords.pve.items.statpool;

import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.custom.ItemAdditiveCooldown;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;

import java.util.*;

public enum BasicStatPool implements StatPool {

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
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            ItemAdditiveCooldown.increaseCritChance(warlordsPlayer, value);
        }
    },
    CRIT_MULTI("Crit Multiplier") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            ItemAdditiveCooldown.increaseCritMultiplier(warlordsPlayer, value);
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

    public static final BasicStatPool[] VALUES = values();
    public static final HashMap<BasicStatPool, StatRange> STAT_RANGES = new HashMap<>() {{
        put(HP, new StatRange(75, 450));
        put(MAX_ENERGY, new StatRange(5, 15));
        put(EPH, new StatRange(1, 3));
        put(SPEED, new StatRange(1, 5));

        put(DAMAGE, new StatRange(1, 5));
        put(HEALING, new StatRange(1, 5));
        put(CRIT_CHANCE, new StatRange(1, 5));
        put(CRIT_MULTI, new StatRange(2, 10));

        put(AGGRO_PRIO, new StatRange(3, 15));
        put(THORNS, new StatRange(2, 10));
        put(KB_RES, new StatRange(2, 10));
        put(REGEN_TIMER, new StatRange(5, 30));
    }};

    public static List<String> getStatPoolLore(Map<BasicStatPool, Integer> statPool, String prefix, boolean inverted) {
        List<String> lore = new ArrayList<>();
        statPool.keySet()
                .stream()
                .sorted(Comparator.comparingInt(Enum::ordinal))
                .forEachOrdered(stat -> lore.add(prefix + (!inverted ? stat.getValueStatFormatted(statPool.get(stat)) : stat.getStatValueFormatted(statPool.get(stat)))));
        return lore;
    }

    public static List<String> getStatPoolLore(Map<BasicStatPool, Integer> statPool, boolean inverted) {
        return getStatPoolLore(statPool, "", inverted);
    }

    public final String name;

    BasicStatPool(String name) {
        this.name = name;
    }

    public String getValueStatFormatted(float value) {
        return ChatColor.GREEN + formatValue(value) + getOperation().prepend + " " + ChatColor.GRAY + getName();
    }

    public String getStatValueFormatted(float value) {
        return ChatColor.GRAY + getName() + ": " + ChatColor.GREEN + formatValue(value) + getOperation().prepend;
    }

    public String formatValue(float value) {
        return NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format(value / getDecimalPlace().value);
    }


    public String getName() {
        return name;
    }


    public static class StatRange {

        private final int min;
        private final int max;

        public StatRange(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }

    }
}
