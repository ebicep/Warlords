package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.TimeWarpCryomancer;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.KnockbackInstance;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import java.util.List;

public class SteadfastWarp implements SpecBoostManager.SpecBoost<SteadfastWarp> {

    private int recastDelayTicks;
    private int kbResistanceLossOnRecastTicks;

    @Override
    public void init() {
        this.recastDelayTicks = getValue("recastDelayTicks", int.class);
        this.kbResistanceLossOnRecastTicks = getValue("kbResistanceLossOnRecastTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "steadfastWarp";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(recastDelayTicks, kbResistanceLossOnRecastTicks);
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
        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {
        }

        @EventHandler
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
                } else {
                    warlordsEntity.addInstance(InstanceBuilder
                            .healing()
                            .cause(getStringName())
                            .source(warlordsEntity)
                            .value(preWarpHealing));
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
                        warlordsEntity.getCooldownManager().addCooldown(new RegularCooldown<>(
                                getStringName(),
                                null,
                                SteadfastWarp.Boost.class,
                                null,
                                warlordsEntity,
                                CooldownTypes.SPEC_BOOST,
                                cooldownManager -> {},
                                remainingKbResTickDuration
                        ) {
                            @Override
                            public void multiplyKB(Vector currentVector) {
                                currentVector.zero();
                            }
                        });
                    },
                    false,
                    secondaryAbility -> !warlordsEntity.getCooldownManager().hasCooldown(regularCooldown)
            );
            cooldown.addExtraKnockbackInstance(new KnockbackInstance() {
                @Override
                public void multiplyKB(Vector currentVector) {
                    currentVector.zero();
                }
            });
        }

    }

}