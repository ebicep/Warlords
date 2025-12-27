package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class InnerFlame extends BaseSet {

    private int healingDamageConversion;

    @Override
    public void init() {
        super.init();
        this.healingDamageConversion = getValue("healingDamageConversion", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "innerFlame";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healingDamageConversion);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for converting healing done into AoE damage 
            // for nearby enemies based on healingDamageConversion percentage.
        }

    }

}