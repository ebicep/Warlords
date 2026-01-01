package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Bellicose extends BaseSet {

    private int maxHealthRequirement;
    private int meleeDamageIncrease;

    @Override
    public void init() {
        super.init();
        this.maxHealthRequirement = getValue("maxHealthRequirement", int.class);
        this.meleeDamageIncrease = getValue("meleeDamageIncrease", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "bellicose";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(maxHealthRequirement, meleeDamageIncrease);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {

        }

    }

}
