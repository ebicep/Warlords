package com.ebicep.warlords.pve.items.statpool;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.custom.ItemAdditiveCooldown;

public enum ItemTomeStatPool implements ItemStatPool<ItemTomeStatPool> {

    DAMAGE("Damage") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, int value) {
            ItemAdditiveCooldown.increaseDamage(warlordsPlayer, value);
        }
    },
    HEALING("Healing") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, int value) {
            ItemAdditiveCooldown.increaseHealing(warlordsPlayer, value);
        }
    },
    CRIT_CHANCE("Crit Chance") {
        @Override
        public void applyToAbility(AbstractAbility ability, int value) {
            float calculatedValue = 1 + value / 100f;
            ability.setCritChance(ability.getCritChance() * calculatedValue);
        }
    },
    CRIT_MULTI("Crit Multiplier") {
        @Override
        public void applyToAbility(AbstractAbility ability, int value) {
            float calculatedValue = 1 + value / 100f;
            ability.setCritMultiplier(ability.getCritMultiplier() * calculatedValue);
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


}
