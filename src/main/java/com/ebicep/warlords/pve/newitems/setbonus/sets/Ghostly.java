package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Ghostly extends BaseSet {

    private int dodgeChance;

    @Override
    public void init() {
        super.init();
        this.dodgeChance = getValue("dodgeChance", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "ghostly";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(dodgeChance);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Roll for dodgeChance when taking damage.
            // 2. On successful dodge, apply/increment stacking damage and healing buffs.
        }

    }

}