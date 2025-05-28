package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.LastStand;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;

import java.util.List;

public class VitalityBoost implements SpecBoostManager.SpecBoost<VitalityBoost> {

    private int healthIncrease;
    private float healingReceivedIncreasePercent;
    private int lastStandCooldownReductionTicks;

    @Override
    public void init() {
        this.healthIncrease = getValue("healthIncrease", int.class);
        this.healingReceivedIncreasePercent = getValue("healingReceivedIncreasePercent", float.class);
        this.lastStandCooldownReductionTicks = getValue("lastStandCooldownReductionTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "vitalityBoost";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healthIncrease, healingReceivedIncreasePercent, lastStandCooldownReductionTicks);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public VitalityBoost get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getHealth().addAdditiveModifier("Spec Boost", healthIncrease);
            warlordsPlayer.getAbilitiesMatching(LastStand.class).forEach(lastStand -> {
                lastStand.getCooldown().addAdditiveModifier("Spec Boost", -lastStandCooldownReductionTicks / 20f);
            });
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getStringName(),
                    null,
                    Boost.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.SPEC_BOOST,
                    cooldownManager -> {
                    },
                    false
            ) {
                @Override
                public float modifyHealingFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                    return currentHealValue * AbstractAbility.convertToMultiplicationDecimal(healingReceivedIncreasePercent);
                }
            });
        }

    }

}
