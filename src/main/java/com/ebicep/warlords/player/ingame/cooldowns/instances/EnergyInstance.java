package com.ebicep.warlords.player.ingame.cooldowns.instances;

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

}
