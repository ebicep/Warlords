package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.WoundingStrikeBerserker;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class EfficientStrikes implements SpecBoostManager.SpecBoost<EfficientStrikes> {

    private int woundingStrikeEnergyCostReduction;

    @Override
    public void init() {
        this.woundingStrikeEnergyCostReduction = getValue("woundingStrikeEnergyCostReduction", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "efficientStrikes";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(woundingStrikeEnergyCostReduction);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public EfficientStrikes get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(WoundingStrikeBerserker.class).forEach(woundingStrike -> {
                woundingStrike.getEnergyCost().addAdditiveModifier("Spec Boost", -woundingStrikeEnergyCostReduction);
            });
        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(WoundingStrikeBerserker.class).forEach(woundingStrike -> {
                woundingStrike.getEnergyCost().removeModifier("Spec Boost");
            });
        }

    }

}

