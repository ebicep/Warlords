package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.BloodLust;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BloodFrenzy implements SpecBoostManager.SpecBoost<BloodFrenzy> {

    private float bloodLustReductionPercent;
    private float bloodLustHealingIncreasePercent;

    @Override
    public void init() {
        this.bloodLustReductionPercent = getValue("bloodLustReductionPercent", float.class);
        this.bloodLustHealingIncreasePercent = getValue("bloodLustHealingIncreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "bloodFrenzy";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(bloodLustReductionPercent, bloodLustHealingIncreasePercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public BloodFrenzy get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private final Map<BloodLust, Integer> previousTickDurations = new HashMap<>();

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(BloodLust.class).forEach(bloodLust -> {
                bloodLust.getCooldown().addMultiplicativeModifierAdd("Spec Boost", -bloodLustReductionPercent / 100);
                bloodLust.getEnergyCost().addMultiplicativeModifierAdd("Spec Boost", -bloodLustReductionPercent / 100);
                previousTickDurations.put(bloodLust, bloodLust.getTickDuration());
                bloodLust.multiplyTickDuration((100 - bloodLustReductionPercent) / 100);
                bloodLust.setDamageConvertPercent(bloodLust.getDamageConvertPercent() + 5);
            });
        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(BloodLust.class).forEach(bloodLust -> {
                bloodLust.getCooldown().removeModifier("Spec Boost");
                bloodLust.getEnergyCost().removeModifier("Spec Boost");
                Integer duration = previousTickDurations.get(bloodLust);
                if (duration != null) {
                    bloodLust.setTickDuration(duration);
                }
                bloodLust.setDamageConvertPercent(bloodLust.getDamageConvertPercent() - 5);
            });
        }

    }

}

