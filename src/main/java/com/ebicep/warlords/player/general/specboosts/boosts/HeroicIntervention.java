package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.Intervene;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class HeroicIntervention implements SpecBoostManager.SpecBoost<HeroicIntervention> {

    private int interveneCapIncrease;
    private float interveneCastAndRangeIncrease;

    @Override
    public void init() {
        this.interveneCapIncrease = getValue("interveneCapIncrease", int.class);
        this.interveneCastAndRangeIncrease = getValue("interveneCastAndRangeIncrease", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "heroicIntervention";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(interveneCapIncrease, interveneCastAndRangeIncrease);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public HeroicIntervention get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(Intervene.class).forEach(intervene -> {
                intervene.setMaxDamagePrevented(intervene.getMaxDamagePrevented() + interveneCapIncrease);
                intervene.setRadius(intervene.getRadius() + interveneCastAndRangeIncrease);
                intervene.setBreakRadius(intervene.getBreakRadius() + interveneCastAndRangeIncrease);
            });
        }

    }

}
