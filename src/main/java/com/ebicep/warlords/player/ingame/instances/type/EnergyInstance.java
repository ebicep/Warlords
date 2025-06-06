package com.ebicep.warlords.player.ingame.instances.type;

import com.ebicep.warlords.player.ingame.WarlordsEntity;

public interface EnergyInstance extends Instance {

    /**
     * Called every tick, before multiplyEnergyGainPerTick
     */
    default float addEnergyGainPerTick(float energyGainPerTick) {
        return energyGainPerTick;
    }

    /**
     * Called every tick, after addEnergyGainPerTick
     */
    default float multiplyEnergyGainPerTick(float energyGainPerTick) {
        return energyGainPerTick;
    }

    default float addEnergyPerHit(WarlordsEntity we, float energyPerHit) {
        return energyPerHit;
    }

}
