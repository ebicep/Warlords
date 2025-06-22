package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.RayOfLight;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class RadiantLight implements SpecBoostManager.SpecBoost<RadiantLight> {

    private float rayOfLightHealingIncreasePercent;

    @Override
    public void init() {
        this.rayOfLightHealingIncreasePercent = getValue("rayOfLightHealingIncreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "radiantLight";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(rayOfLightHealingIncreasePercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public RadiantLight get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(RayOfLight.class).forEach(rayOfLight -> {
                rayOfLight.getHealValues().getRayHealing().forEachValue(floatModifiable ->
                        floatModifiable.addMultiplicativeModifierAdd("Spec Boost", rayOfLightHealingIncreasePercent / 100)
                );
            });
        }

    }

}
