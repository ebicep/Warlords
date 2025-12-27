package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Battery extends BaseSet {

    private int redRuneAbilityEnergyCostReduction;

    @Override
    public void init() {
        super.init();
        this.redRuneAbilityEnergyCostReduction = getValue("redRuneAbilityEnergyCostReduction", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "battery";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        // Matches the {{energy}} placeholder in your description
        return List.of(redRuneAbilityEnergyCostReduction);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Accessing the Red Rune ability (Slot 0).
            // 2. Subtracting the flat value of redRuneAbilityEnergyCostReduction 
            //    from the ability's energy cost.
        }

    }

}