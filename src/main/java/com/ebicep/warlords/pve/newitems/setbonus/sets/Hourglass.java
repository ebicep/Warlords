package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Hourglass extends BaseSet {

    private int cooldownReductionPercent;
    private int freezeIntervalSeconds;
    private int freezeDurationSeconds;

    @Override
    public void init() {
        super.init();
        this.cooldownReductionPercent = getValue("cooldownReductionPercent", int.class);
        this.freezeIntervalSeconds = getValue("freezeIntervalSeconds", int.class);
        this.freezeDurationSeconds = getValue("freezeDurationSeconds", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "hourglass";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(cooldownReductionPercent, freezeIntervalSeconds, freezeDurationSeconds);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Applying a global 25% Cooldown Reduction (CDR) modifier.
            // 2. Starting a repeating task that triggers every freezeIntervalSeconds (300 ticks).
            // 3. Applying a "Freeze" effect (no movement/abilities) to the player 
            //    for freezeDurationSeconds (60 ticks).
        }

    }

}