package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Sanguineous extends BaseSet {

    private int damagePerHealthyPlayerPercent;
    private int healthyPlayerThresholdPercent;

    @Override
    public void init() {
        super.init();
        this.damagePerHealthyPlayerPercent = getValue("damagePerHealthyPlayerPercent", int.class);
        this.healthyPlayerThresholdPercent = getValue("healthyPlayerThresholdPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "sanguineous";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(damagePerHealthyPlayerPercent, healthyPlayerThresholdPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation to check for players above healthyPlayerThresholdPercent
            // and apply a damage multiplier based on damagePerHealthyPlayerPercent.
        }

    }

}