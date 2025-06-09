package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.ImpalingStrike;
import com.ebicep.warlords.abilities.VitalityConcoction;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class SustainedOnslaught implements SpecBoostManager.SpecBoost<SustainedOnslaught> {

    private int speedIncrease;
    private int vitalityConcoctionHealing;
    private float vitalityConcoctionCooldownReductionSeconds;
    private int impalingStrikeEnergyCostReduction;

    @Override
    public void init() {
        this.speedIncrease = getValue("speedIncrease", int.class);
        this.vitalityConcoctionHealing = getValue("vitalityConcoctionHealing", int.class);
        this.vitalityConcoctionCooldownReductionSeconds = getValue("vitalityConcoctionCooldownReductionSeconds", float.class);
        this.impalingStrikeEnergyCostReduction = getValue("impalingStrikeEnergyCostReduction", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "sustainedOnslaught";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(speedIncrease, vitalityConcoctionHealing, vitalityConcoctionCooldownReductionSeconds, impalingStrikeEnergyCostReduction);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public SustainedOnslaught get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getSpeed().addBaseModifier(speedIncrease);
            warlordsPlayer.getAbilitiesMatching(VitalityConcoction.class).forEach(vitalityConcoction -> {
                vitalityConcoction.getCooldown().addAdditiveModifier("Spec Boost", -vitalityConcoctionCooldownReductionSeconds);
            });
            warlordsPlayer.getAbilitiesMatching(ImpalingStrike.class).forEach(impalingStrike -> {
                impalingStrike.getEnergyCost().addAdditiveModifier("Spec Boost", -impalingStrikeEnergyCostReduction);
            });
        }

        @EventHandler
        public void onWarlordsAbilityActivate(WarlordsAbilityActivateEvent.Post event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            if (event.getAbility() instanceof VitalityConcoction) {
                warlordsEntity.addInstance(InstanceBuilder
                        .healing()
                        .cause(getStringName())
                        .source(warlordsEntity)
                        .value(vitalityConcoctionHealing)
                );
            }
        }

    }

}
