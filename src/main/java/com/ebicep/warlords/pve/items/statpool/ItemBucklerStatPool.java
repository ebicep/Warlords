package com.ebicep.warlords.pve.items.statpool;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;

public enum ItemBucklerStatPool implements ItemStatPool<ItemBucklerStatPool> {

    DAMAGE_RED("Damage Reduction") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value) {
            warlordsPlayer.getSpec().setDamageResistance((int) (warlordsPlayer.getSpec().getDamageResistance() + value));
        }
    },
    AGGRO_PRIO("Aggression Priority") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value) {
            // TODO
        }
    },
    THORNS("Thorns") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value) {
            // TODO
        }
    },
    KB_RES("Knockback Resistance") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value) {
            // TODO
        }
    },
    RES_SPEED("Respawn Speed") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value) {
            // TODO
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

    public abstract void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value);
}
