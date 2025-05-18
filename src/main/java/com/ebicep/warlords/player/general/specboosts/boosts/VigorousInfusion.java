package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.LightInfusionCrusader;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class VigorousInfusion implements SpecBoostManager.SpecBoost<VigorousInfusion> {

    private float infusionSpeedIncrease;
    private int infusionDurationIncreaseTicks;
    private float cooldownReductionSeconds;

    @Override
    public void init() {
        this.infusionSpeedIncrease = getValue("infusionSpeedIncrease", float.class);
        this.infusionDurationIncreaseTicks = getValue("infusionDurationIncreaseTicks", int.class);
        this.cooldownReductionSeconds = getValue("cooldownReductionSeconds", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "vigorousInfusion";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(infusionSpeedIncrease, infusionDurationIncreaseTicks, cooldownReductionSeconds);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public VigorousInfusion get() {
        return this;
    }


    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(LightInfusionCrusader.class).forEach(lightInfusion -> {
                lightInfusion.setSpeedBuff(lightInfusion.getSpeedBuff() + infusionSpeedIncrease);
                lightInfusion.setTickDuration(lightInfusion.getTickDuration() + infusionDurationIncreaseTicks);
                lightInfusion.getCooldown().addAdditiveModifier("Spec Boost", -cooldownReductionSeconds);
            });
        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(LightInfusionCrusader.class).forEach(lightInfusion -> {
                lightInfusion.setSpeedBuff(lightInfusion.getSpeedBuff() - infusionSpeedIncrease);
                lightInfusion.setTickDuration(lightInfusion.getTickDuration() - infusionDurationIncreaseTicks);
                lightInfusion.getCooldown().removeModifier("Spec Boost");
            });
        }

    }

}