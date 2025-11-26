package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.internal.AbstractArcaneShield;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class ArcaneRecluse implements SpecBoostManager.SpecBoost<ArcaneRecluse> {

    private int healthIncrease;
    private int shieldTickDurationIncrease;
    private float absorptionIncreasePercent;

    @Override
    public void init() {
        this.healthIncrease = getValue("healthIncrease", int.class);
        this.shieldTickDurationIncrease = getValue("shieldTickDurationIncrease", int.class);
        this.absorptionIncreasePercent = getValue("absorptionIncreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "arcaneRecluse";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healthIncrease, shieldTickDurationIncrease, absorptionIncreasePercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public ArcaneRecluse get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getHealth().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost (Base)", healthIncrease);
            warlordsPlayer.getAbilitiesMatching(AbstractArcaneShield.class).forEach(arcaneShield -> {
                arcaneShield.setTickDuration(arcaneShield.getTickDuration() + shieldTickDurationIncrease);
                arcaneShield.setShieldPercentage(arcaneShield.getShieldPercentage() + absorptionIncreasePercent);
                arcaneShield.updateCustomStats(warlordsPlayer);
            });
        }

    }

}