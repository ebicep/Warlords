package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.ImpalingStrike;
import com.ebicep.warlords.abilities.VitalityConcoction;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.event.EventHandler;

import java.util.List;

public class SustainedOnslaught implements SpecBoostManager.SpecBoost<SustainedOnslaught> {

    private int impalingStrikeEnergyCostReduction;
    private int vitalityConcoctionHealing;
    private float vitalityConcoctionCooldownReductionSeconds;

    @Override
    public void init() {
        this.impalingStrikeEnergyCostReduction = getValue("impalingStrikeEnergyCostReduction", int.class);
        this.vitalityConcoctionHealing = getValue("vitalityConcoctionHealing", int.class);
        this.vitalityConcoctionCooldownReductionSeconds = getValue("vitalityConcoctionCooldownReductionSeconds", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "sustainedOnslaught";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(impalingStrikeEnergyCostReduction, vitalityConcoctionHealing, vitalityConcoctionCooldownReductionSeconds);
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
            warlordsPlayer.getAbilitiesMatching(ImpalingStrike.class).forEach(impalingStrike -> {
                impalingStrike.getEnergyCost().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost", -impalingStrikeEnergyCostReduction);
            });
            warlordsPlayer.getAbilitiesMatching(VitalityConcoction.class).forEach(vitalityConcoction -> {
                vitalityConcoction.getCooldown().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost", -vitalityConcoctionCooldownReductionSeconds);
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
