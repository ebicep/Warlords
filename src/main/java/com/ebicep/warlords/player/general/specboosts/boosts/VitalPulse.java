package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.HeartToHeart;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class VitalPulse implements SpecBoostManager.SpecBoost<VitalPulse> {

    private int healthIncrease;
    private float heartToHeartCooldownReductionSeconds;
    private int heartToHeartHealingIncrease;

    @Override
    public void init() {
        this.healthIncrease = getValue("healthIncrease", int.class);
        this.heartToHeartCooldownReductionSeconds = getValue("heartToHeartCooldownReductionSeconds", float.class);
        this.heartToHeartHealingIncrease = getValue("heartToHeartHealingIncrease", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "vitalPulse";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healthIncrease, heartToHeartCooldownReductionSeconds, heartToHeartHealingIncrease);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public VitalPulse get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getHealth().addAdditiveModifier("Spec Boost (Base)", healthIncrease);
            warlordsPlayer.getAbilitiesMatching(HeartToHeart.class).forEach(heartToHeart -> {
                heartToHeart.getCooldown().addAdditiveModifier("Spec Boost", -heartToHeartCooldownReductionSeconds);
                heartToHeart.getHealValues().getHeartToHeartHealing().forEachValue(floatModifiable ->
                        floatModifiable.addAdditiveModifier("Spec Boost", heartToHeartHealingIncrease)
                );
            });
        }

    }

}
