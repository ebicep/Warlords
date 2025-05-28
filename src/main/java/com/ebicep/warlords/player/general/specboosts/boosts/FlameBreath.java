package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.FlameBurst;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class FlameBreath implements SpecBoostManager.SpecBoost<FlameBreath> {

    private float cooldownReductionPercent;
    private float energyCostReductionPercent;
    private float damageIncrease;

    @Override
    public void init() {
        this.cooldownReductionPercent = getValue("cooldownReductionPercent", float.class);
        this.energyCostReductionPercent = getValue("energyCostReductionPercent", float.class);
        this.damageIncrease = getValue("damageIncrease", float.class);
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

    public class Boost implements SpecBoostManager.Boost {


        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            List<AbstractAbility> abilities = warlordsPlayer.getAbilities();
            for (int i = 0; i < abilities.size(); i++) {
                if (abilities.get(i) instanceof FlameBurst flameBurst) {
                    com.ebicep.warlords.abilities.FlameBreath flameBreath = new com.ebicep.warlords.abilities.FlameBreath();
                    flameBreath.init(flameBreath.getBuilder());
                    flameBreath.getCooldown().addMultiplicativeModifierAdd("Spec Boost", -cooldownReductionPercent / 100);
                    flameBreath.getEnergyCost().addMultiplicativeModifierAdd("Spec Boost", -energyCostReductionPercent / 100);
                    flameBreath.getDamageValues().getFlameBreathDamage().forEachValue(floatModifiable ->
                            floatModifiable.addMultiplicativeModifierAdd("Spec Boost", damageIncrease / 100)
                    );
                    abilities.set(i, flameBreath);
                }
            }
            warlordsPlayer.resetAbilityTree();
        }

    }

}