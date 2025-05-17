package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class BurstChain implements SpecBoostManager.SpecBoost {

    private float velocityIncrease;
    private float consecutiveHitDamageIncrease;

    @Override
    public void init() {
        this.velocityIncrease = getValue("velocityIncrease", float.class);
        this.consecutiveHitDamageIncrease = getValue("consecutiveHitDamageIncrease", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "burstChain";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(velocityIncrease, consecutiveHitDamageIncrease);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {

        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {

        }

    }

}