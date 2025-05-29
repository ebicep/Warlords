package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.Boulder;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class MegalithicBoulder implements SpecBoostManager.SpecBoost<MegalithicBoulder> {

    private float boulderDamageIncreasePercent;
    private float boulderKnockbackIncreasePercent;

    @Override
    public void init() {
        this.boulderDamageIncreasePercent = getValue("boulderDamageIncreasePercent", float.class);
        this.boulderKnockbackIncreasePercent = getValue("boulderKnockbackIncreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "megalithicBoulder";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(boulderDamageIncreasePercent, boulderKnockbackIncreasePercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public MegalithicBoulder get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(Boulder.class).forEach(boulder -> {
                boulder.getDamageValues().getBoulderDamage().forEachValue(floatModifiable ->
                        floatModifiable.addMultiplicativeModifierAdd("Spec Boost", boulderDamageIncreasePercent / 100)
                );
                boulder.setVelocity(boulder.getVelocity() * AbstractAbility.convertToMultiplicationDecimal(boulderKnockbackIncreasePercent));
            });
        }

    }

}
