package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.MysticalBarrier;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.DamageInstance;
import org.bukkit.event.EventHandler;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class PactOfProtection implements SpecBoostManager.SpecBoost<PactOfProtection> {

    private float mysticalBarrierCooldownReductionPercent;
    private float targetDamageReductionPercent;
    private float selfDamageIncreasePercent;

    @Override
    public void init() {
        this.mysticalBarrierCooldownReductionPercent = getValue("mysticalBarrierCooldownReductionPercent", float.class);
        this.targetDamageReductionPercent = getValue("targetDamageReductionPercent", float.class);
        this.selfDamageIncreasePercent = getValue("selfDamageIncreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "pactOfProtection";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(mysticalBarrierCooldownReductionPercent, targetDamageReductionPercent, selfDamageIncreasePercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public PactOfProtection get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(MysticalBarrier.class).forEach(mysticalBarrier -> {
                mysticalBarrier.getCooldown().addMultiplicativeModifierAdd("Spec Boost", -mysticalBarrierCooldownReductionPercent / 100);
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            if (warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!(cooldown instanceof RegularCooldown<?> regularCooldown)) {
                return;
            }
            if (!cooldown.getCooldownClass().equals(MysticalBarrier.class) || !cooldown.getFrom().equals(warlordsEntity)) {
                return;
            }
            regularCooldown.addExtraDamageInstance(new DamageInstance() {

                @Override
                public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * AbstractAbility.convertToDivisionDecimal(targetDamageReductionPercent);
                }

            });
            RegularCooldown<Boost> pactCooldown = new RegularCooldown<>(
                    getStringName(),
                    "PACT",
                    Boost.class,
                    null,
                    warlordsEntity,
                    CooldownTypes.SPEC_BOOST,
                    cooldownManager -> {
                    },
                    regularCooldown.getTicksLeft(),
                    Collections.singletonList((cd, ticksLeft, ticksElapsed) -> {
                        cd.setTicksLeft(regularCooldown.getTicksLeft());
                    })
            ) {
                @Override
                public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * AbstractAbility.convertToMultiplicationDecimal(selfDamageIncreasePercent);
                }
            };
            Consumer<CooldownManager> oldOnRemoveForce = regularCooldown.getOnRemoveForce();
            regularCooldown.setOnRemoveForce(cooldownManager -> {
                oldOnRemoveForce.accept(cooldownManager);
                warlordsEntity.getCooldownManager().removeCooldown(pactCooldown);
            });
            warlordsEntity.getCooldownManager().addCooldown(pactCooldown);

        }

    }

}
