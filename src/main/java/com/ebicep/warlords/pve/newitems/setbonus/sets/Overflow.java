package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.MultiFloatModifiable;

import java.util.List;

public class Overflow extends BaseSet {

    private int excessHealingHealthThresholdPercent;
    private int excessHealingEnergyCap;

    @Override
    public void init() {
        super.init();
        this.excessHealingHealthThresholdPercent = getValue("excessHealingHealthThresholdPercent", int.class);
        this.excessHealingEnergyCap = getValue("excessHealingEnergyCap", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "overflow";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(excessHealingHealthThresholdPercent, excessHealingEnergyCap);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Overflow.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {},
                    false
            ).addModifier(
                    Modifier.MODIFY_OUTGOING_HEALING,
                    (event, currentHealingValue) -> {
                        if (event.getWarlordsEntity().equals(warlordsPlayer)) {
                            return;
                        }
                        if (!event.getWarlordsEntity().getTeam().equals(warlordsPlayer.getTeam())) {
                            return;
                        }

                        float targetCurrentHealth = event.getWarlordsEntity().getCurrentHealth();
                        float targetMaxHealth = event.getWarlordsEntity().getMaxHealth();
                        float targetHealthPercent = (targetCurrentHealth / targetMaxHealth) * 100f;

                        if (targetHealthPercent < excessHealingHealthThresholdPercent) {
                            return;
                        }

                        float energyToGain = getEnergyToGain(currentHealingValue, targetCurrentHealth, targetMaxHealth);
                        if (energyToGain > 0) {
                            warlordsPlayer.addEnergy(warlordsPlayer, getName(), energyToGain);
                        }
                    }
            ));

        }

    }

    private float getEnergyToGain(MultiFloatModifiable currentHealingValue, float targetCurrentHealth, float targetMaxHealth) {
        float healingAmount = currentHealingValue.getCalculatedValue();
        float healthAfterHealing = targetCurrentHealth + healingAmount;

        float excessHealing;
        if (healthAfterHealing > targetMaxHealth) {
            excessHealing = healthAfterHealing - targetMaxHealth;
        } else {
            float healthAboveThreshold = targetCurrentHealth - (targetMaxHealth * (excessHealingHealthThresholdPercent / 100f));
            if (healthAboveThreshold > 0) {
                float thresholdRatio = healthAboveThreshold / (targetMaxHealth * ((100f - excessHealingHealthThresholdPercent) / 100f));
                excessHealing = healingAmount * thresholdRatio;
            } else {
                excessHealing = 0;
            }
        }

        return Math.min(excessHealing, excessHealingEnergyCap);
    }

}