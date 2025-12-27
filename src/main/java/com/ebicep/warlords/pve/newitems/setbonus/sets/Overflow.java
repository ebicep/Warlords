package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Overflow extends BaseSet {

    private int excessHealingEnergyCap;
    private int excessHealingHealthThresholdPercent;

    @Override
    public void init() {
        super.init();
        this.excessHealingEnergyCap = getValue("excessHealingEnergyCap", int.class);
        this.excessHealingHealthThresholdPercent = getValue("excessHealingHealthThresholdPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "overflow";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(excessHealingEnergyCap, excessHealingHealthThresholdPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for intercepting healing events.
            // If target health > excessHealingHealthThresholdPercent,
            // convert the "overflow" amount into energy, up to excessHealingEnergyCap.
        }

    }

}