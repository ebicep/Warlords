package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.Berserk;
import com.ebicep.warlords.abilities.BloodLust;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.CustomInstanceFlags;
import org.bukkit.event.EventHandler;

import java.util.List;

public class BloodFrenzy implements SpecBoostManager.SpecBoost<BloodFrenzy> {

    private int bloodLustCooldownReductionTicks;
    private int bloodLustDurationReductionTicks;
    private int bloodLustEnergyCostReduction;
    private float bloodLustHealingPercent;
    private float bloodLustHealingPiercePercent;

    @Override
    public void init() {
        this.bloodLustCooldownReductionTicks = getValue("bloodLustCooldownReductionTicks", int.class);
        this.bloodLustDurationReductionTicks = getValue("bloodLustDurationReductionTicks", int.class);
        this.bloodLustEnergyCostReduction = getValue("bloodLustEnergyCostReduction", int.class);
        this.bloodLustHealingPercent = getValue("bloodLustHealingPercent", float.class);
        this.bloodLustHealingPiercePercent = getValue("bloodLustHealingPiercePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "bloodFrenzy";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(bloodLustCooldownReductionTicks, bloodLustDurationReductionTicks, bloodLustEnergyCostReduction, bloodLustHealingPercent, bloodLustHealingPiercePercent);
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

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(BloodLust.class).forEach(bloodLust -> {
                bloodLust.getCooldown().addAdditiveModifier("Spec Boost", -bloodLustCooldownReductionTicks / 20f);
                bloodLust.setTickDuration(bloodLust.getTickDuration() - bloodLustDurationReductionTicks);
                bloodLust.getEnergyCost().addAdditiveModifier("Spec Boost", -bloodLustEnergyCostReduction);
                bloodLust.setDamageConvertPercent(bloodLust.getDamageConvertPercent() - (int) bloodLustHealingPercent);
            });
        }

        @EventHandler
        public void onDamageHealEvent(WarlordsDamageHealingEvent event) {
            if (!event.getSource().equals(warlordsEntity)) {
                return;
            }
            if (!(event.getAbility() instanceof BloodLust bloodLust)) {
                return;
            }
            if (event.getFlags().contains(InstanceFlags.NO_LUST_HEALING)) {
                return;
            }
            for (CustomInstanceFlags customFlag : event.getCustomFlags()) {
                if (customFlag instanceof CustomInstanceFlags.FinalEventInstanceFlag(WarlordsDamageHealingFinalEvent finalEvent)) {
                    float additionalHealing = finalEvent.getValueBeforeAllReduction() * bloodLustHealingPiercePercent / 100f;
                    event.applyToMinMax(floatModifiable ->
                            floatModifiable.addAdditiveModifier(getStringName(), additionalHealing)
                    );
                    return;
                }
            }
        }

        @EventHandler
        public void onWarlordsAbilityActivatePostApplyEvent(WarlordsAbilityActivateEvent.Post event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            if (event.getAbility() instanceof Berserk) {
                warlordsEntity.getAbilitiesMatching(BloodLust.class).forEach(bloodLust -> {
                    bloodLust.setCurrentCooldown(0);
                });
            }
        }

    }

}

