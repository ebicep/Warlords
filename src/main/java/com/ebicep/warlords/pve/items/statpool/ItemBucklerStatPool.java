package com.ebicep.warlords.pve.items.statpool;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.custom.ItemAdditiveCooldown;

public enum ItemBucklerStatPool implements ItemStatPool<ItemBucklerStatPool> {

    AGGRO_PRIO("Aggression Priority") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, int value) {
            warlordsPlayer.setBonusAgroWeight(warlordsPlayer.getBonusAgroWeight() + value);
        }
    },
    THORNS("Thorns") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, int value) {
            // TODO
        }
    },
    KB_RES("Knockback Resistance") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, int value) {
            ItemAdditiveCooldown.increaseKBRes(warlordsPlayer, value);
        }
    },
    REGEN_TIMER("Shorter Regen Timer") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, int value) {
            warlordsPlayer.setRegenTickTimerModifier((1 - value) / 100f);
        }
    },
    ;

    public static final ItemBucklerStatPool[] VALUES = values();
    public final String name;

    ItemBucklerStatPool(String name) {
        this.name = name;
    }

    @Override
    public ItemBucklerStatPool[] getPool() {
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
