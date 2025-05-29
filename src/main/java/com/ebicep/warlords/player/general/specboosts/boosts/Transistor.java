package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.LightningBolt;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class Transistor implements SpecBoostManager.SpecBoost<Transistor> {

    private float lightningBoltDamageIncreasePercent;

    @Override
    public void init() {
        this.lightningBoltDamageIncreasePercent = getValue("lightningBoltDamageIncreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "transistor";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(lightningBoltDamageIncreasePercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public Transistor get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(LightningBolt.class).forEach(lightningBolt -> {
                lightningBolt.getDamageValues().getBoltDamage().forEachValue(floatModifiable ->
                        floatModifiable.addMultiplicativeModifierAdd("Spec Boost", lightningBoltDamageIncreasePercent / 100)
                );
            });
        }

    }

}
