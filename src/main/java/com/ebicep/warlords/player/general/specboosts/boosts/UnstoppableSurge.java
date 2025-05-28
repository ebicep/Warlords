package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.LightInfusionAvenger;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsAddSpeedModifierEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.motionsystem.MotionModifierBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class UnstoppableSurge implements SpecBoostManager.SpecBoost<UnstoppableSurge> {

    private int lightInfusionHealing;
    private float lightInfusionSpeedIncreasePercent;
    private int lightInfusionDurationIncreaseTicks;
    private float slowResistancePercent;
    private float knockbackResistancePercent;

    @Override
    public void init() {
        this.lightInfusionHealing = getValue("lightInfusionHealing", int.class);
        this.lightInfusionSpeedIncreasePercent = getValue("lightInfusionSpeedIncreasePercent", float.class);
        this.lightInfusionDurationIncreaseTicks = getValue("lightInfusionDurationIncreaseTicks", int.class);
        this.slowResistancePercent = getValue("slowResistancePercent", float.class);
        this.knockbackResistancePercent = getValue("knockbackResistancePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "unstoppableSurge";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(lightInfusionHealing, lightInfusionSpeedIncreasePercent, lightInfusionDurationIncreaseTicks, slowResistancePercent, knockbackResistancePercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public UnstoppableSurge get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(LightInfusionAvenger.class).forEach(lightInfusionAvenger -> {
                lightInfusionAvenger.setSpeedBuff(lightInfusionAvenger.getSpeedBuff() + lightInfusionSpeedIncreasePercent);
                lightInfusionAvenger.setTickDuration(lightInfusionAvenger.getTickDuration() + lightInfusionDurationIncreaseTicks);
            });
            warlordsPlayer.getKnockback().addModifier(new MotionModifierBuilder()
                    .setFrom(warlordsPlayer)
                    .setName(getStringName())
                    .setModifier(-knockbackResistancePercent)
                    .setDuration(-1)
                    .build()
            );
        }

        @EventHandler
        public void onWarlordsAbilityActivate(WarlordsAbilityActivateEvent.Post event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            if (event.getAbility() instanceof LightInfusionAvenger) {
                warlordsEntity.addInstance(InstanceBuilder
                        .healing()
                        .cause(getStringName())
                        .source(warlordsEntity)
                        .value(lightInfusionHealing)
                );
            }
        }

        @EventHandler
        public void onAddSpeed(WarlordsAddSpeedModifierEvent event) {
            if (!event.getWarlordsEntity().equals(warlordsEntity)) {
                return;
            }
            if (event.getModifier().getModifier() > 0) {
                return;
            }
            event.getModifier().setModifier(Math.min(0, event.getModifier().getModifier() + slowResistancePercent));
        }

    }

}
