package com.ebicep.warlords.player.ingame.motionsystem.speed;

import com.ebicep.warlords.player.ingame.motionsystem.MotionSystem;
import com.ebicep.warlords.player.ingame.motionsystem.motionaddon.NewValueModifier;

public class MaxSpeedReductionValueModifier implements NewValueModifier {

    private float maxSpeedReduction = 0.5f;

    @Override
    public String addonName() {
        return "Max Speed Reduction";
    }

    @Override
    public int getPriority() {
        return -10;
    }

    @Override
    public void modifyNewValue(MotionSystem.NewValueData newValueData) {
        float min = newValueData.min();
        float max = newValueData.max();
        float calculated = max * min;
        if (max - calculated > maxSpeedReduction) {
            newValueData.newValue().setBaseValue(max - maxSpeedReduction);
        }
    }


}
