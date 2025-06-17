package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.IceBarrier;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.motionsystem.MotionModifierBuilder;
import com.ebicep.warlords.player.ingame.motionsystem.speed.ConditionalStackValueModifier;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.event.EventHandler;

import java.util.List;

public class ChillyAura implements SpecBoostManager.SpecBoost<ChillyAura> {

    private float rangeBlocks;
    private float slowAmountPercent;
    private float healthLossPercent;
    private int healthLossTickPeriod;

    @Override
    public void init() {
        this.rangeBlocks = getValue("rangeBlocks", float.class);
        this.slowAmountPercent = getValue("slowAmountPercent", float.class);
        this.healthLossPercent = getValue("healthLossPercent", float.class);
        this.healthLossTickPeriod = getValue("healthLossTickPeriod", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "chillyAura";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(rangeBlocks, slowAmountPercent, healthLossPercent, healthLossTickPeriod);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public ChillyAura get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;

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
            if (!cooldown.getCooldownClass().equals(IceBarrier.IceBarrierData.class) || !cooldown.getFrom().equals(warlordsEntity)) {
                return;
            }
            regularCooldown.addTriConsumer((cd, ticksLeft, ticksElapsed) -> {
                if (ticksElapsed % healthLossTickPeriod == 0) {
                    PlayerFilter.entitiesAround(warlordsEntity, rangeBlocks, rangeBlocks, rangeBlocks)
                                .aliveEnemiesOf(warlordsEntity)
                                .forEach(we -> {
                                    we.addSpeedModifier(warlordsEntity, getStringName(), -slowAmountPercent, 6);
                                    we.addSpeedModifier(new MotionModifierBuilder()
                                            .setFrom(warlordsEntity)
                                            .setName(getStringName())
                                            .setModifier(-slowAmountPercent)
                                            .setDuration(6)
                                            .addAddons(new ConditionalStackValueModifier(AbstractAbility.convertToDivisionDecimal(slowAmountPercent)))
                                            .build()
                                    );
                                    float damage = we.getMaxHealth() * healthLossPercent / 100;
                                    we.addInstance(InstanceBuilder
                                            .damage()
                                            .cause(getStringName())
                                            .source(warlordsEntity)
                                            .value(damage)
                                    );
                                });
                }
            });
        }

    }

}