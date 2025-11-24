package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.HealingTotem;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFlag;
import com.ebicep.warlords.player.ingame.instances.type.CustomInstanceFlags;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.event.EventHandler;

import java.util.Comparator;
import java.util.List;

public class TotemicBoon implements SpecBoostManager.SpecBoost<TotemicBoon> {

    private int healingTotemHealingDecreasePercent;
    private int healingTotemEnergyDecreasePercent;
    private float healingTotemCooldownDecreasePercent;
    private int healingTotemMaxAbilityCharges;
    private float healingTotemSpeedMultiplier;
    private float healingTotemRadiusIncrease;
    private float healthTransferThresholdPercent;

    @Override
    public void init() {
        this.healingTotemHealingDecreasePercent = getValue("healingTotemHealingDecreasePercent", int.class);
        this.healingTotemEnergyDecreasePercent = getValue("healingTotemEnergyDecreasePercent", int.class);
        this.healingTotemCooldownDecreasePercent = getValue("healingTotemCooldownDecreasePercent", float.class);
        this.healingTotemMaxAbilityCharges = getValue("healingTotemMaxAbilityCharges", int.class);
        this.healingTotemSpeedMultiplier = getValue("healingTotemSpeedMultiplier", float.class);
        this.healingTotemRadiusIncrease = getValue("healingTotemRadiusIncrease", float.class);
        this.healthTransferThresholdPercent = getValue("healthTransferThresholdPercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "totemicBoon";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healingTotemHealingDecreasePercent,
                healingTotemEnergyDecreasePercent,
                healingTotemCooldownDecreasePercent,
                healingTotemMaxAbilityCharges,
                healingTotemSpeedMultiplier,
                healingTotemRadiusIncrease,
                healthTransferThresholdPercent
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public TotemicBoon get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(HealingTotem.class).forEach(healingTotem -> {
                healingTotem.getHealValues()
                            .getTotemHealing()
                            .forEachValue(floatModifiable -> floatModifiable.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE,
                                    "Spec Boost",
                                    -healingTotemHealingDecreasePercent / 100f
                            ));
                healingTotem.getEnergyCost().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Spec Boost", -healingTotemEnergyDecreasePercent / 100f);
                healingTotem.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Spec Boost", -healingTotemCooldownDecreasePercent / 100f);
                healingTotem.setMaxCharges(healingTotemMaxAbilityCharges);
                healingTotem.setCurrentCharges(healingTotemMaxAbilityCharges);
                healingTotem.setHealingPeriod((int) (healingTotem.getHealingPeriod() / healingTotemSpeedMultiplier));
                healingTotem.setTickDuration((int) (healingTotem.getTickDuration() / healingTotemSpeedMultiplier));
                healingTotem.getHitBoxRadius().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost", healingTotemRadiusIncrease);
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onDamageHealEvent(WarlordsDamageHealingEvent event) {
            if (!event.getSource().equals(warlordsEntity)) {
                return;
            }
            if (!(event.getAbility() instanceof HealingTotem)) {
                return;
            }
            WarlordsEntity target = event.getWarlordsEntity();
            boolean aboveThreshold = target.getCurrentHealth() / target.getMaxBaseHealth() > healthTransferThresholdPercent / 100f;
            if (!aboveThreshold) {
                return;
            }
            for (CustomInstanceFlags customFlag : event.getCustomFlags()) {
                if (customFlag instanceof CustomInstanceFlags.PlayersEffectedInstanceFlag(List<WarlordsEntity> players)) {
                    players.stream()
                           .min(Comparator.comparingDouble(WarlordsEntity::getCurrentHealth))
                           .ifPresent(event::setPlayer);
                    return;
                }
            }
        }

        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!(cooldown.getCooldownObject() instanceof HealingTotem.HealingTotemData data) || !cooldown.getFrom().equals(warlordsEntity)) {
                return;
            }
            cooldown.getFlags().add(CooldownFlag.CANNOT_BE_REDUCED_VIND);
        }

    }

}
