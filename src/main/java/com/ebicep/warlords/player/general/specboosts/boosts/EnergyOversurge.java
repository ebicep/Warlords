package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.EnergySeerLuminary;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class EnergyOversurge implements SpecBoostManager.SpecBoost<EnergyOversurge> {

    private float energySeerCooldownReductionPercent;
    private int energySeerAdditionalEnergyGrant;
    private float energyLostIncreasePercent;

    @Override
    public void init() {
        this.energySeerCooldownReductionPercent = getValue("energySeerCooldownReductionPercent", float.class);
        this.energySeerAdditionalEnergyGrant = getValue("energySeerAdditionalEnergyGrant", int.class);
        this.energyLostIncreasePercent = getValue("energyLostIncreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "energyOversurge";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(energySeerCooldownReductionPercent, energySeerAdditionalEnergyGrant, energyLostIncreasePercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public EnergyOversurge get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(EnergySeerLuminary.class).forEach(energySeer -> {
                energySeer.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Spec Boost", -energySeerCooldownReductionPercent / 100);
                energySeer.setEnergyRestore(energySeer.getEnergyRestore() + energySeerAdditionalEnergyGrant);
                energySeer.setEpsDecrease((int) (energySeer.getEpsDecrease() * (1 + energyLostIncreasePercent / 100)));
            });
        }

    }

}
