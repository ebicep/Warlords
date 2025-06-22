package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.MercifulHex;
import com.ebicep.warlords.abilities.RayOfLight;
import com.ebicep.warlords.abilities.SanctifiedBeacon;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class UnmercifulHex implements SpecBoostManager.SpecBoost<UnmercifulHex> {

    private float mercifulHexSelfHealingIncreasePercent;
    private float mercifulHexDamageIncreasePercent;

    @Override
    public void init() {
        this.mercifulHexSelfHealingIncreasePercent = getValue("mercifulHexSelfHealingIncreasePercent", float.class);
        this.mercifulHexDamageIncreasePercent = getValue("mercifulHexDamageIncreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "unmercifulHex";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(mercifulHexSelfHealingIncreasePercent, mercifulHexDamageIncreasePercent);
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
                mercifulHex.getHealValues().getHexSelfHealing().forEachValue(floatModifiable ->
                        floatModifiable.addMultiplicativeModifierAdd("Spec Boost", mercifulHexSelfHealingIncreasePercent / 100)
                );
                mercifulHex.getDamageValues().getHexDamage().forEachValue(floatModifiable ->
                        floatModifiable.addMultiplicativeModifierAdd("Spec Boost", mercifulHexDamageIncreasePercent / 100)
                );
                mercifulHex.setMaxEnemiesHit(Integer.MAX_VALUE);
            });
            warlordsPlayer.getAbilitiesMatching(RayOfLight.class).forEach(rayOfLight -> {
                rayOfLight.setRemoveDebuffs(false);
            });
            warlordsPlayer.getAbilitiesMatching(SanctifiedBeacon.class).forEach(sanctifiedBeacon -> {
                sanctifiedBeacon.setCritMultiplierReducedBy(0);
            });
        }

    }

}
