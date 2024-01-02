package com.ebicep.warlords.pve.items.statpool;

import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.custom.ItemAdditiveCooldown;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.*;

public enum BasicStatPool implements StatPool {

    HP("Health") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            warlordsPlayer.getHealth().addAdditiveModifier(getName() + " (Base)", value);
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
            ItemAdditiveCooldown.giveCooldown(warlordsPlayer, itemAdditiveCooldown -> itemAdditiveCooldown.addDamageBoost(value));
        }
    },
    HEALING("Healing") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            ItemAdditiveCooldown.giveCooldown(warlordsPlayer, itemAdditiveCooldown -> itemAdditiveCooldown.addHealBoost(value));
        }
    },
    CRIT_CHANCE("Crit Chance") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            ItemAdditiveCooldown.giveCooldown(warlordsPlayer, itemAdditiveCooldown -> itemAdditiveCooldown.addCritChance(value));
        }
    },
    CRIT_MULTI("Crit Multiplier") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            ItemAdditiveCooldown.giveCooldown(warlordsPlayer, itemAdditiveCooldown -> itemAdditiveCooldown.addCritMultiplier(value));
        }
    },
    AGGRO_PRIO("Aggression Priority") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            warlordsPlayer.setBonusAggroWeight(warlordsPlayer.getBonusAggroWeight() + value);
        }

        @Override
        public Operation getOperation() {
            return Operation.ADD;
        }
    },
    THORNS("Thorns") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            ItemAdditiveCooldown.giveCooldown(warlordsPlayer, itemAdditiveCooldown -> itemAdditiveCooldown.addThorns(value, highestTier.maxThornsDamage));
        }
    },
    KB_RES("Knockback Resistance") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            ItemAdditiveCooldown.giveCooldown(warlordsPlayer, itemAdditiveCooldown -> itemAdditiveCooldown.addKBRes(value));
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
        put(HP, new StatRange(25, 150));
        put(MAX_ENERGY, new StatRange(2, 7));
        put(EPH, new StatRange(.25f, 1));
        put(SPEED, new StatRange(1, 3));

        put(DAMAGE, new StatRange(.5f, 3));
        put(HEALING, new StatRange(.5f, 3));
        put(CRIT_CHANCE, new StatRange(.5f, 3));
        put(CRIT_MULTI, new StatRange(2, 6));

        put(AGGRO_PRIO, new StatRange(2, 8));
        put(THORNS, new StatRange(1, 5));
        put(KB_RES, new StatRange(1, 5));
        put(REGEN_TIMER, new StatRange(2, 10));
    }};

    public static List<Component> getStatPoolLore(Map<BasicStatPool, Float> statPool, boolean inverted, BasicStatPool obfuscatedStat) {
        return getStatPoolLore(statPool, Component.empty(), inverted, obfuscatedStat);
    }

    public static List<Component> getStatPoolLore(Map<BasicStatPool, Float> statPool, Component prefix, boolean inverted, BasicStatPool obfuscatedStat) {
        List<Component> lore = new ArrayList<>();
        // separate because need to shuffle because people can determine stat based on position
        if (obfuscatedStat != null) {
            List<BasicStatPool> basicStatPools = new ArrayList<>(statPool.keySet());
            Collections.shuffle(basicStatPools);
            basicStatPools.forEach(stat -> {
                if (obfuscatedStat == stat) {
                    lore.add(Component.textOfChildren(
                            prefix,
                            Component.text("????????????????", NamedTextColor.GREEN)
                    ));
                } else {
                    lore.add(Component.textOfChildren(
                            prefix,
                            (!inverted ? stat.getValueStatFormattedObfuscated() : stat.getStatValueFormattedObfuscated())
                    ));
                }
            });
        } else {
            statPool.keySet()
                    .stream()
                    .sorted(Comparator.comparingInt(Enum::ordinal))
                    .forEachOrdered(stat -> {
                        lore.add(Component.textOfChildren(
                                prefix,
                                (!inverted ? stat.getValueStatFormatted(statPool.get(stat)) : stat.getStatValueFormatted(statPool.get(stat)))
                        ));
                    });
        }
        return lore;
    }

    public Component getValueStatFormattedObfuscated() {
        return Component.textOfChildren(
                Component.text("??" + getOperation().prepend, NamedTextColor.GREEN),
                Component.text(" " + getName(), NamedTextColor.GRAY)
        );
    }

    public Component getStatValueFormattedObfuscated() {
        return Component.textOfChildren(
                Component.text(getName() + ": ", NamedTextColor.GRAY),
                Component.text("??" + getOperation().prepend, NamedTextColor.GREEN)
        );
    }

    public Component getValueStatFormatted(float value) {
        return Component.textOfChildren(
                Component.text(formatValue(value) + getOperation().prepend, NamedTextColor.GREEN),
                Component.text(" " + getName(), NamedTextColor.GRAY)
        );
    }

    public Component getStatValueFormatted(float value) {
        return Component.textOfChildren(
                Component.text(getName() + ": ", NamedTextColor.GRAY),
                Component.text(formatValue(value) + getOperation().prepend, NamedTextColor.GREEN)
        );
    }

    public String getName() {
        return name;
    }

    public String formatValue(float value) {
        return NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format(value / getDecimalPlace().value);
    }

    public final String name;


    BasicStatPool(String name) {
        this.name = name;
    }

    public record StatRange(float min, float max) {

    }
}
