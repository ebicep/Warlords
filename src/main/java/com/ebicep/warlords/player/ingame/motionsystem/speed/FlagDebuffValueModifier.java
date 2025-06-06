package com.ebicep.warlords.player.ingame.motionsystem.speed;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.ingame.motionsystem.MotionSystem;
import com.ebicep.warlords.player.ingame.motionsystem.motionaddon.NewValueModifier;

public class FlagDebuffValueModifier implements NewValueModifier {

    private final float flagSpeedModifier;

    public FlagDebuffValueModifier() {
        this.flagSpeedModifier = AbstractAbility.convertToDivisionDecimal(-ConfigManager.getGameConfigValue(ConfigManager.DEFAULT_NAMESPACES,
                "ctf.flagSpeedModifier",
                int.class
        ));
    }

    @Override
    public int getPriority() {
        return -Integer.MAX_VALUE;
    }

    @Override
    public boolean forceApply() {
        return true;
    }

    @Override
    public void modifyNewValue(MotionSystem.NewValueData newValueData) {
        float newMax = (newValueData.max() - .1f) * flagSpeedModifier;
        newValueData.setMax(newMax);
        newValueData.newValue().setBaseValue(newMax * newValueData.getMin());
    }

    @Override
    public String addonName() {
        return "Flag Debuff";
    }

}
