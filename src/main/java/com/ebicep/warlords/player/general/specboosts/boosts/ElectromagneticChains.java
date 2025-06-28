package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.ChainLightning;
import com.ebicep.warlords.abilities.LightningBolt;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import org.bukkit.event.EventHandler;

import java.util.List;

public class ElectromagneticChains implements SpecBoostManager.SpecBoost<ElectromagneticChains> {

    private float damageReductionPercent;
    private float chainLightningDamageReductionPercent;
    private int chainLightningDurationTicks;
    private int maxStacks;

    @Override
    public void init() {
        this.damageReductionPercent = getValue("damageReductionPercent", float.class);
        this.chainLightningDamageReductionPercent = getValue("chainLightningDamageReductionPercent", float.class);
        this.chainLightningDurationTicks = getValue("chainLightningDurationTicks", int.class);
        this.maxStacks = getValue("maxStacks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "electromagneticChains";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(damageReductionPercent, chainLightningDamageReductionPercent, chainLightningDurationTicks, maxStacks);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public ElectromagneticChains get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(LightningBolt.class).forEach(lightningBolt -> {
                lightningBolt.getDamageValues().getBoltDamage().forEachValue(floatModifiable ->
                        floatModifiable.addMultiplicativeModifierAdd("Spec Boost", -damageReductionPercent / 100)
                );
            });
            warlordsPlayer.getAbilitiesMatching(ChainLightning.class).forEach(chainLightning -> {
                chainLightning.getDamageValues().getChainDamage().forEachValue(floatModifiable ->
                        floatModifiable.addMultiplicativeModifierAdd("Spec Boost", -damageReductionPercent / 100)
                );
            });
        }

        @EventHandler
        public void onDamageHealFinalEvent(WarlordsDamageHealingFinalEvent event) {
            if (!event.getSource().equals(warlordsEntity)) {
                return;
            }
            if (!(event.getAbility() instanceof ChainLightning chainLightning)) {
                return;
            }
            WarlordsEntity target = event.getWarlordsEntity();
            target.getCooldownManager().limitCooldowns(RegularCooldown.class, Boost.class, maxStacks);
            target.getCooldownManager().addCooldown(new RegularCooldown<>(
                    getStringName(),
                    "CHAIN",
                    Boost.class,
                    null,
                    warlordsEntity,
                    CooldownTypes.HIGH_LEVEL_DEBUFF,
                    cooldownManager -> {},
                    chainLightningDurationTicks
            ) {
                @Override
                public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * AbstractAbility.convertToDivisionDecimal(chainLightningDamageReductionPercent);
                }
            });
        }

    }

}
