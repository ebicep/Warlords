package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.SeismicWaveBerserker;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class SeismicShift implements SpecBoostManager.SpecBoost<SeismicShift> {

    private int seismicWaveCooldownReductionTicks;

    @Override
    public void init() {
        this.seismicWaveCooldownReductionTicks = getValue("seismicWaveCooldownReductionPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "seismicShift";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(seismicWaveCooldownReductionTicks);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public SeismicShift get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(SeismicWaveBerserker.class).forEach(seismicWave -> {
                seismicWave.getCooldown().addMultiplicativeModifierAdd("Spec Boost", -seismicWaveCooldownReductionTicks / 20);
            });
        }

    }

}

