package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.SeismicWaveBerserker;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class SeismicShift implements SpecBoostManager.SpecBoost<SeismicShift> {

    private float seismicWaveDamageIncreasePercent;
    private float seismicWaveCooldownReductionPercent;

    @Override
    public void init() {
        this.seismicWaveDamageIncreasePercent = getValue("seismicWaveDamageIncreasePercent", float.class);
        this.seismicWaveCooldownReductionPercent = getValue("seismicWaveCooldownReductionPercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "seismicShift";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(seismicWaveDamageIncreasePercent, seismicWaveCooldownReductionPercent);
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
                seismicWave.getDamageValues().getWaveDamage().forEachValue(floatModifiable ->
                        floatModifiable.addMultiplicativeModifierAdd("Spec Boost", seismicWaveDamageIncreasePercent / 100)
                );
                seismicWave.getCooldown().addMultiplicativeModifierAdd("Spec Boost", -seismicWaveCooldownReductionPercent / 100);
            });
        }

    }

}

