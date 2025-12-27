package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class IlluminatedPrism extends BaseSet {

    private int repeatedAttackDamageReduction;
    private int repeatedAttackMaxDamageReduction;

    @Override
    public void init() {
        super.init();
        this.repeatedAttackDamageReduction = getValue("repeatedAttackDamageReduction", int.class);
        this.repeatedAttackMaxDamageReduction = getValue("repeatedAttackMaxDamageReduction", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "illuminatedPrism";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(repeatedAttackDamageReduction, repeatedAttackMaxDamageReduction);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {

        }

    }

}
