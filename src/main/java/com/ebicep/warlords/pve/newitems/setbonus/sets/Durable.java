package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Durable extends BaseSet {

    private int blueRuneDurationIncreaseSeconds;

    @Override
    public void init() {
        super.init();
        this.blueRuneDurationIncreaseSeconds = getValue("blueRuneDurationIncreaseSeconds", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "durable";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        // Your JSON description uses {{ticks}}, but the data is in seconds.
        // If the placeholder expects ticks, we multiply by 20.
        return List.of(blueRuneDurationIncreaseSeconds * 20);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Identifying the Blue Rune ability.
            // 2. Extending the 'tick' or 'second' duration variable of that 
            //    specific ability by blueRuneDurationIncreaseSeconds.
        }

    }

}