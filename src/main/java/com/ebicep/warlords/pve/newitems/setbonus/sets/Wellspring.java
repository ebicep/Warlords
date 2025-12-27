package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Wellspring extends BaseSet {

    private int blueRuneCooldownReductionPercent;

    @Override
    public void init() {
        super.init();
        this.blueRuneCooldownReductionPercent = getValue("blueRuneCooldownReductionPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "wellspring";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        // Matches the {{blue;%}} placeholder in your description
        return List.of(blueRuneCooldownReductionPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Locating the ability assigned to the Blue Rune slot (usually Index 1 or 2).
            // 2. Applying a 5% cooldown reduction multiplier to that specific ability.
        }

    }

}