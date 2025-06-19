package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.CapacitorTotem;
import com.ebicep.warlords.abilities.ChainLightning;
import com.ebicep.warlords.abilities.LightningRod;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.event.EventHandler;

import java.util.List;

public class GalvanizedSpark implements SpecBoostManager.SpecBoost<GalvanizedSpark> {

    private int lightningRodMaxAbilityCharges;
    private float lightningRodCooldownSeconds;
    private float lightningRodHealingPercent;
    private int lightningRodEnergyRestore;
    private float lightningRodSpeedIncreasePercent;
    private int lightningRodSpeedDurationTicks;
    private float lightningRodMagnitude;
    private float lightningRodY;
    private float capacitorTotemDamageDecrease;

    @Override
    public void init() {
        this.lightningRodMaxAbilityCharges = getValue("lightningRodMaxAbilityCharges", int.class);
        this.lightningRodCooldownSeconds = getValue("lightningRodCooldownSeconds", float.class);
        this.lightningRodHealingPercent = getValue("lightningRodHealingPercent", float.class);
        this.lightningRodEnergyRestore = getValue("lightningRodEnergyRestore", int.class);
        this.lightningRodSpeedIncreasePercent = getValue("lightningRodSpeedIncreasePercent", float.class);
        this.lightningRodSpeedDurationTicks = getValue("lightningRodSpeedDurationTicks", int.class);
        this.lightningRodMagnitude = getValue("lightningRodMagnitude", float.class);
        this.lightningRodY = getValue("lightningRodY", float.class);
        this.capacitorTotemDamageDecrease = getValue("capacitorTotemDamageDecrease", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "galvanizedSpark";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                lightningRodMaxAbilityCharges,
                lightningRodCooldownSeconds,
                lightningRodHealingPercent,
                lightningRodEnergyRestore,
                lightningRodSpeedIncreasePercent,
                lightningRodSpeedDurationTicks,
                capacitorTotemDamageDecrease
        );
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
                lightningRod.setMaxCharges(lightningRodMaxAbilityCharges);
                lightningRod.setCurrentCharges(lightningRodMaxAbilityCharges);
                lightningRod.getCooldown().addOverridingModifier("Spec Boost", lightningRodCooldownSeconds);
                lightningRod.getHealValues().getHealthRestore().value().setBaseValue(lightningRodHealingPercent);
                lightningRod.setEnergyRestore(lightningRodEnergyRestore);
                lightningRod.setMagnitude(lightningRodMagnitude);
                lightningRod.setY(lightningRodY);
            });
            warlordsPlayer.getAbilitiesMatching(CapacitorTotem.class).forEach(capacitorTotem -> {
                capacitorTotem.getDamageValues()
                              .getTotemDamage()
                              .forEachValue(floatModifiable -> floatModifiable.addAdditiveModifier("Spec Boost", -capacitorTotemDamageDecrease));
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
