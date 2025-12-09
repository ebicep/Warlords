package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.HealingRain;
import com.ebicep.warlords.abilities.WaterBolt;
import com.ebicep.warlords.abilities.WaterBreath;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Downpour implements SpecBoostManager.SpecBoost<Downpour> {

    private float waterBoltSpeedIncreasePercent;
    private float waterBreathKnockbackIncrease;
    private int healingRainMaxCharges;
    private float healingRainHealingIncreasePercent;
    private float healingRainCooldownReductionPercent;
    private float healingRainDurationDecreasePercent;
    private float healingRainRadiusDecreaseBlocks;

    @Override
    public void init() {
        this.waterBoltSpeedIncreasePercent = getValue("waterBoltSpeedIncreasePercent", float.class);
        this.waterBreathKnockbackIncrease = getValue("waterBreathKnockbackIncrease", float.class);
        this.healingRainMaxCharges = getValue("healingRainMaxCharges", int.class);
        this.healingRainCooldownReductionPercent = getValue("healingRainCooldownReductionPercent", float.class);
        this.healingRainHealingIncreasePercent = getValue("healingRainHealingIncreasePercent", float.class);
        this.healingRainDurationDecreasePercent = getValue("healingRainDurationDecreasePercent", float.class);
        this.healingRainRadiusDecreaseBlocks = getValue("healingRainRadiusDecreaseBlocks", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "downpour";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                waterBoltSpeedIncreasePercent,
                healingRainMaxCharges,
                healingRainCooldownReductionPercent,
                healingRainHealingIncreasePercent,
                healingRainDurationDecreasePercent,
                healingRainRadiusDecreaseBlocks
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public Downpour get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(WaterBolt.class).forEach(waterBolt -> {
                waterBolt.getProjectileSpeed().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "Spec Boost", (waterBoltSpeedIncreasePercent + 100) / 100);
                waterBolt.setMaxFullDistance((int) waterBolt.getMaxDistance().getCalculatedValue());
            });
            warlordsPlayer.getAbilitiesMatching(WaterBreath.class).forEach(waterBreath -> {
                waterBreath.setVelocity(waterBreath.getVelocity() + waterBreathKnockbackIncrease);
            });
            warlordsPlayer.getAbilitiesMatching(HealingRain.class).forEach(healingRain -> {
                healingRain.setMaxCharges(healingRainMaxCharges);
                healingRain.getCooldown().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "Spec Boost", -healingRainCooldownReductionPercent / 100);
                healingRain.getHealValues().getRainHealing().forEachValue(floatModifiable ->
                        floatModifiable.addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "Spec Boost", healingRainHealingIncreasePercent / 100)
                );
                healingRain.setTickDuration(Math.round(healingRain.getTickDuration() * (1 - healingRainDurationDecreasePercent / 100)));
                healingRain.getHitBoxRadius().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost", -healingRainRadiusDecreaseBlocks);
            });
        }

    }

}
