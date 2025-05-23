package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.Intervene;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class HeroicIntervention implements SpecBoostManager.SpecBoost<HeroicIntervention> {

    private int interveneCapIncrease;
    private int interveneDurationIncreaseTicks;
    private float interveneCastAndRangeIncrease;

    @Override
    public void init() {
        this.interveneCapIncrease = getValue("interveneCapIncrease", int.class);
        this.interveneDurationIncreaseTicks = getValue("interveneDurationIncreaseTicks", int.class);
        this.interveneCastAndRangeIncrease = getValue("interveneCastAndRangeIncrease", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "heroicIntervention";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(interveneCapIncrease, interveneDurationIncreaseTicks, interveneCastAndRangeIncrease);
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
                intervene.setTickDuration(intervene.getTickDuration() + interveneDurationIncreaseTicks);
                intervene.setRadius(intervene.getRadius() + interveneCastAndRangeIncrease);
                intervene.setBreakRadius(intervene.getBreakRadius() + interveneCastAndRangeIncrease);
            });
        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(Intervene.class).forEach(intervene -> {
                intervene.setMaxDamagePrevented(intervene.getMaxDamagePrevented() - interveneCapIncrease);
                intervene.setTickDuration(intervene.getTickDuration() - interveneDurationIncreaseTicks);
                intervene.setRadius(intervene.getRadius() - interveneCastAndRangeIncrease);
                intervene.setBreakRadius(intervene.getBreakRadius() - interveneCastAndRangeIncrease);
            });
        }

    }

}
