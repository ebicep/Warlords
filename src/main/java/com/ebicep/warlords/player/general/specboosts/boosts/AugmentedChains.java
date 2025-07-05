package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.Boulder;
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

    private float chainHealCooldownReductionSeconds;
    private float chainHealHealingIncreasePercent;
    private float chainHealEnergyCostDecrease;
    private float boulderDamageDecreasePercent;

    @Override
    public void init() {
        this.chainHealCooldownReductionSeconds = getValue("chainHealCooldownReductionSeconds", float.class);
        this.chainHealHealingIncreasePercent = getValue("chainHealHealingIncreasePercent", float.class);
        this.chainHealEnergyCostDecrease = getValue("chainHealEnergyCostDecrease", float.class);
        this.boulderDamageDecreasePercent = getValue("boulderDamageDecreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "augmentedChains";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(chainHealCooldownReductionSeconds, chainHealHealingIncreasePercent, chainHealEnergyCostDecrease, boulderDamageDecreasePercent);
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
            warlordsPlayer.getAbilitiesMatching(ChainHeal.class).forEach(chainHeal -> {
                chainHeal.getHealValues().getChainHealing().forEachValue(floatModifiable ->
                        floatModifiable.addMultiplicativeModifierAdd("Spec Boost", chainHealHealingIncreasePercent / 100)
                );
                chainHeal.getEnergyCost().addAdditiveModifier("Spec Boost", -chainHealEnergyCostDecrease);
            });
            warlordsPlayer.getAbilitiesMatching(Boulder.class).forEach(boulder -> {
                boulder.getDamageValues().getBoulderDamage().forEachValue(floatModifiable ->
                        floatModifiable.addMultiplicativeModifierAdd("Spec Boost", -boulderDamageDecreasePercent / 100)
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
                chainHeal.subtractCurrentCooldown(chainHealCooldownReductionSeconds);
            });
        }

    }

}
