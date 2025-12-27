package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Oathkeeper extends BaseSet {

    private int meleeDamageIncreasePercent;
    private int meleeAttackTwiceChancePercent;

    @Override
    public void init() {
        super.init();
        this.meleeDamageIncreasePercent = getValue("meleeDamageIncreasePercent", int.class);
        this.meleeAttackTwiceChancePercent = getValue("meleeAttackTwiceChancePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "oathkeeper";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(meleeDamageIncreasePercent, meleeAttackTwiceChancePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {

        }

    }

}
