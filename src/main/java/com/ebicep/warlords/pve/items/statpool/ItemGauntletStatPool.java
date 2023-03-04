package com.ebicep.warlords.pve.items.statpool;

import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

public enum ItemGauntletStatPool implements ItemStatPool<ItemGauntletStatPool> {

    HP("Health") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value) {
            warlordsPlayer.setMaxBaseHealth(warlordsPlayer.getMaxBaseHealth() + value);
        }
    },
    MAX_ENERGY("Max Energy") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value) {
            AbstractPlayerClass playerClass = warlordsPlayer.getSpec();
            playerClass.setMaxEnergy((int) (playerClass.getMaxEnergy() + value));
        }
    },
    EPH("Energy Per Hit") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value) {
            AbstractPlayerClass playerClass = warlordsPlayer.getSpec();
            playerClass.setEnergyOnHit(playerClass.getEnergyOnHit() + value);
        }
    },
    EPS("Energy Per Second") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value) {
            AbstractPlayerClass playerClass = warlordsPlayer.getSpec();
            playerClass.setEnergyPerSec(playerClass.getEnergyPerSec() + value);
        }
    },
    SPEED("Speed") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value) {
            warlordsPlayer.getSpeed().addBaseModifier(value);
        }
    },

    ;

    public static final ItemGauntletStatPool[] VALUES = values();
    public final String name;

    ItemGauntletStatPool(String name) {
        this.name = name;
    }

    @Override
    public ItemGauntletStatPool[] getPool() {
        return VALUES;
    }

    public abstract void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value);
}
