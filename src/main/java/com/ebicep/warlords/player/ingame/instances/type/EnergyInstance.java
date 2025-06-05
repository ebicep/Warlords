package com.ebicep.warlords.player.ingame.instances.type;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface EnergyInstance extends Instance {

    @Nullable
    default List<EnergyInstance> getExtraEnergyInstances() {
        return null;
    }

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
