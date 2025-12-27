package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Gravemind extends BaseSet {

    private int summonChance;
    private int duration;

    @Override
    public void init() {
        super.init();
        this.summonChance = getValue("summonChance", int.class);
        this.duration = getValue("duration", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "gravemind";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(summonChance, duration);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for on-kill logic to trigger the 
            // summoning of allied mobs based on summonChance.
        }

    }

}