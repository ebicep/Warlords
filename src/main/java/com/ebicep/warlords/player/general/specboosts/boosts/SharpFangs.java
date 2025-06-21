package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.PoisonousHex;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class SharpFangs implements SpecBoostManager.SpecBoost<SharpFangs> {

    private float poisonousHexDamageIncreasePercent;

    @Override
    public void init() {
        this.poisonousHexDamageIncreasePercent = getValue("poisonousHexDamageIncreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "sharpFangs";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(poisonousHexDamageIncreasePercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public SharpFangs get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(PoisonousHex.class).forEach(poisonousHex -> {
                poisonousHex.getDamageValues().getHexDamage().forEachValue(floatModifiable ->
                        floatModifiable.addMultiplicativeModifierAdd("Spec Boost", poisonousHexDamageIncreasePercent / 100)
                );
            });
        }

    }

}
