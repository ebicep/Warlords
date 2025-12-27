package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Vial extends BaseSet {

    private int redRuneAbilityHealingIncreasePercent;

    @Override
    public void init() {
        super.init();
        this.redRuneAbilityHealingIncreasePercent = getValue("redRuneAbilityHealingIncreasePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "vial";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(redRuneAbilityHealingIncreasePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Finding the ability in the Red Rune slot (Index 0).
            // 2. Modifying the healing min/max values or adding a 
            //    multiplier to the healing output of that skill.
        }

    }

}