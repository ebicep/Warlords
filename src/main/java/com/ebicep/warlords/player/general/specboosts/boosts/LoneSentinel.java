package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.FortifyingHex;
import com.ebicep.warlords.abilities.MysticalBarrier;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class LoneSentinel implements SpecBoostManager.SpecBoost<LoneSentinel> {

    private float fortifyingHexFlagMultiplier;
    private int fortifyingHexAllyPierceReduction;
    private float mysticalBarrierMeleeDamageReductionPercent;

    @Override
    public void init() {
        this.fortifyingHexFlagMultiplier = getValue("fortifyingHexFlagMultiplier", float.class);
        this.fortifyingHexAllyPierceReduction = getValue("fortifyingHexAllyPierceReduction", int.class);
        this.mysticalBarrierMeleeDamageReductionPercent = getValue("mysticalBarrierMeleeDamageReductionPercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "loneSentinel";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(fortifyingHexFlagMultiplier, fortifyingHexAllyPierceReduction, mysticalBarrierMeleeDamageReductionPercent);
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
            warlordsPlayer.getAbilitiesMatching(FortifyingHex.class).forEach(fortifyingHex -> {
                fortifyingHex.setDamageReductionFlagMultiplier(fortifyingHexFlagMultiplier);
                fortifyingHex.setMaxAlliesHit(fortifyingHex.getMaxAlliesHit() - fortifyingHexAllyPierceReduction);
            });
            warlordsPlayer.getAbilitiesMatching(MysticalBarrier.class).forEach(mysticalBarrier -> {
                mysticalBarrier.setMeleeDamageReduction(mysticalBarrier.getMeleeDamageReduction() + mysticalBarrierMeleeDamageReductionPercent);
            });
        }

    }

}
