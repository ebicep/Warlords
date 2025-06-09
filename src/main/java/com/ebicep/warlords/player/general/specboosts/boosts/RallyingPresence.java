package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.InspiringPresence;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class RallyingPresence implements SpecBoostManager.SpecBoost<RallyingPresence> {

    private int durationDecreaseTicks;
    private int energyPerSecondIncrease;
    private int speedIncreasePercent;

    @Override
    public void init() {
        this.durationDecreaseTicks = getValue("durationDecreaseTicks", int.class);
        this.energyPerSecondIncrease = getValue("energyPerSecondIncrease", int.class);
        this.speedIncreasePercent = getValue("speedIncreasePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "rallyingPresence";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(durationDecreaseTicks, energyPerSecondIncrease, speedIncreasePercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public RallyingPresence get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(InspiringPresence.class).forEach(inspiringPresence -> {
                inspiringPresence.setTickDuration(inspiringPresence.getTickDuration() - durationDecreaseTicks);
                inspiringPresence.setEnergyPerSecond(inspiringPresence.getEnergyPerSecond() + energyPerSecondIncrease);
                inspiringPresence.setSpeedBuff(inspiringPresence.getSpeedBuff() + speedIncreasePercent);
            });
        }

    }

}
