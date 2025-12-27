package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Soulforged extends BaseSet {

    private int healthThreshold;
    private int energyPerSecondBonus;
    private int energyRegenDisabledBelowHealthPercent;

    @Override
    public void init() {
        super.init();
        this.healthThreshold = getValue("healthThreshold", int.class);
        this.energyPerSecondBonus = getValue("energyPerSecondBonus", int.class);
        this.energyRegenDisabledBelowHealthPercent = getValue("energyRegenDisabledBelowHealthPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "soulforged";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healthThreshold, energyRegenDisabledBelowHealthPercent, energyPerSecondBonus);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Monitoring the player's health percentage.
            // 2. If above threshold, add energyPerSecondBonus to base EPS.
            // 3. If below threshold, set base energy regeneration to 0.
        }

    }

}