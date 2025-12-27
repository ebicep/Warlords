package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Soulflame extends BaseSet {

    private boolean healAlliesOnDamage;
    private boolean selfHealthDegeneration;

    @Override
    public void init() {
        super.init();
        this.healAlliesOnDamage = getValue("healAlliesOnDamage", boolean.class);
        this.selfHealthDegeneration = getValue("selfHealthDegeneration", boolean.class);
    }

    @Override
    public String getConfigFieldName() {
        return "soulflame";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        // No numeric placeholders in current description, 
        // but variables are available for future logic.
        return List.of();
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. A damage-triggered heal (lifesteal-style but for allies).
            // 2. A repeating task or tick-based logic that reduces the 
            //    player's health at a fixed rate.
        }

    }

}