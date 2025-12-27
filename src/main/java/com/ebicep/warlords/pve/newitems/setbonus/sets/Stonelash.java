package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Stonelash extends BaseSet {

    private boolean knockbackImmune;
    private int energyGainPenaltyPercent;

    @Override
    public void init() {
        super.init();
        this.knockbackImmune = getValue("knockbackImmune", boolean.class);
        this.energyGainPenaltyPercent = getValue("energyGainPenaltyPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "stonelash";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(energyGainPenaltyPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Setting the player's knockback resistance attribute to 100% 
            //    (or modifying the velocity handler) if knockbackImmune is true.
            // 2. Applying a 0.5x multiplier to all sources of energy gain 
            //    (EPS, EPH, and ability-based restoration).
        }

    }

}