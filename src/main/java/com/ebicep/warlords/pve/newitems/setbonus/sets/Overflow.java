package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Overflow extends BaseSet {

    private int excessHealingConversionPercent;
    private int excessHealingHealthThresholdPercent;
    private int excessHealingEnergyCap;

    @Override
    public void init() {
        super.init();
        this.excessHealingConversionPercent = getValue("excessHealingConversionPercent", int.class);
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
        return List.of(excessHealingConversionPercent, excessHealingHealthThresholdPercent, excessHealingEnergyCap);
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

                        float energyToGain = energyFromExcessHealing(
                                currentHealingValue.getCalculatedValue(),
                                event.getWarlordsEntity().getCurrentHealth(),
                                event.getWarlordsEntity().getMaxHealth(),
                                excessHealingHealthThresholdPercent,
                                excessHealingConversionPercent,
                                excessHealingEnergyCap
                        );
                        if (energyToGain > 0) {
                            warlordsPlayer.addEnergy(warlordsPlayer, getName(), energyToGain);
                        }
                    }
            ));

        }

    }

    static float energyFromExcessHealing(
            float healingAmount,
            float targetCurrentHealth,
            float targetMaxHealth,
            int healthThresholdPercent,
            int conversionPercent,
            int energyCap
    ) {
        if (healingAmount <= 0 || targetMaxHealth <= 0 || conversionPercent <= 0 || energyCap <= 0) {
            return 0;
        }

        float thresholdHealth = targetMaxHealth * (healthThresholdPercent / 100f);
        float healthAfterHealing = targetCurrentHealth + healingAmount;
        float excessHealing = Math.max(0f, healthAfterHealing - thresholdHealth) - Math.max(0f, targetCurrentHealth - thresholdHealth);
        if (excessHealing <= 0) {
            return 0;
        }

        return Math.min(excessHealing * (conversionPercent / 100f), energyCap);
    }

}
