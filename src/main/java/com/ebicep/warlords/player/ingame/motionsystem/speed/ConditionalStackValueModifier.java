package com.ebicep.warlords.player.ingame.motionsystem.speed;

import com.ebicep.warlords.player.ingame.motionsystem.MotionSystem;
import com.ebicep.warlords.player.ingame.motionsystem.motionaddon.NewValueModifier;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class ConditionalStackValueModifier implements NewValueModifier {

    private final float multiplier;

    public ConditionalStackValueModifier(float multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE - 50;
    }

    @Override
    public boolean forceApply() {
        return true;
    }

    @Override
    public void modifyNewValue(MotionSystem.NewValueData newValueData) {
        if (newValueData.min() != multiplier) {
            newValueData.newValue().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, addonName(), multiplier);
        }
    }

    @Override
    public String addonName() {
        return "Conditional Stack Value Modifier";
    }

}
