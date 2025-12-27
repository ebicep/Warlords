package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class BrittleCrown extends BaseSet {

    private int insigniaGainBonusPercent;
    private int insigniaLossOnHitPercent;

    @Override
    public void init() {
        super.init();
        this.insigniaGainBonusPercent = getValue("insigniaGainBonusPercent", int.class);
        this.insigniaLossOnHitPercent = getValue("insigniaLossOnHitPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "brittleCrown";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(insigniaGainBonusPercent, insigniaLossOnHitPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Modifying the insignia multiplier for every kill made by the player.
            // 2. Registering a listener for when the player takes damage.
            // 3. Calculating 5% of current total insignia and removing it on hit.
        }

    }

}