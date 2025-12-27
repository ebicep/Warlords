package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Energize extends BaseSet {

    private int energyPerKillThreshold;
    private int energyGained;
    private int freeAbilityCastChancePercent;

    @Override
    public void init() {
        super.init();
        this.energyPerKillThreshold = getValue("energyPerKillThreshold", int.class);
        this.energyGained = getValue("energyGained", int.class);
        this.freeAbilityCastChancePercent = getValue("freeAbilityCastChancePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "energize";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(energyPerKillThreshold, energyGained, freeAbilityCastChancePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Tracking kills and granting energyGained every energyPerKillThreshold kills.
            // 2. Rolling a chance (freeAbilityCastChancePercent) to bypass energy costs on cast.
        }

    }

}