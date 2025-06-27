package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.FortifyingHex;
import com.ebicep.warlords.abilities.GuardianBeam;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class DivineShields implements SpecBoostManager.SpecBoost<DivineShields> {

    private float fortifyingHexDamageReductionIncreasePercent;
    private float guardianBeamShieldIncreasePercent;

    @Override
    public void init() {
        this.fortifyingHexDamageReductionIncreasePercent = getValue("fortifyingHexDamageReductionIncreasePercent", float.class);
        this.guardianBeamShieldIncreasePercent = getValue("guardianBeamShieldIncreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "divineShields";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(fortifyingHexDamageReductionIncreasePercent, guardianBeamShieldIncreasePercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public DivineShields get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(FortifyingHex.class).forEach(fortifyingHex -> {
                fortifyingHex.getDamageReduction().addAdditiveModifier("Spec Boost", fortifyingHexDamageReductionIncreasePercent);
            });
            warlordsPlayer.getAbilitiesMatching(GuardianBeam.class).forEach(guardianBeam -> {
                guardianBeam.getShieldValues().replaceAll(
                        value -> (int) (value + (value * guardianBeamShieldIncreasePercent / 100))
                );
            });
        }

    }

}
