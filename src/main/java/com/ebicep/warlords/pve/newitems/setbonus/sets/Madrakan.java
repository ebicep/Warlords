package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Madrakan extends BaseSet {

    private int damageIncreasePercent;
    private boolean disableCriticalHits;

    @Override
    public void init() {
        super.init();
        this.damageIncreasePercent = getValue("damageIncreasePercent", int.class);
        this.disableCriticalHits = getValue("disableCriticalHits", boolean.class);
    }

    @Override
    public String getConfigFieldName() {
        return "madrakan";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(damageIncreasePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Increasing the player's base damage by damageIncreasePercent.
            // 2. Setting the player's Crit Chance or Crit Multiplier to 0
            //    (or intercepting damage events to cancel crits) if disableCriticalHits is true.
        }

    }

}