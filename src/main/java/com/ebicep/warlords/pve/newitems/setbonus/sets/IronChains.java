package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class IronChains extends BaseSet {

    private int damageReductionIncreasePercent;
    private int movementSpeedPenaltyPercent;

    @Override
    public void init() {
        super.init();
        this.damageReductionIncreasePercent = getValue("damageReductionIncreasePercent", int.class);
        this.movementSpeedPenaltyPercent = getValue("movementSpeedPenaltyPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "ironChains";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(damageReductionIncreasePercent, movementSpeedPenaltyPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Increasing the player's damage reduction attribute.
            // 2. Applying a permanent movement speed debuff (slowness) 
            //    calculated from movementSpeedPenaltyPercent.
        }

    }

}