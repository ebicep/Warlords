package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.GuardianBeam;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class DivineShields implements SpecBoostManager.SpecBoost<DivineShields> {

    private float guardianBeamShieldIncreasePercent;
    private int guardianBeamShieldDurationIncreaseTicks;

    @Override
    public void init() {
        this.guardianBeamShieldIncreasePercent = getValue("guardianBeamShieldIncreasePercent", float.class);
        this.guardianBeamShieldDurationIncreaseTicks = getValue("guardianBeamShieldDurationIncreaseTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "divineShields";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(guardianBeamShieldIncreasePercent, guardianBeamShieldDurationIncreaseTicks);
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
            warlordsPlayer.getAbilitiesMatching(GuardianBeam.class).forEach(guardianBeam -> {
                guardianBeam.getShieldValues().replaceAll(
                        value -> (int) (value + (value * guardianBeamShieldIncreasePercent / 100))
                );
                guardianBeam.setTickDuration(guardianBeam.getTickDuration() + guardianBeamShieldDurationIncreaseTicks);
            });
        }

    }

}
