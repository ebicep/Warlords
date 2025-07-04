package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.Boulder;
import com.ebicep.warlords.abilities.EarthenSpike;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class AcceleratedSpike implements SpecBoostManager.SpecBoost<AcceleratedSpike> {

    private float travelSpeedIncreasePercent;
    private float castRangeIncrease;
    private float hitRadius;
    private float damageIncreasePercent;
    private int maxEnergyIncrease;
    private int boulderEnergyCostIncrease;

    @Override
    public void init() {
        this.travelSpeedIncreasePercent = getValue("travelSpeedIncreasePercent", float.class);
        this.castRangeIncrease = getValue("castRangeIncrease", float.class);
        this.hitRadius = getValue("hitRadiusIncrease", float.class);
        this.damageIncreasePercent = getValue("damageIncreasePercent", float.class);
        this.maxEnergyIncrease = getValue("maxEnergyIncrease", int.class);
        this.boulderEnergyCostIncrease = getValue("boulderEnergyCostIncrease", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "acceleratedSpike";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                maxEnergyIncrease,
                boulderEnergyCostIncrease,
                travelSpeedIncreasePercent,
                castRangeIncrease,
                hitRadius,
                damageIncreasePercent
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public AcceleratedSpike get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(EarthenSpike.class).forEach(earthenSpike -> {
                earthenSpike.setVerticalVelocity(0);
                earthenSpike.setSpeed(earthenSpike.getSpeed() * AbstractAbility.convertToMultiplicationDecimal(travelSpeedIncreasePercent));
                earthenSpike.getHitBoxRadius().addAdditiveModifier("Spec Boost", castRangeIncrease);
                earthenSpike.setSpikeHitbox(hitRadius);
                earthenSpike.getDamageValues().getSpikeDamage().forEachValue(floatModifiable ->
                        floatModifiable.addMultiplicativeModifierAdd("Spec Boost", damageIncreasePercent / 100)
                );
            });
            warlordsPlayer.getAbilitiesMatching(Boulder.class).forEach(boulder -> {
                boulder.getEnergyCost().addAdditiveModifier("Spec Boost", boulderEnergyCostIncrease);
            });
            warlordsPlayer.getEnergy().addAdditiveModifier("Spec Boost", maxEnergyIncrease);
        }

    }

}
