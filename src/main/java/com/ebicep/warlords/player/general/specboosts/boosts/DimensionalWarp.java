package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.TimeWarpPyromancer;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFlag;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.DamageInstance;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;

public class DimensionalWarp implements SpecBoostManager.SpecBoost<DimensionalWarp> {

    private int maxEnergyGain;
    private float healthRestorePercent;
    private int ticks;
    private float speedIncrease;
    private float damageIncrease;

    @Override
    public void init() {
        this.maxEnergyGain = getValue("maxEnergyGain", int.class);
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
                maxEnergyGain,
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

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getEnergy().addAdditiveModifier("Spec Boost", maxEnergyGain);
            warlordsPlayer.getAbilitiesMatching(TimeWarpPyromancer.class).forEach(timeWarp -> {
                timeWarp.setTickDuration(timeWarp.getTickDuration() - ticks);
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!(cooldown.getCooldownObject() instanceof TimeWarpPyromancer.TimeWarpPyromancerData data) || !cooldown.getFrom().equals(warlordsEntity)) {
                return;
            }
            cooldown.getFlags().add(CooldownFlag.CANNOT_BE_REDUCED_VIND);
            warlordsEntity.addSpeedModifier(warlordsEntity, getStringName(), speedIncrease, cooldown);
            cooldown.addExtraDamageInstance(new DamageInstance() {
                @Override
                public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * AbstractAbility.convertToMultiplicationDecimal(damageIncrease);
                }
            });
        }

        @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
        public void onDeath(WarlordsDeathEvent event) {
            if (event.getWarlordsEntity().equals(warlordsEntity)) {
                new CooldownFilter<>(warlordsEntity, RegularCooldown.class)
                        .filterCooldownClass(TimeWarpPyromancer.TimeWarpPyromancerData.class)
                        .forEach(cooldown -> {
                            if (cooldown.getCooldownObject() instanceof TimeWarpPyromancer.TimeWarpPyromancerData data) {
                                warlordsEntity.setCurrentHealth(0.1f);
                                data.setWarpHeal(() -> {
                                            warlordsEntity.addInstance(InstanceBuilder
                                                    .healing()
                                                    .cause(getStringName())
                                                    .source(warlordsEntity)
                                                    .value(warlordsEntity.getMaxBaseHealth() * (healthRestorePercent / 100f)));
                                        }
                                );
                                cooldown.expire(warlordsEntity.getCooldownManager());
                            }
                            Utils.playGlobalSound(warlordsEntity.getLocation(), Sound.ITEM_TOTEM_USE, 2, 2f);
                            warlordsEntity.getCooldownManager().removeCooldown(cooldown);
                            event.setCancelled(true);
                        });
            }
        }

    }

}