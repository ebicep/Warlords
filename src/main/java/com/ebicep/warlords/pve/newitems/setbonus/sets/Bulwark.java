package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Bulwark extends BaseSet {

    private int radius;
    private int damageReduction;
    private int healthThreshold;

    @Override
    public void init() {
        super.init();
        this.radius = getValue("radius", int.class);
        this.damageReduction = getValue("damageReduction", int.class);
        this.healthThreshold = getValue("healthThreshold", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "bulwark";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(radius, damageReduction, healthThreshold);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for a protective aura that reduces damage for nearby allies.
            // Logic should include a check to double the effect if the user's health 
            // is below healthThreshold.
        }

    }

}