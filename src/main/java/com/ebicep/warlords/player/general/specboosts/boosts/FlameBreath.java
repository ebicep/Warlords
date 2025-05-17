package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.FlameBurst;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class FlameBreath implements SpecBoostManager.SpecBoost<FlameBreath> {

    private float cooldownReductionPercent;
    private float energyCostReductionPercent;
    private float damageIncrease;

    private int maxAnimationTime = 12;
    private int maxAnimationEffects = 4;
    private float hitbox = 10;
    private double velocity = 1.1;

    @Override
    public void init() {
        this.cooldownReductionPercent = getValue("cooldownReductionPercent", float.class);
        this.energyCostReductionPercent = getValue("energyCostReductionPercent", float.class);
        this.damageIncrease = getValue("damageIncrease", float.class);

        this.maxAnimationTime = getValue("maxAnimationTime", int.class);
        this.maxAnimationEffects = getValue("maxAnimationEffects", int.class);
        this.hitbox = getValue("hitbox", float.class);
        this.velocity = getValue("velocity", double.class);
    }

    @Override
    public String getConfigFieldName() {
        return "flameBreath";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(cooldownReductionPercent, energyCostReductionPercent, -damageIncrease);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public FlameBreath get() {
        return this;
    }

    public int getMaxAnimationTime() {
        return maxAnimationTime;
    }

    public int getMaxAnimationEffects() {
        return maxAnimationEffects;
    }

    public float getHitbox() {
        return hitbox;
    }

    public double getVelocity() {
        return velocity;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(FlameBurst.class).forEach(flameBurst -> {
                flameBurst.setBreath(true);
                flameBurst.getCooldown().addMultiplicativeModifierAdd("Spec Boost", -cooldownReductionPercent / 100);
                flameBurst.getEnergyCost().addMultiplicativeModifierAdd("Spec Boost", -energyCostReductionPercent / 100);
                flameBurst.getDamageValues().getFlameBurstDamage().forEachValue(floatModifiable ->
                        floatModifiable.addMultiplicativeModifierAdd("Spec Boost", damageIncrease / 100)
                );
            });
        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(FlameBurst.class).forEach(flameBurst -> {
                flameBurst.setBreath(false);
                flameBurst.getCooldown().removeModifier("Spec Boost");
                flameBurst.getEnergyCost().removeModifier("Spec Boost");
                flameBurst.getDamageValues().getFlameBurstDamage().forEachValue(floatModifiable ->
                        floatModifiable.removeModifier("Spec Boost")
                );
            });
        }

    }

}