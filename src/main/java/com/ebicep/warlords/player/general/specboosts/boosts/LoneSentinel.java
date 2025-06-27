package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.EnergySeerSentinel;
import com.ebicep.warlords.abilities.FortifyingHex;
import com.ebicep.warlords.abilities.MysticalBarrier;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class LoneSentinel implements SpecBoostManager.SpecBoost<LoneSentinel> {

    private float resistanceIncreasePercent;
    private float energySeerHealingMultiplier;
    private int fortifyingHexAllyPierceReduction;

    @Override
    public void init() {
        this.resistanceIncreasePercent = getValue("resistanceIncreasePercent", float.class);
        this.energySeerHealingMultiplier = getValue("energySeerHealingMultiplier", float.class);
        this.fortifyingHexAllyPierceReduction = getValue("fortifyingHexAllyPierceReduction", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "loneSentinel";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(resistanceIncreasePercent, energySeerHealingMultiplier, fortifyingHexAllyPierceReduction);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public LoneSentinel get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.setDamageResistance(warlordsPlayer.getSpec().getDamageResistance() + resistanceIncreasePercent);
            warlordsPlayer.getAbilitiesMatching(EnergySeerSentinel.class).forEach(energySeer -> {
                energySeer.getHealValues().getSeerHealingMultiplier().forEachValue(floatModifiable ->
                        floatModifiable.addOverridingModifier("Spec Boost", energySeerHealingMultiplier / 100)
                );
            });
            warlordsPlayer.getAbilitiesMatching(FortifyingHex.class).forEach(fortifyingHex -> {
                fortifyingHex.setMaxAlliesHit(fortifyingHex.getMaxAlliesHit() - fortifyingHexAllyPierceReduction);
            });
            warlordsPlayer.getAbilitiesMatching(MysticalBarrier.class).forEach(mysticalBarrier -> {
                mysticalBarrier.setCanTargetAllies(false);
            });
        }

    }

}
