package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.OrbsOfLife;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class VibrantOrbs implements SpecBoostManager.SpecBoost<VibrantOrbs> {

    private float healingIncreasePercent;

    @Override
    public void init() {
        this.healingIncreasePercent = getValue("healingIncreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "vibrantOrbs";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healingIncreasePercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public VibrantOrbs get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(OrbsOfLife.class).forEach(orbsOfLife ->
                    orbsOfLife.getHealValues()
                            .getOrbHealing()
                            .forEachValue(floatModifiable -> floatModifiable.addMultiplicativeModifierAdd("Spec Boost", healingIncreasePercent / 100))
            );
        }


    }

}
