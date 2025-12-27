package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Transference extends BaseSet {

    private int healingRedirectPercent;

    @Override
    public void init() {
        super.init();
        this.healingRedirectPercent = getValue("healingRedirectPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "transference";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healingRedirectPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for intercepting incoming healing on the player.
            // Redirect healingRedirectPercent of that value to the ally 
            // with the lowest current health percentage.
        }

    }

}