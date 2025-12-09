package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.WoundingStrikeDefender;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Striker implements SpecBoostManager.SpecBoost<Striker> {

    private float woundingStrikeDamageIncreasePercent;
    private float woundingIncreasePercent;

    @Override
    public void init() {
        this.woundingStrikeDamageIncreasePercent = getValue("woundingStrikeDamageIncreasePercent", float.class);
        this.woundingIncreasePercent = getValue("woundingIncreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "striker";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(woundingStrikeDamageIncreasePercent, woundingIncreasePercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public Striker get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(WoundingStrikeDefender.class).forEach(woundingStrike -> {
                woundingStrike.getDamageValues().getStrikeDamage().forEachValue(floatModifiable ->
                        floatModifiable.addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "Spec Boost", woundingStrikeDamageIncreasePercent / 100)
                );
                woundingStrike.getWounding().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost", woundingIncreasePercent);
            });
        }

    }

}
