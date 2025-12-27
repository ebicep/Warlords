package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class MournsongVial extends BaseSet {

    private int allyEnergyPerSecondBonusPercent;
    private int selfDamageTakenIncreasePercent;

    @Override
    public void init() {
        super.init();
        this.allyEnergyPerSecondBonusPercent = getValue("allyEnergyPerSecondBonusPercent", int.class);
        this.selfDamageTakenIncreasePercent = getValue("selfDamageTakenIncreasePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "mournsongVial";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(allyEnergyPerSecondBonusPercent, selfDamageTakenIncreasePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Increasing the 'damage resistance' attribute of the wearer 
            //    by -60% (effectively increasing damage taken).
            // 2. Iterating through all players on the same team (excluding self).
            // 3. Applying a 30% multiplier to their Energy Per Second (EPS).
        }

    }

}