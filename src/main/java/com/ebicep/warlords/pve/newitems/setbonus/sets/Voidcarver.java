package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Voidcarver extends BaseSet {

    private int radius;
    private int cdrBoost;

    @Override
    public void init() {
        super.init();
        this.radius = getValue("radius", int.class);
        this.cdrBoost = getValue("cdrBoost", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "voidcarver";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(radius, cdrBoost);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for the aura effect: 
            // Reducing cooldowns of allies within the specified radius.
        }

    }

}