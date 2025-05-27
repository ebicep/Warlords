package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.TimeWarpPyromancer;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.DamageInstance;
import org.bukkit.event.EventHandler;

import java.util.List;

public class DimensionalWarp implements SpecBoostManager.SpecBoost<DimensionalWarp> {

    private float healthRestorePercent;
    private int ticks;
    private float speedIncrease;
    private float damageIncrease;

    @Override
    public void init() {
        this.healthRestorePercent = getValue("healthRestorePercent", float.class);
        this.ticks = getValue("ticks", int.class);
        this.speedIncrease = getValue("speedIncrease", float.class);
        this.damageIncrease = getValue("damageIncrease", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "dimensionalWarp";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                healthRestorePercent,
                ticks,
                speedIncrease,
                damageIncrease
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public DimensionalWarp get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;
        private int previousTickDuration;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(TimeWarpPyromancer.class).forEach(timeWarp -> {
                previousTickDuration = timeWarp.getTickDuration();
                timeWarp.setTickDuration(ticks);
            });
        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(TimeWarpPyromancer.class).forEach(timeWarp -> {
                timeWarp.setTickDuration(previousTickDuration);
            });
        }

        @EventHandler
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!(cooldown.getCooldownObject() instanceof TimeWarpPyromancer.TimeWarpPyromancerData data) || !cooldown.getFrom().equals(warlordsEntity)) {
                return;
            }
            warlordsEntity.addSpeedModifier(warlordsEntity, getStringName(), speedIncrease, ticks);
            cooldown.addExtraDamageInstance(new DamageInstance() {
                @Override
                public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * AbstractAbility.convertToMultiplicationDecimal(damageIncrease);
                }

                @Override
                public float modifyDamageAfterAllFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                    if (warlordsEntity.getCurrentHealth() - currentDamageValue < 0) {
                        data.setWarpHeal(() -> warlordsEntity.addInstance(InstanceBuilder
                                .healing()
                                .cause(getStringName())
                                .source(warlordsEntity)
                                .value(warlordsEntity.getMaxBaseHealth() * (healthRestorePercent / 100f)))
                        );
                        warlordsEntity.getCooldownManager().removeCooldown(cooldown);
                    }
                    return currentDamageValue;
                }
            });
        }

    }

}