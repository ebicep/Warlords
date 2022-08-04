package com.ebicep.warlords.player.ingame.cooldowns.instances;

public interface EnergyInstance extends Instance {

    default float addEnergyGainPerTick(float energyGainPerTick) {
        return energyGainPerTick;
    }

    default float multiplyEnergyGainPerTick(float energyGainPerTick) {
        return energyGainPerTick;
    }

}
