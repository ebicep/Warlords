package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.FortifyingHex;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class RuinousHex implements SpecBoostManager.SpecBoost<RuinousHex> {

    private int fortifyingHexEnemyPierceIncrease;
    private float fortifyingHexDamageIncreasePercent;
    private int fortifyingHexEnergyCostIncrease;
    private int fortifyingHexAllyPierceReduction;
    private float fortifyingHexDamageReductionDecreasePercent;

    @Override
    public void init() {
        this.fortifyingHexEnemyPierceIncrease = getValue("fortifyingHexEnemyPierceIncrease", int.class);
        this.fortifyingHexDamageIncreasePercent = getValue("fortifyingHexDamageIncreasePercent", float.class);
        this.fortifyingHexEnergyCostIncrease = getValue("fortifyingHexEnergyCostIncrease", int.class);
        this.fortifyingHexAllyPierceReduction = getValue("fortifyingHexAllyPierceReduction", int.class);
        this.fortifyingHexDamageReductionDecreasePercent = getValue("fortifyingHexDamageReductionDecreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "ruinousHex";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                fortifyingHexEnemyPierceIncrease,
                fortifyingHexDamageIncreasePercent,
                fortifyingHexEnergyCostIncrease,
                fortifyingHexAllyPierceReduction,
                fortifyingHexDamageReductionDecreasePercent
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public RuinousHex get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(FortifyingHex.class).forEach(fortifyingHex -> {
                fortifyingHex.setMaxEnemiesHit(fortifyingHex.getMaxEnemiesHit() + fortifyingHexEnemyPierceIncrease);
                fortifyingHex.getDamageValues().getHexDamage().forEachValue(floatModifiable ->
                        floatModifiable.addMultiplicativeModifierAdd("Spec Boost", fortifyingHexDamageIncreasePercent / 100)
                );
                fortifyingHex.getEnergyCost().addAdditiveModifier("Spec Boost", fortifyingHexEnergyCostIncrease);
                fortifyingHex.setMaxAlliesHit(fortifyingHex.getMaxAlliesHit() - fortifyingHexAllyPierceReduction);
                fortifyingHex.getDamageReduction().addAdditiveModifier("Spec Boost", -fortifyingHexDamageReductionDecreasePercent);
            });
        }

    }

}
