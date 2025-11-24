package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.ProtectorsStrike;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class BigGuy implements SpecBoostManager.SpecBoost<BigGuy> {

    private int healthIncrease;
    private int maxEnergyIncrease;
    private float strikeSelfHealingPercent;
    private float strikeAllyHealingPercent;

    @Override
    public void init() {
        this.healthIncrease = getValue("healthIncrease", int.class);
        this.maxEnergyIncrease = getValue("maxEnergyIncrease", int.class);
        this.strikeSelfHealingPercent = getValue("strikeSelfHealingPercent", float.class);
        this.strikeAllyHealingPercent = getValue("strikeAllyHealingPercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "bigGuy";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healthIncrease, maxEnergyIncrease, strikeSelfHealingPercent, strikeAllyHealingPercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public BigGuy get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getHealth().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost (Base)", healthIncrease);
            warlordsPlayer.getEnergy().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost", maxEnergyIncrease);
            warlordsPlayer.getAbilitiesMatching(ProtectorsStrike.class).forEach(protectorsStrike -> {
                protectorsStrike.setSelfHealing(strikeSelfHealingPercent);
                protectorsStrike.setAllyHealing(strikeAllyHealingPercent);
            });
        }

    }

}
