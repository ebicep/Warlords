package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.VolatileBrew;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class DetonationCatalyst implements SpecBoostManager.SpecBoost<DetonationCatalyst> {

    @Override
    public void init() {
    }

    @Override
    public String getConfigFieldName() {
        return "detonationCatalyst";
    }

    @Override
    public List<Object> getVariables() {
        return List.of();
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public DetonationCatalyst get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(VolatileBrew.class).forEach(volatileBrew -> {
                volatileBrew.setBothStatesActive(true);
            });
        }

    }

}
