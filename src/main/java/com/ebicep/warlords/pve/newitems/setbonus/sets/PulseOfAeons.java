package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class PulseOfAeons extends BaseSet {

    private int healsPerPulse;
    private int pulseHealAmount;

    @Override
    public void init() {
        super.init();
        this.healsPerPulse = getValue("healsPerPulse", int.class);
        this.pulseHealAmount = getValue("pulseHealAmount", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "pulseOfAeons";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healsPerPulse, pulseHealAmount);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Listening for healing instances triggered by the player.
            // 2. Tracking a counter until it reaches healsPerPulse.
            // 3. Triggering a global heal of pulseHealAmount to all allies.
        }

    }

}