package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Moonveil extends BaseSet {

    private int critChanceBoost;

    @Override
    public void init() {
        super.init();
        this.critChanceBoost = getValue("critChanceBoost", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "moonveil";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(critChanceBoost);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for increasing crit chance while 
            // the player has active damage-preventing buffs.
        }

    }

}