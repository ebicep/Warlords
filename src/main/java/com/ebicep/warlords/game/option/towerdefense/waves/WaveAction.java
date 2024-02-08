package com.ebicep.warlords.game.option.towerdefense.waves;

import com.ebicep.warlords.game.option.pve.PveOption;

public interface WaveAction<T extends PveOption> {

    /**
     * @param pveOption the pve option to use if needed
     * @return true if the action is complete
     */
    boolean run(T pveOption);

}
