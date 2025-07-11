package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.ArcaneShield;
import com.ebicep.warlords.abilities.TimeWarpCryomancer;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class SteadfastWarp implements SpecBoostManager.SpecBoost<SteadfastWarp> {

    private int healthIncrease;
    private int absorptionDecreasePercent;
    private int warpHealDecreasePercent;
    private int warpBackMaxHeal;
    private int recastDelayTicks;
    private int kbResistanceLossOnRecastTicks;

    @Override
    public void init() {
        this.healthIncrease = getValue("healthIncrease", int.class);
        this.absorptionDecreasePercent = getValue("absorptionDecreasePercent", int.class);
        this.warpHealDecreasePercent = getValue("warpHealDecreasePercent", int.class);
        this.warpBackMaxHeal = getValue("warpBackMaxHeal", int.class);
        this.recastDelayTicks = getValue("recastDelayTicks", int.class);
        this.kbResistanceLossOnRecastTicks = getValue("kbResistanceLossOnRecastTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "steadfastWarp";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healthIncrease, absorptionDecreasePercent, warpHealDecreasePercent, warpBackMaxHeal, recastDelayTicks, kbResistanceLossOnRecastTicks);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public SteadfastWarp get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getHealth().addAdditiveModifier("Spec Boost (Base)", healthIncrease);
            warlordsPlayer.getAbilitiesMatching(ArcaneShield.class).forEach(arcaneShield -> {
                arcaneShield.setShieldPercentage(arcaneShield.getShieldPercentage() - absorptionDecreasePercent);
            });
            warlordsPlayer.getAbilitiesMatching(TimeWarpCryomancer.class).forEach(timeWarpCryomancer -> {
                timeWarpCryomancer.setWarpHealPercentage(timeWarpCryomancer.getWarpHealPercentage() - warpHealDecreasePercent);
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!(cooldown instanceof RegularCooldown<?> regularCooldown)) {
                return;
            }
            if (!(cooldown.getCooldownObject() instanceof TimeWarpCryomancer.TimeWarpPyromancerData data) || !cooldown.getFrom().equals(warlordsEntity)) {
                return;
            }
            TimeWarpCryomancer timeWarpCryomancer = data.getStaticAbility();
            float preWarpHealth = warlordsEntity.getCurrentHealth();
            Runnable regularWarpHeal = data.getWarpHeal();
            data.setWarpHeal(() -> {
                float regularWarpHealing = warlordsEntity.getMaxHealth() * (timeWarpCryomancer.getWarpHealPercentage() / 100f);
                float preWarpHealing = preWarpHealth - warlordsEntity.getCurrentHealth();
                if (regularWarpHealing > preWarpHealing) {
                    regularWarpHeal.run();
                } else if (warpBackMaxHeal > preWarpHealing){
                    warlordsEntity.addInstance(InstanceBuilder
                            .healing()
                            .cause(getStringName())
                            .source(warlordsEntity)
                            .value(preWarpHealing));
                } else {
                    warlordsEntity.addInstance(InstanceBuilder
                            .healing()
                            .cause(getStringName())
                            .source(warlordsEntity)
                            .value(warpBackMaxHeal));
                }
            });
            timeWarpCryomancer.addSecondaryAbility(
                    recastDelayTicks,
                    () -> {
                        int remainingKbResTickDuration = regularCooldown.getTicksLeft() - kbResistanceLossOnRecastTicks;
                        regularCooldown.setTicksLeft(1);
                        if (remainingKbResTickDuration < 0) {
                            return;
                        }
                        warlordsEntity.addKnockbackModifier(warlordsEntity, getStringName(), -100, remainingKbResTickDuration);
                    },
                    false,
                    secondaryAbility -> !warlordsEntity.getCooldownManager().hasCooldown(regularCooldown)
            );
            warlordsEntity.addKnockbackModifier(warlordsEntity, getStringName(), -100, regularCooldown);
        }

    }

}