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
    private float lightningRodCooldownDecreaseSeconds;
    private float lightningRodHealingDecreasePercent;
    private int lightningRodEnergyRestoreDecrease;
    private float lightningRodSpeedIncreasePercent;
    private int lightningRodSpeedDurationTicks;
    private float capacitorTotemDamageDecreasePercent;
    private float lightningRodMagnitude;
    private float lightningRodY;

    @Override
    public void init() {
        this.lightningRodMaxAbilityCharges = getValue("lightningRodMaxAbilityCharges", int.class);
        this.lightningRodCooldownDecreaseSeconds = getValue("lightningRodCooldownDecreaseSeconds", float.class);
        this.lightningRodHealingDecreasePercent = getValue("lightningRodHealingDecreasePercent", float.class);
        this.lightningRodEnergyRestoreDecrease = getValue("lightningRodEnergyRestoreDecrease", int.class);
        this.lightningRodSpeedIncreasePercent = getValue("lightningRodSpeedIncreasePercent", float.class);
        this.lightningRodSpeedDurationTicks = getValue("lightningRodSpeedDurationTicks", int.class);
        this.capacitorTotemDamageDecreasePercent = getValue("capacitorTotemDamageDecreasePercent", float.class);
        this.lightningRodMagnitude = getValue("lightningRodMagnitude", float.class);
        this.lightningRodY = getValue("lightningRodY", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "galvanizedSpark";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                lightningRodMaxAbilityCharges,
                lightningRodCooldownDecreaseSeconds,
                lightningRodHealingDecreasePercent,
                lightningRodEnergyRestoreDecrease,
                lightningRodSpeedIncreasePercent,
                lightningRodSpeedDurationTicks,
                capacitorTotemDamageDecreasePercent
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
                lightningRod.getCooldown().addAdditiveModifier("Spec Boost", -lightningRodCooldownDecreaseSeconds);
                lightningRod.getHealValues().getHealthRestore().value().setBaseValue(lightningRod.getHealValues().getHealthRestore().getValue() - lightningRodHealingDecreasePercent);
                lightningRod.setEnergyRestore(lightningRod.getEnergyRestore() - lightningRodEnergyRestoreDecrease);
                lightningRod.setMagnitude(lightningRodMagnitude);
                lightningRod.setY(lightningRodY);
            });
            warlordsPlayer.getAbilitiesMatching(CapacitorTotem.class).forEach(capacitorTotem -> {
                capacitorTotem.getDamageValues()
                              .getTotemDamage()
                              .forEachValue(floatModifiable -> floatModifiable.addMultiplicativeModifierAdd("Spec Boost", -capacitorTotemDamageDecreasePercent / 100));
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
