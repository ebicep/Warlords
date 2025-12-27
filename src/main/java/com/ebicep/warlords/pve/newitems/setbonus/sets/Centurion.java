package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Centurion extends BaseSet {

    private int bonusCDR;

    @Override
    public void init() {
        super.init();
        this.bonusCDR = getValue("bonusCDR", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "centurion";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(bonusCDR);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for applying additional cooldown 
            // reduction across all of the player's abilities.
        }

    }

}