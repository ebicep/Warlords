package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.FallenSouls;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class SoulRend implements SpecBoostManager.SpecBoost<SoulRend> {

    private float fallenSoulsDamageIncreasePercent;

    @Override
    public void init() {
        this.fallenSoulsDamageIncreasePercent = getValue("fallenSoulsDamageIncreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "soulRend";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(fallenSoulsDamageIncreasePercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public SoulRend get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(FallenSouls.class).forEach(fallenSouls -> {
                fallenSouls.getDamageValues().getFallenSoulDamage().forEachValue(floatModifiable ->
                        floatModifiable.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Spec Boost", fallenSoulsDamageIncreasePercent / 100)
                );
            });
        }

    }

}
