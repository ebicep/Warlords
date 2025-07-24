package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.SoothingElixir;
import com.ebicep.warlords.abilities.VolatileBrew;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class AlchemistsFury implements SpecBoostManager.SpecBoost<AlchemistsFury> {

    private float soothingElixirCooldownReductionSeconds;
    private float soothingElixirDamageIncreasePercent;
    private float soothingElixirProjectileSpeedMultiplier;

    @Override
    public void init() {
        this.soothingElixirCooldownReductionSeconds = getValue("soothingElixirCooldownReductionSeconds", float.class);
        this.soothingElixirDamageIncreasePercent = getValue("soothingElixirDamageIncreasePercent", float.class);
        this.soothingElixirProjectileSpeedMultiplier = getValue("soothingElixirProjectileSpeedMultiplier", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "alchemistsFury";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                soothingElixirCooldownReductionSeconds,
                soothingElixirDamageIncreasePercent,
                soothingElixirProjectileSpeedMultiplier
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public AlchemistsFury get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(SoothingElixir.class).forEach(soothingElixir -> {
                soothingElixir.getCooldown().addAdditiveModifier("Spec Boost", -soothingElixirCooldownReductionSeconds);
                soothingElixir.setSpeed(soothingElixir.getSpeed() * soothingElixirProjectileSpeedMultiplier);
                soothingElixir.setGravity(soothingElixir.getGravity() * soothingElixirProjectileSpeedMultiplier);
                soothingElixir.getDamageValues().getElixirDamage().forEachValue(floatModifiable ->
                        floatModifiable.addMultiplicativeModifierAdd("Spec Boost", soothingElixirDamageIncreasePercent / 100)
                );
                soothingElixir.getHealValues().getElixirDOTHealing().forEachValue(floatModifiable ->
                        floatModifiable.addOverridingModifier("Spec Boost", 0)
                );
            });
            warlordsPlayer.getAbilitiesMatching(VolatileBrew.class).forEach(volatileBrew -> {
                volatileBrew.setEarlyActivationEffectivenessReduction(0);
            });
        }

    }

}
