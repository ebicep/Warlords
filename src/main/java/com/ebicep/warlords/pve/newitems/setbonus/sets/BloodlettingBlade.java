package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class BloodlettingBlade extends BaseSet {

    private int critMultiplierIncreasePercent;
    private int selfDamageOnCritPercentMaxHealth;

    @Override
    public void init() {
        super.init();
        this.critMultiplierIncreasePercent = getValue("critMultiplierIncreasePercent", int.class);
        this.selfDamageOnCritPercentMaxHealth = getValue("selfDamageOnCritPercentMaxHealth", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "bloodlettingBlade";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(critMultiplierIncreasePercent, selfDamageOnCritPercentMaxHealth);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Adding 100% to the player's base Critical Multiplier.
            // 2. Listening for outgoing damage events that result in a Critical Hit.
            // 3. Applying 5% of the player's Max Health as true damage back to themselves per crit.
        }

    }

}