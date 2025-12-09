package com.ebicep.warlords.player.ingame.motionsystem.speed;

import com.ebicep.warlords.player.ingame.motionsystem.MotionSystem;
import com.ebicep.warlords.player.ingame.motionsystem.motionaddon.NewValueModifier;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class BaseToWalkingSpeedValueModifier implements NewValueModifier {

    public static final float BASE_PLAYER_WALK_SPEED = 0.25f;// 0.2825 with 13%
    private static final float BASE_SPEED = 7.02f;
    private float baseWalkSpeed;

    public BaseToWalkingSpeedValueModifier(float baseWalkSpeed) {
        this.baseWalkSpeed = baseWalkSpeed;
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean forceApply() {
        return true;
    }

    @Override
    public void modifyNewValue(MotionSystem.NewValueData newValueData) {
//        floatModifiable.addModifier(addonName() + " - Base Speed", BASE_SPEED);
        newValueData.newValue().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, addonName() + " - Base Walk Speed", baseWalkSpeed);
    }

    @Override
    public String addonName() {
        return "Base Speed to Walking Speed";
    }

    public float getBaseWalkSpeed() {
        return baseWalkSpeed;
    }

    public void setBaseWalkSpeed(float baseWalkSpeed) {
        this.baseWalkSpeed = baseWalkSpeed;
    }

}
