package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.EnergySeerConjurer;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class HouseOfLife implements SpecBoostManager.SpecBoost<HouseOfLife> {

    private int healthIncrease;
    private int energyIncrease;
    private float energySeerHealingMultiplier;

    @Override
    public void init() {
        this.healthIncrease = getValue("healthIncrease", int.class);
        this.energyIncrease = getValue("energyIncrease", int.class);
        this.energySeerHealingMultiplier = getValue("energySeerHealingMultiplier", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "houseOfLife";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healthIncrease, energyIncrease, energySeerHealingMultiplier);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public HouseOfLife get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {


        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getHealth().addAdditiveModifier("Spec Boost (Base)", healthIncrease);
            warlordsPlayer.getEnergy().addAdditiveModifier("Spec Boost", energyIncrease);

            warlordsPlayer.getAbilitiesMatching(EnergySeerConjurer.class).forEach(energySeer -> {
                energySeer.getHealValues().getSeerHealingMultiplier().forEachValue(floatModifiable ->
                        floatModifiable.addOverridingModifier("Spec Boost", energySeerHealingMultiplier / 100)
                );
            });
        }

    }

}
