package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.FrostBolt;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class FrostMissile implements SpecBoostManager.SpecBoost<FrostMissile> {

    private float damageIncrease;

    @Override
    public void init() {
        this.damageIncrease = getValue("damageIncrease", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "frostMissile";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(damageIncrease);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public FrostMissile get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(FrostBolt.class).forEach(frostBolt ->
                    frostBolt.getDamageValues()
                             .getBoltDamage()
                             .forEachValue(floatModifiable -> floatModifiable.addMultiplicativeModifierAdd("Spec Boost", damageIncrease / 100))
            );
        }

    }

}
