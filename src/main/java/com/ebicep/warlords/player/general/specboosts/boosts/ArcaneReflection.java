package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.internal.AbstractArcaneShield;
import com.ebicep.warlords.abilities.WaterBolt;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.event.EventHandler;

import java.util.List;

public class ArcaneReflection implements SpecBoostManager.SpecBoost<ArcaneReflection> {

    private int arcaneShieldDurationIncreaseTicks;
    private int arcaneShieldSpeedPercent;
    private float damageReflectionPercent;
    private float meleeDamageIncreasePercent;
    private float waterBoltDamageIncreasePercent;

    @Override
    public void init() {
        this.arcaneShieldDurationIncreaseTicks = getValue("arcaneShieldDurationIncreaseTicks", int.class);
        this.arcaneShieldSpeedPercent = getValue("arcaneShieldSpeedPercent", int.class);
        this.damageReflectionPercent = getValue("damageReflectionPercent", float.class);
        this.meleeDamageIncreasePercent = getValue("meleeDamageIncreasePercent", float.class);
        this.waterBoltDamageIncreasePercent = getValue("waterBoltDamageIncreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "arcaneReflection";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(arcaneShieldDurationIncreaseTicks, arcaneShieldSpeedPercent, damageReflectionPercent, meleeDamageIncreasePercent, waterBoltDamageIncreasePercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public ArcaneReflection get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(AbstractArcaneShield.class).forEach(arcaneShield -> {
                arcaneShield.setTickDuration(arcaneShield.getTickDuration() + arcaneShieldDurationIncreaseTicks);
            });
            warlordsPlayer.getAbilitiesMatching(WaterBolt.class).forEach(waterBolt ->
                    waterBolt.getDamageValues()
                             .getBoltDamage()
                             .forEachValue(floatModifiable -> floatModifiable.addMultiplicativeModifierAdd("Arcane Reflection", waterBoltDamageIncreasePercent / 100))
            );
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getStringName(),
                    null,
                    Boost.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.SPEC_BOOST,
                    cooldownManager -> {
                    },
                    false
            ) {
                @Override
                public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    if (event.getCause().isEmpty()) {
                        return currentDamageValue * AbstractAbility.convertToMultiplicationDecimal(meleeDamageIncreasePercent);
                    }
                    return currentDamageValue;
                }

                @Override
                public void onShieldFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                    if (event.getFlags().contains(InstanceFlags.RECURSIVE)) {
                        return;
                    }
                    WarlordsEntity attacker = event.getSource();
                    attacker.addInstance(InstanceBuilder
                            .damage()
                            .cause(getStringName())
                            .source(from)
                            .value(currentDamageValue * damageReflectionPercent / 100)
                            .showAsCrit(isCrit)
                            .flags(InstanceFlags.RECURSIVE, InstanceFlags.REFLECTIVE_DAMAGE)
                    );
                    Utils.playGlobalSound(warlordsPlayer.getLocation(), "warrior.intervene.block", 2, 2);
                }
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!(cooldown instanceof RegularCooldown<?> regularCooldown) ||
                    !(cooldown.getName().equals("Arcane Shield")) ||
                    !cooldown.getFrom().equals(warlordsEntity)
            ) {
                return;
            }
            warlordsEntity.addSpeedModifier(warlordsEntity, getStringName(), arcaneShieldSpeedPercent, regularCooldown);
        }

    }

}