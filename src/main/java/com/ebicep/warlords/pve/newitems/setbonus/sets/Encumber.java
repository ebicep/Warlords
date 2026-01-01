package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Encumber extends BaseSet {

    private int extraCharges;

    @Override
    public void init() {
        super.init();
        this.extraCharges = getValue("extraCharges", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "encumber";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(extraCharges);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {

        }

    }

}
