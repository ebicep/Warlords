package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.SanctifiedBeacon;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class ConsecratedBeacon implements SpecBoostManager.SpecBoost<ConsecratedBeacon> {

    private float sanctifiedBeaconEnergyCostDecrease;
    private float sanctifiedBeaconCooldownReductionPercent;
    private int sanctifiedBeaconAdditionalHexStacks;

    @Override
    public void init() {
        this.sanctifiedBeaconEnergyCostDecrease = getValue("sanctifiedBeaconEnergyCostDecrease", float.class);
        this.sanctifiedBeaconCooldownReductionPercent = getValue("sanctifiedBeaconCooldownReductionPercent", float.class);
        this.sanctifiedBeaconAdditionalHexStacks = getValue("sanctifiedBeaconAdditionalHexStacks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "consecratedBeacon";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(sanctifiedBeaconEnergyCostDecrease, sanctifiedBeaconCooldownReductionPercent, sanctifiedBeaconAdditionalHexStacks);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public ConsecratedBeacon get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(SanctifiedBeacon.class).forEach(sanctifiedBeacon -> {
                sanctifiedBeacon.getEnergyCost().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost", -sanctifiedBeaconEnergyCostDecrease);
                sanctifiedBeacon.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Spec Boost", -sanctifiedBeaconCooldownReductionPercent / 100);
                sanctifiedBeacon.setStacksGranted(sanctifiedBeacon.getStacksGranted() + sanctifiedBeaconAdditionalHexStacks);
            });
        }

    }

}
