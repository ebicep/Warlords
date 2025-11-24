package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.FreezingBreath;
import com.ebicep.warlords.abilities.TimeSurge;
import com.ebicep.warlords.abilities.TimeWarpCryomancer;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractArcaneShield;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsPlayerStunEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BlizzardBreath implements SpecBoostManager.SpecBoost<BlizzardBreath> {

    private float cooldownReductionPerEnemyHitPercent;
    private int immunityDurationTicks;
    private int productionValuesDecreasePercent;

    @Override
    public void init() {
        this.cooldownReductionPerEnemyHitPercent = getValue("cooldownReductionPerEnemyHitPercent", float.class);
        this.immunityDurationTicks = getValue("immunityDurationTicks", int.class);
        this.productionValuesDecreasePercent = getValue("productionValuesDecreasePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "blizzardBreath";
    }

    @Override
    public TextComponent getDescription() {
        return appendAbility(getTextDescription(), new com.ebicep.warlords.abilities.TimeSurge());
    }

    @Override
    public List<Object> getVariables() {
        return List.of(cooldownReductionPerEnemyHitPercent, immunityDurationTicks, productionValuesDecreasePercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public BlizzardBreath get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private final Map<UUID, Integer> breathTargetsHit = new HashMap<>();
        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            List<AbstractAbility> abilities = warlordsPlayer.getAbilities();
            for (int i = 0; i < abilities.size(); i++) {
                if (abilities.get(i) instanceof TimeWarpCryomancer) {
                    TimeSurge timeSurge = new TimeSurge();
                    timeSurge.init(timeSurge.getBuilder());
                    abilities.set(i, timeSurge);
                }
            }
            warlordsPlayer.resetAbilityTree();
            warlordsPlayer.getAbilitiesMatching(AbstractArcaneShield.class).forEach(arcaneShield -> {
                arcaneShield.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Spec Boost", -productionValuesDecreasePercent / 100.0f);
                arcaneShield.getEnergyCost().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Spec Boost", -productionValuesDecreasePercent / 100.0f);
                arcaneShield.setShieldPercentage(arcaneShield.getShieldPercentage() * AbstractAbility.convertToDivisionDecimal(productionValuesDecreasePercent));
                arcaneShield.updateCustomStats(warlordsPlayer);
            });
            warlordsPlayer.getAbilitiesMatching(TimeSurge.class).forEach(timeSurge -> {
                timeSurge.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Spec Boost", -productionValuesDecreasePercent / 100.0f);
                timeSurge.getEnergyCost().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Spec Boost", -productionValuesDecreasePercent / 100.0f);
                timeSurge.setHealPercentage(timeSurge.getHealPercentage() * AbstractAbility.convertToDivisionDecimal(productionValuesDecreasePercent));
            });
        }

        @EventHandler
        public void onDamageHealFinal(WarlordsDamageHealingFinalEvent event) {
            WarlordsDamageHealingEvent damageHealingEvent = event.getWarlordsDamageHealingEvent();
            if (!damageHealingEvent.getSource().equals(warlordsEntity)) {
                return;
            }
            if (!damageHealingEvent.getCause().equals("Freezing Breath")) {
                return;
            }
            UUID uuid = damageHealingEvent.getUUID();
            if (breathTargetsHit.containsKey(uuid)) {
                breathTargetsHit.compute(uuid, (k, hits) -> hits + 1);
            } else {
                breathTargetsHit.put(uuid, 1);
            }
        }

        @EventHandler
        public void onWarlordsAbilityActivatePostApplyEvent(WarlordsAbilityActivateEvent.PostApply event) {
            if (!event.getWarlordsEntity().equals(warlordsEntity)) {
                return;
            }
            if (!(event.getAbility() instanceof FreezingBreath)) {
                return;
            }
            RegularCooldown<Boost> breathCooldown = new RegularCooldown<>(
                    getStringName(),
                    null,
                    Boost.class,
                    null,
                    warlordsEntity,
                    CooldownTypes.SPEC_BOOST,
                    cooldownManager -> {},
                    immunityDurationTicks
            ) {
                @Override
                protected Listener getListener() {
                    return new Listener() {
                        @EventHandler
                        public void onWarlordsPlayerStunEvent(WarlordsPlayerStunEvent e) {
                            if (e.getWarlordsEntity().equals(warlordsEntity)) {
                                e.setCancelled(true);
                            }
                        }
                    };
                }
            };
            warlordsEntity.addKnockbackModifier(warlordsEntity, getStringName(), -100, breathCooldown);
            warlordsEntity.getCooldownManager().addCooldown(breathCooldown);
            if (breathTargetsHit.isEmpty()) {
                return;
            }
            int hits = breathTargetsHit.values().stream().mapToInt(Integer::intValue).sum();
            float reduction = hits * (cooldownReductionPerEnemyHitPercent / 100);
            for (AbstractAbility ability : warlordsEntity.getAbilities()) {
                ability.subtractCurrentCooldown(ability.getCooldownValue() * reduction);
            }
            breathTargetsHit.clear();
        }

    }


}