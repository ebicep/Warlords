package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.VolatileBrew;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class DetonationCatalyst implements SpecBoostManager.SpecBoost<DetonationCatalyst> {

    private float volatileBrewCooldownReductionSeconds;
    private int volatileBrewExtraDurationTicks;

    @Override
    public void init() {
        this.volatileBrewCooldownReductionSeconds = getValue("volatileBrewCooldownReductionSeconds", float.class);
        this.volatileBrewExtraDurationTicks = getValue("volatileBrewExtraDurationTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "detonationCatalyst";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(volatileBrewCooldownReductionSeconds, volatileBrewExtraDurationTicks);
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
                volatileBrew.getCooldown().addAdditiveModifier("Spec Boost", -volatileBrewCooldownReductionSeconds);
                volatileBrew.setTickDuration(volatileBrew.getTickDuration() + volatileBrewExtraDurationTicks);
                volatileBrew.setBothStatesActive(true);
            });
        }

    }

}
