package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Omamori extends BaseSet {

    private int ultimateDurationIncreaseSeconds;

    @Override
    public void init() {
        super.init();
        this.ultimateDurationIncreaseSeconds = getValue("ultimateDurationIncreaseSeconds", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "omamori";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(ultimateDurationIncreaseSeconds);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for increasing the duration of the player's 
            // ultimate ability by the specified seconds.
        }

    }

}