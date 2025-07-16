package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.FrostBolt;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class FrostMissile implements SpecBoostManager.SpecBoost<FrostMissile> {

    private int energyDecrease;
    private float damageDecreasePercent;
    private int slowIncrease;
    private int directHitSlowIncrease;

    @Override
    public void init() {
        this.energyDecrease = getValue("energyDecrease", int.class);
        this.damageDecreasePercent = getValue("damageDecreasePercent", float.class);
        this.slowIncrease = getValue("slowIncrease", int.class);
        this.directHitSlowIncrease = getValue("directHitSlowIncrease", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "frostMissile";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(energyDecrease, damageDecreasePercent, slowIncrease, directHitSlowIncrease);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public FrostMissile get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(FrostBolt.class).forEach(frostBolt -> {
                frostBolt.getEnergyCost().addAdditiveModifier("Spec Boost", -energyDecrease);
                frostBolt.getDamageValues()
                         .getBoltDamage()
                         .forEachValue(floatModifiable -> floatModifiable.addMultiplicativeModifierAdd("Spec Boost", -damageDecreasePercent / 100));
                frostBolt.setSlowness(frostBolt.getSlowness() + slowIncrease);
                frostBolt.setDirectHitAdditionalSlowness(frostBolt.getDirectHitAdditionalSlowness() + directHitSlowIncrease);
            });
        }

    }

}
