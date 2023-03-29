package com.ebicep.warlords.pve.items.statpool;

import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

public enum ItemGauntletStatPool implements ItemStatPool<ItemGauntletStatPool> {

    HP("Health") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, int value) {
            warlordsPlayer.setMaxBaseHealth(warlordsPlayer.getMaxBaseHealth() + value);
        }
    },
    MAX_ENERGY("Max Energy") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, int value) {
            AbstractPlayerClass playerClass = warlordsPlayer.getSpec();
            playerClass.setMaxEnergy(playerClass.getMaxEnergy() + value);
        }
    },
    EPH("Energy Per Hit") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, int value) {
            AbstractPlayerClass playerClass = warlordsPlayer.getSpec();
            playerClass.setEnergyOnHit(playerClass.getEnergyOnHit() + value);
        }
    },
    SPEED("Speed") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, int value) {
            warlordsPlayer.getSpeed().addBaseModifier(value);
        }

        @Override
        public Operation getOperation() {
            return Operation.MULTIPLY;
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Operation getOperation() {
        return Operation.ADD;
    }

}
