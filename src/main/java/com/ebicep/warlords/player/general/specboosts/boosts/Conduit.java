package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.AvengersStrike;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Conduit implements SpecBoostManager.SpecBoost<Conduit> {

    private float avengerStrikeDamageIncreasePercent;
    private int energyRemovalIncrease;

    @Override
    public void init() {
        this.avengerStrikeDamageIncreasePercent = getValue("avengerStrikeDamageIncreasePercent", float.class);
        this.energyRemovalIncrease = getValue("energyRemovalIncrease", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "conduit";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(avengerStrikeDamageIncreasePercent, energyRemovalIncrease);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public Conduit get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(AvengersStrike.class).forEach(avengerStrike -> {
                avengerStrike.getDamageValues().getStrikeDamage().forEachValue(floatModifiable ->
                        floatModifiable.addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "Spec Boost", avengerStrikeDamageIncreasePercent / 100)
                );
                avengerStrike.setEnergySteal(avengerStrike.getEnergySteal() + energyRemovalIncrease);
            });
        }

    }

}
