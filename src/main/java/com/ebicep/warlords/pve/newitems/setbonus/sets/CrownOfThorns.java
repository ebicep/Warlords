package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class CrownOfThorns extends BaseSet {

    private int thornDamageBoost;

    @Override
    public void init() {
        super.init();
        this.thornDamageBoost = getValue("thornDamageBoost", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "crownOfThorns";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(thornDamageBoost);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for doubling the thorn damage cap 
            // and applying the percentage boost to thorn damage.
        }

    }

}