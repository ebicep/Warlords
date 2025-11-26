package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.EnergySeerConjurer;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class HouseOfLife implements SpecBoostManager.SpecBoost<HouseOfLife> {

    private int healthIncrease;
    private int energyIncrease;
    private float energySeerHealingMultiplier;
    private int energySeerEnergyIncrease;

    @Override
    public void init() {
        this.healthIncrease = getValue("healthIncrease", int.class);
        this.energyIncrease = getValue("energyIncrease", int.class);
        this.energySeerHealingMultiplier = getValue("energySeerHealingMultiplier", float.class);
        this.energySeerEnergyIncrease = getValue("energySeerEnergyIncrease", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "houseOfLife";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healthIncrease, energyIncrease, energySeerHealingMultiplier, energySeerEnergyIncrease);
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
            warlordsPlayer.getHealth().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost (Base)", healthIncrease);
            warlordsPlayer.getEnergy().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost", energyIncrease);

            warlordsPlayer.getAbilitiesMatching(EnergySeerConjurer.class).forEach(energySeer -> {
                energySeer.getHealValues().getSeerHealingMultiplier().forEachValue(floatModifiable ->
                        floatModifiable.addModifier(FloatModifiable.ModifierType.OVERRIDING, "Spec Boost", energySeerHealingMultiplier / 100)
                );
                energySeer.setEnergyRestore(energySeer.getEnergyRestore() + energySeerEnergyIncrease);
            });
        }

    }

}
