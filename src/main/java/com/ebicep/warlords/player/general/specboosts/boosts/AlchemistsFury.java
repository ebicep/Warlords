package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.SoothingElixir;
import com.ebicep.warlords.abilities.VolatileBrew;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class AlchemistsFury implements SpecBoostManager.SpecBoost<AlchemistsFury> {

    private float soothingElixirDamageIncreasePercent;
    private float soothingElixirProjectileSpeedMultiplier;
    private float soothingElixirRadiusIncrease;
    private float soothingElixirPuddleRadiusIncrease;
    private int soothingElixirExtraLeechStacks;

    @Override
    public void init() {
        this.soothingElixirDamageIncreasePercent = getValue("soothingElixirDamageIncreasePercent", float.class);
        this.soothingElixirProjectileSpeedMultiplier = getValue("soothingElixirProjectileSpeedMultiplier", float.class);
        this.soothingElixirRadiusIncrease = getValue("soothingElixirRadiusIncrease", float.class);
        this.soothingElixirPuddleRadiusIncrease = getValue("soothingElixirPuddleRadiusIncrease", float.class);
        this.soothingElixirExtraLeechStacks = getValue("soothingElixirExtraLeechStacks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "alchemistsFury";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                soothingElixirDamageIncreasePercent,
                soothingElixirProjectileSpeedMultiplier,
                soothingElixirRadiusIncrease,
                soothingElixirPuddleRadiusIncrease,
                soothingElixirExtraLeechStacks
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
                soothingElixir.setSpeed(soothingElixir.getSpeed() * soothingElixirProjectileSpeedMultiplier);
                soothingElixir.getDamageValues().getElixirDamage().forEachValue(floatModifiable ->
                        floatModifiable.addMultiplicativeModifierAdd("Spec Boost", soothingElixirDamageIncreasePercent / 100)
                );
                soothingElixir.getHealValues().getElixirHealing().forEachValue(floatModifiable ->
                        floatModifiable.addOverridingModifier("Spec Boost", 0)
                );
                soothingElixir.getHealValues().getElixirDOTHealing().forEachValue(floatModifiable ->
                        floatModifiable.addOverridingModifier("Spec Boost", 0)
                );
                soothingElixir.getHitBoxRadius().addAdditiveModifier("Spec Boost", soothingElixirRadiusIncrease);
                soothingElixir.setLeechStacksApplied(soothingElixir.getLeechStacksApplied() + soothingElixirExtraLeechStacks);
            });
            warlordsPlayer.getAbilitiesMatching(VolatileBrew.class).forEach(volatileBrew -> {
                volatileBrew.setEarlyActivationEffectivenessReduction(0);
            });
        }

    }

}
