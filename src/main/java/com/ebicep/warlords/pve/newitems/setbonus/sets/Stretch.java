package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Stretch extends BaseSet {

    private int redRuneAbilityRangeIncreasePercent;

    @Override
    public void init() {
        super.init();
        this.redRuneAbilityRangeIncreasePercent = getValue("redRuneAbilityRangeIncreasePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "stretch";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(redRuneAbilityRangeIncreasePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Identifying the ability bound to the Red Rune slot (usually the first skill).
            // 2. Applying a 1.15x multiplier to its range or hit-box radius.
        }

    }

}