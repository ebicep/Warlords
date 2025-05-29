package com.ebicep.warlords.player.ingame.motionsystem.speed;

import com.ebicep.warlords.player.ingame.motionsystem.MotionSystem;
import com.ebicep.warlords.player.ingame.motionsystem.motionaddon.NewValueModifier;

public class DebuffImmunityValueModifier implements NewValueModifier {

    @Override
    public int getPriority() {
        return 1000;
    }

    @Override
    public boolean forceApply() {
        return true;
    }

    @Override
    public void modifyNewValue(MotionSystem.NewValueData newValueData) {
        newValueData.newValue().setBaseValue(newValueData.max());
    }

    @Override
    public String addonName() {
        return "Debuff Immunity";
    }

}
