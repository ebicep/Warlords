package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class ShieldGate extends BaseSet {

    private int shieldEffectivenessIncreasePercent;
    private int maxHealthReductionPercent;

    @Override
    public void init() {
        super.init();
        this.shieldEffectivenessIncreasePercent = getValue("shieldEffectivenessIncreasePercent", int.class);
        this.maxHealthReductionPercent = getValue("maxHealthReductionPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "shieldGate";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(shieldEffectivenessIncreasePercent, maxHealthReductionPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Reducing the player's Max Health by 99% (forcing reliance on shields).
            // 2. Applying a 3.0x (300%) multiplier to all incoming/outgoing shield amounts.
        }

    }

}