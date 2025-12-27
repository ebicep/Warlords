package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Detonator extends BaseSet {

    private int redRuneAbilityDamageIncreasePercent;

    @Override
    public void init() {
        super.init();
        this.redRuneAbilityDamageIncreasePercent = getValue("redRuneAbilityDamageIncreasePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "detonator";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(redRuneAbilityDamageIncreasePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Locating the ability assigned to the Red Rune slot.
            // 2. Applying a flat 5% damage increase to that specific ability's 
            //    min/max damage values.
        }

    }

}