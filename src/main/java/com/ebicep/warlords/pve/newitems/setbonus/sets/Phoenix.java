package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Phoenix extends BaseSet {

    private float damagePerMobPercent;

    @Override
    public void init() {
        super.init();
        this.damagePerMobPercent = getValue("damagePerMobPercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "phoenix";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(damagePerMobPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation to count active mobs on the field 
            // and apply a damage multiplier based on damagePerMobPercent.
        }

    }

}