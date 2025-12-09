package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.MysticalBarrier;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
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
                mysticalBarrier.getCooldown().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "Spec Boost", -mysticalBarrierCooldownReductionPercent / 100);
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!(cooldown instanceof RegularCooldown<?> regularCooldown)) {
                return;
            }
            if (!cooldown.getCooldownClass().equals(MysticalBarrier.class) || !cooldown.getFrom().equals(warlordsEntity)) {
                return;
            }
            regularCooldown.addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (e, currentDamageValue) -> {
                currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                        getStringName(),
                        AbstractAbility.convertToDivisionDecimal(targetDamageReductionPercent)
                );
                    }
            );
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
            );
            pactCooldown.addModifier(Modifier.INCOMING_DAMAGE_BEFORE_INTERVENE, (e, currentDamageValue) -> {
                currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                        getStringName(),
                        AbstractAbility.convertToMultiplicationDecimal(selfDamageIncreasePercent)
                );
                    }
            );
            Consumer<CooldownManager> oldOnRemoveForce = regularCooldown.getOnRemoveForce();
            regularCooldown.setOnRemoveForce(cooldownManager -> {
                oldOnRemoveForce.accept(cooldownManager);
                warlordsEntity.getCooldownManager().removeCooldown(pactCooldown);
            });
            warlordsEntity.getCooldownManager().addCooldown(pactCooldown);

        }

    }

}
