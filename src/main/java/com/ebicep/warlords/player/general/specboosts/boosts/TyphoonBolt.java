package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.WaterBolt;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class TyphoonBolt implements SpecBoostManager.SpecBoost<TyphoonBolt> {

    private float waterBoltHealingIncreasePercent;

    @Override
    public void init() {
        this.waterBoltHealingIncreasePercent = getValue("waterBoltHealingIncreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "typhoonBolt";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(waterBoltHealingIncreasePercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public TyphoonBolt get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(WaterBolt.class).forEach(waterBolt -> {
                waterBolt.getHealValues().getBoltHealing().forEachValue(floatModifiable ->
                        floatModifiable.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Spec Boost", waterBoltHealingIncreasePercent / 100)
                );
            });
        }

    }

}
