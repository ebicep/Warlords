package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.Repentance;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import org.bukkit.event.EventHandler;

import java.util.Collections;
import java.util.List;

public class PenitentResolve implements SpecBoostManager.SpecBoost<PenitentResolve> {

    private int healthIncrease;
    private int repentanceHealingPerSecond;
    private int repentanceHealingDurationTicks;

    @Override
    public void init() {
        this.healthIncrease = getValue("healthIncrease", int.class);
        this.repentanceHealingPerSecond = getValue("repentanceHealingPerSecond", int.class);
        this.repentanceHealingDurationTicks = getValue("repentanceHealingDurationTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "penitentResolve";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healthIncrease, repentanceHealingPerSecond, repentanceHealingDurationTicks);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public PenitentResolve get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getHealth().addAdditiveModifier("Spec Boost", healthIncrease);
        }

        @EventHandler
        public void onWarlordsAbilityActivate(WarlordsAbilityActivateEvent.Post event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            if (event.getAbility() instanceof Repentance) {
                warlordsEntity.getCooldownManager().addCooldown(new RegularCooldown<>(
                        getStringName(),
                        "RESOLVE",
                        Boost.class,
                        null,
                        warlordsEntity,
                        CooldownTypes.SPEC_BOOST,
                        cooldownManager -> {
                        },
                        repentanceHealingDurationTicks,
                        Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                            if (ticksElapsed % 20 == 0) {
                                warlordsEntity.addInstance(InstanceBuilder
                                        .healing()
                                        .cause(getStringName())
                                        .source(warlordsEntity)
                                        .value(repentanceHealingPerSecond)
                                );
                            }
                        })
                ) {
                    @Override
                    public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        if (event.getCause().isEmpty()) {
                            setTicksLeft(0);
                        }
                        return currentDamageValue;
                    }
                });
            }
        }

    }

}
