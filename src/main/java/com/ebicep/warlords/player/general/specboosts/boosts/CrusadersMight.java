package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.CrusadersStrike;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class CrusadersMight implements SpecBoostManager.SpecBoost<CrusadersMight> {

    private float damageIncreasePercent;

    @Override
    public void init() {
        this.damageIncreasePercent = getValue("damageIncreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "crusadersMight";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(damageIncreasePercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public CrusadersMight get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(CrusadersStrike.class).forEach(crusaderStrike -> {
                crusaderStrike.getDamageValues().getStrikeDamage().forEachValue(floatModifiable ->
                        floatModifiable.addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "Spec Boost", damageIncreasePercent / 100)
                );
            });
        }

    }

}
