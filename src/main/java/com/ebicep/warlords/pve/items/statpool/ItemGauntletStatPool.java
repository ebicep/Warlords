package com.ebicep.warlords.pve.items.statpool;

import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.ItemTier;

import java.util.HashMap;

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

        @Override
        public DecimalPlace getDecimalPlace() {
            return DecimalPlace.TENTHS;
        }
    },

    ;

    public static final ItemGauntletStatPool[] VALUES = values();
    public static final HashMap<ItemGauntletStatPool, ItemTier.StatRange> STAT_RANGES = new HashMap<>() {{
        put(ItemGauntletStatPool.HP, new ItemTier.StatRange(75, 450));
        put(ItemGauntletStatPool.MAX_ENERGY, new ItemTier.StatRange(10, 30));
        put(ItemGauntletStatPool.EPH, new ItemTier.StatRange(2, 10));
        put(ItemGauntletStatPool.SPEED, new ItemTier.StatRange(2, 10));
    }};
    public final String name;

    ItemGauntletStatPool(String name) {
        this.name = name;
    }

    @Override
    public ItemGauntletStatPool[] getPool() {
        return VALUES;
    }

    @Override
    public HashMap<ItemGauntletStatPool, ItemTier.StatRange> getStatRange() {
        return STAT_RANGES;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Operation getOperation() {
        return Operation.ADD;
    }

    @Override
    public DecimalPlace getDecimalPlace() {
        return DecimalPlace.ONES;
    }

}
