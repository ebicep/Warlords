package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.GroundSlamDefender;
import com.ebicep.warlords.abilities.SeismicWaveDefender;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class FerventForce implements SpecBoostManager.SpecBoost<FerventForce> {

    private float seismicWaveCooldownReductionPercent;
    private float groundSlamCooldownReductionPercent;

    @Override
    public void init() {
        this.seismicWaveCooldownReductionPercent = getValue("seismicWaveCooldownReductionPercent", float.class);
        this.groundSlamCooldownReductionPercent = getValue("groundSlamCooldownReductionPercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "ferventForce";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(seismicWaveCooldownReductionPercent, groundSlamCooldownReductionPercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public FerventForce get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(SeismicWaveDefender.class).forEach(seismicWave -> {
                seismicWave.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Spec Boost", -seismicWaveCooldownReductionPercent / 100);
            });
            warlordsPlayer.getAbilitiesMatching(GroundSlamDefender.class).forEach(groundSlam -> {
                groundSlam.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Spec Boost", -groundSlamCooldownReductionPercent / 100);
            });
        }

    }

}
