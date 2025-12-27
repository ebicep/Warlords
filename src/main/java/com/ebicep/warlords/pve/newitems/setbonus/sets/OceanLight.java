package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class OceanLight extends BaseSet {

    private float healingPerMobPercent;

    @Override
    public void init() {
        super.init();
        this.healingPerMobPercent = getValue("healingPerMobPercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "oceanLight";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healingPerMobPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation to count active mobs on the field 
            // and apply a healing multiplier based on healingPerMobPercent.
        }

    }

}