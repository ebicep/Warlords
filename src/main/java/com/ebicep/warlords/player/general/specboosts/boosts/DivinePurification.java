package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.ArcaneShield;
import com.ebicep.warlords.abilities.WaterBreath;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class DivinePurification implements SpecBoostManager.SpecBoost<DivinePurification> {

    private int waterBreathImmunityDurationTicks;
    private float waterBreathCooldownReductionPercent;
    private float waterBreathEnergyCostReductionPercent;
    private float arcaneShieldEnergyCost;

    @Override
    public void init() {
        this.waterBreathImmunityDurationTicks = getValue("waterBreathImmunityDurationTicks", int.class);
        this.waterBreathCooldownReductionPercent = getValue("waterBreathCooldownReductionPercent", float.class);
        this.waterBreathEnergyCostReductionPercent = getValue("waterBreathEnergyCostReductionPercent", float.class);
        this.arcaneShieldEnergyCost = getValue("arcaneShieldEnergyCost", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "divinePurification";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                waterBreathImmunityDurationTicks,
                waterBreathCooldownReductionPercent,
                waterBreathEnergyCostReductionPercent,
                arcaneShieldEnergyCost
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public DivinePurification get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(WaterBreath.class).forEach(waterBreath -> {
                waterBreath.getCooldown().addMultiplicativeModifierAdd("Spec Boost", -waterBreathCooldownReductionPercent / 100f);
                waterBreath.getEnergyCost().addMultiplicativeModifierAdd("Spec Boost", -waterBreathEnergyCostReductionPercent / 100f);
            });
            warlordsPlayer.getAbilitiesMatching(ArcaneShield.class).forEach(arcaneShield -> {
                arcaneShield.getEnergyCost().addOverridingModifier("Spec Boost", arcaneShieldEnergyCost);
            });
        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(WaterBreath.class).forEach(waterBreath -> {
                waterBreath.getCooldown().removeModifier("Spec Boost");
                waterBreath.getEnergyCost().removeModifier("Spec Boost");
            });
            warlordsPlayer.getAbilitiesMatching(ArcaneShield.class).forEach(arcaneShield -> {
                arcaneShield.getEnergyCost().removeModifier("Spec Boost");
            });
        }

        @EventHandler
        public void onDamageHealFinalEvent(WarlordsDamageHealingFinalEvent event) {
            if (!event.getSource().equals(warlordsEntity)) {
                return;
            }
            if (!(event.getAbility() instanceof WaterBreath waterBreath)) {
                return;
            }
            WarlordsEntity target = event.getWarlordsEntity();
            target.getCooldownManager().addCooldown(new RegularCooldown<>(
                    getStringName(),
                    "PURI",
                    Boost.class,
                    null,
                    warlordsEntity,
                    CooldownTypes.SPEC_BOOST,
                    cooldownManager -> {
                    },
                    waterBreathImmunityDurationTicks
            ) {

                @Override
                protected Listener getListener() {
                    return CooldownManager.getDefaultDebuffImmunityListener(target);
                }
            });
        }

    }

}
