package com.ebicep.warlords.player.ingame.motionsystem.speed;

import com.ebicep.warlords.player.ingame.motionsystem.MotionSystem;
import com.ebicep.warlords.player.ingame.motionsystem.motionaddon.NewValueModifier;

public class OverrideValueModifier implements NewValueModifier {

    private final float newSpeedValue;

    public OverrideValueModifier(float newSpeedValue) {
        this.newSpeedValue = newSpeedValue;
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE - 10;
    }

    @Override
    public boolean forceApply() {
        return true;
    }

    @Override
    public void modifyNewValue(MotionSystem.NewValueData newValueData) {
        newValueData.newValue().setBaseValue(newSpeedValue);
    }

    @Override
    public String addonName() {
        return "Override Value Modifier";
    }

}
