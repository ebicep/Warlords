package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.ChainLightning;
import com.ebicep.warlords.abilities.LightningRod;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.event.EventHandler;

import java.util.List;

public class GalvanizedSpark implements SpecBoostManager.SpecBoost<GalvanizedSpark> {

    private float lightningRodCooldownSeconds;
    private float lightningRodHealingPercent;
    private int lightningRodEnergyRestore;
    private float lightningRodSpeedIncreasePercent;
    private int lightningRodSpeedDurationTicks;

    @Override
    public void init() {
        this.lightningRodCooldownSeconds = getValue("lightningRodCooldownSeconds", float.class);
        this.lightningRodHealingPercent = getValue("lightningRodHealingPercent", float.class);
        this.lightningRodEnergyRestore = getValue("lightningRodEnergyRestore", int.class);
        this.lightningRodSpeedIncreasePercent = getValue("lightningRodSpeedIncreasePercent", float.class);
        this.lightningRodSpeedDurationTicks = getValue("lightningRodSpeedDurationTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "galvanizedSpark";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(lightningRodCooldownSeconds, lightningRodHealingPercent, lightningRodEnergyRestore, lightningRodSpeedIncreasePercent, lightningRodSpeedDurationTicks);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public GalvanizedSpark get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(LightningRod.class).forEach(lightningRod -> {
                lightningRod.getCooldown().addOverridingModifier("Spec Boost", lightningRodCooldownSeconds);
                lightningRod.getHealValues().getHealthRestore().value().setBaseValue(lightningRodHealingPercent);
                lightningRod.setEnergyRestore(lightningRodEnergyRestore);
            });
        }

        @EventHandler
        public void onWarlordsAbilityActivate(WarlordsAbilityActivateEvent.Post event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            if (event.getAbility() instanceof LightningRod) {
                warlordsEntity.getAbilitiesMatching(ChainLightning.class).forEach(chainLightning -> {
                    chainLightning.setCurrentCooldown(0);
                });
                warlordsEntity.addSpeedModifier(warlordsEntity, getStringName(), lightningRodSpeedIncreasePercent, lightningRodSpeedDurationTicks);
            }
        }

    }

}
