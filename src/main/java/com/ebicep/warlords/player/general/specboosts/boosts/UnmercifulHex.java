package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.MercifulHex;
import com.ebicep.warlords.abilities.RayOfLight;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class UnmercifulHex implements SpecBoostManager.SpecBoost<UnmercifulHex> {

    private float mercifulHexDamageIncrease;

    @Override
    public void init() {
        this.mercifulHexDamageIncrease = getValue("mercifulHexDamageIncrease", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "unmercifulHex";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(mercifulHexDamageIncrease);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public UnmercifulHex get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(MercifulHex.class).forEach(mercifulHex -> {
                mercifulHex.setMaxEnemiesHit(Integer.MAX_VALUE);
                mercifulHex.setHexStacksPerHitAfter(0);
                mercifulHex.getDamageValues().getHexDamage().forEachValue(floatModifiable ->
                        floatModifiable.addMultiplicativeModifierAdd("Spec Boost", mercifulHexDamageIncrease / 100)
                );
            });
            warlordsPlayer.getAbilitiesMatching(RayOfLight.class).forEach(rayOfLight -> {
                rayOfLight.setRemoveDebuffs(false);
            });
        }

    }

}
