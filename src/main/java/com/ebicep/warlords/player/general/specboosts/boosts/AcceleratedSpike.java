package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.EarthenSpike;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class AcceleratedSpike implements SpecBoostManager.SpecBoost<AcceleratedSpike> {

    private int maxEnergyIncrease;
    private float travelSpeedIncreasePercent;
    private float castRangeIncrease;
    private float hitRadius;
    private float damageIncreasePercent;

    @Override
    public void init() {
        this.maxEnergyIncrease = getValue("maxEnergyIncrease", int.class);
        this.travelSpeedIncreasePercent = getValue("travelSpeedIncreasePercent", float.class);
        this.castRangeIncrease = getValue("castRangeIncrease", float.class);
        this.hitRadius = getValue("hitRadiusIncrease", float.class);
        this.damageIncreasePercent = getValue("damageIncreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "acceleratedSpike";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                maxEnergyIncrease,
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
            warlordsPlayer.getEnergy().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost", maxEnergyIncrease);
            warlordsPlayer.getAbilitiesMatching(EarthenSpike.class).forEach(earthenSpike -> {
                earthenSpike.setVerticalVelocity(0);
                earthenSpike.setSpeed(earthenSpike.getSpeed() * AbstractAbility.convertToMultiplicationDecimal(travelSpeedIncreasePercent));
                earthenSpike.getHitBoxRadius().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost", castRangeIncrease);
                earthenSpike.setSpikeHitbox(hitRadius);
                earthenSpike.getDamageValues().getSpikeDamage().forEachValue(floatModifiable ->
                        floatModifiable.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Spec Boost", damageIncreasePercent / 100)
                );
            });
        }

    }

}
