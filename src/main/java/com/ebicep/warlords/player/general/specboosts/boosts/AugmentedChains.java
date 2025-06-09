package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.ChainHeal;
import com.ebicep.warlords.abilities.EarthenSpike;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.event.EventHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AugmentedChains implements SpecBoostManager.SpecBoost<AugmentedChains> {

    private int energyCostReduction;
    private float earthenSpikeDamageReductionPercent;
    private int chainHealCooldownReductionTicks;
    private float chainHealHealingIncreasePercent;

    @Override
    public void init() {
        this.energyCostReduction = getValue("energyCostReduction", int.class);
        this.earthenSpikeDamageReductionPercent = getValue("earthenSpikeDamageReductionPercent", float.class);
        this.chainHealCooldownReductionTicks = getValue("chainHealCooldownReductionSeconds", int.class);
        this.chainHealHealingIncreasePercent = getValue("chainHealHealingIncreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "augmentedChains";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(energyCostReduction, earthenSpikeDamageReductionPercent, chainHealCooldownReductionTicks, chainHealHealingIncreasePercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public AugmentedChains get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private final Set<UUID> spikesHit = new HashSet<>();
        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(EarthenSpike.class).forEach(earthenSpike -> {
                earthenSpike.getEnergyCost().addAdditiveModifier("Spec Boost", -energyCostReduction);
                earthenSpike.getDamageValues().getSpikeDamage().forEachValue(floatModifiable ->
                        floatModifiable.addMultiplicativeModifierAdd("Spec Boost", -earthenSpikeDamageReductionPercent / 100)
                );
            });
            warlordsPlayer.getAbilitiesMatching(ChainHeal.class).forEach(chainHeal -> {
                chainHeal.getHealValues().getChainHealing().forEachValue(floatModifiable ->
                        floatModifiable.addMultiplicativeModifierAdd("Spec Boost", chainHealHealingIncreasePercent / 100)
                );
            });
        }

        @EventHandler
        public void onDamageHealFinalEvent(WarlordsDamageHealingFinalEvent event) {
            if (!event.getSource().equals(warlordsEntity)) {
                return;
            }
            if (!(event.getAbility() instanceof EarthenSpike earthenSpike)) {
                return;
            }
            UUID uuid = event.getWarlordsDamageHealingEvent().getUUID();
            if (spikesHit.contains(uuid)) {
                return;
            }
            spikesHit.add(uuid);
            warlordsEntity.getAbilitiesMatching(ChainHeal.class).forEach(chainHeal -> {
                chainHeal.subtractCurrentCooldown(chainHealCooldownReductionTicks / 20f);
            });
        }

    }

}
