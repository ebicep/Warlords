package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class BlizzardBreath implements SpecBoostManager.SpecBoost<BlizzardBreath> {

    private float cooldownReductionPerEnemyHitPercent;
    private float breathRangeIncreaseBlocks;
    private float immunityDurationSeconds;

    @Override
    public void init() {
        this.cooldownReductionPerEnemyHitPercent = getValue("cooldownReductionPerEnemyHitPercent", float.class);
        this.breathRangeIncreaseBlocks = getValue("breathRangeIncreaseBlocks", float.class);
        this.immunityDurationSeconds = getValue("immunityDurationSeconds", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "blizzardBreath";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(cooldownReductionPerEnemyHitPercent, breathRangeIncreaseBlocks, immunityDurationSeconds);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public BlizzardBreath get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {


        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {

        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {

        }

    }


}