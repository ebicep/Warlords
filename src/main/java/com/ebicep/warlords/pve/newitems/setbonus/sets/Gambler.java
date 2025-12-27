package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Gambler extends BaseSet {

    private int randomEffectIntervalSeconds;
    private List<String> randomEffects;

    @Override
    public void init() {
        super.init();
        this.randomEffectIntervalSeconds = getValue("randomEffectIntervalSeconds", int.class);
        this.randomEffects = getListValue("randomEffects", String.class);
    }

    @Override
    public String getConfigFieldName() {
        return "gambler";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(randomEffectIntervalSeconds);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Starting a repeating task every randomEffectIntervalSeconds (ticks = seconds * 20).
            // 2. Randomly selecting an index from the randomEffects list.
            // 3. Applying the logic for the specific string effect (e.g., if "TURN_INTO_FROG_15S"...).
        }

    }

}