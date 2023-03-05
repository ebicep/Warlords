package com.ebicep.warlords.pve.items.statpool;

import com.ebicep.warlords.abilties.internal.AbstractAbility;

public enum ItemTomeStatPool implements ItemStatPool<ItemTomeStatPool> {

    DAMAGE("Damage") {
        @Override
        public void applyToAbility(AbstractAbility ability, float value) {
        }
    },
    HEALING("Healing") {
        @Override
        public void applyToAbility(AbstractAbility ability, float value) {
        }
    },
    CRIT_CHANCE("Crit Chance") {
        @Override
        public void applyToAbility(AbstractAbility ability, float value) {
            float calculatedValue = 1 + value / 100;
            ability.setCritChance(ability.getCritChance() * calculatedValue);
        }
    },
    CRIT_MULTI("Crit Multiplier") {
        @Override
        public void applyToAbility(AbstractAbility ability, float value) {
            float calculatedValue = 1 + value / 100;
            ability.setCritMultiplier(ability.getCritMultiplier() * calculatedValue);
        }
    },
    CD_RED("Cooldown Reduction") {
        @Override
        public void applyToAbility(AbstractAbility ability, float value) {
            float calculatedValue = 1 + (100 - value) / 100;
            ability.setCooldown(ability.getCooldown() * calculatedValue);
        }
    },

    ;

    public static final ItemTomeStatPool[] VALUES = values();
    public final String name;

    ItemTomeStatPool(String name) {
        this.name = name;
    }

    @Override
    public ItemTomeStatPool[] getPool() {
        return VALUES;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Operation getOperation() {
        return Operation.MULTIPLY;
    }

    public abstract void applyToAbility(AbstractAbility ability, float value);


}
