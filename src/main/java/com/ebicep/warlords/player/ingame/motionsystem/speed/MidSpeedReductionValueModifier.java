package com.ebicep.warlords.player.ingame.motionsystem.speed;

import com.ebicep.warlords.player.ingame.motionsystem.MotionSystem;
import com.ebicep.warlords.player.ingame.motionsystem.motionaddon.NewValueModifier;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class MidSpeedReductionValueModifier implements NewValueModifier {

    private float speedThreshold = 1.4f;
    private float speedReduction = 0.05f;

    @Override
    public void modifyNewValue(MotionSystem.NewValueData newValueData) {
        float min = newValueData.min();
        FloatModifiable newValue = newValueData.newValue();
        if (min != 1 && newValue.getCalculatedValue() < speedThreshold) {
            newValue.addModifier(FloatModifiable.ModifierType.ADDITIVE, addonName(), -speedReduction); // TODO
        }
    }

    @Override
    public String addonName() {
        return "Mid Speed Threshold Reduction";
    }


}
