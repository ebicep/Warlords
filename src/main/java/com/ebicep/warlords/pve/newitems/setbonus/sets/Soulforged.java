package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Soulforged extends BaseSet {

    private int healthThreshold;
    private int energyPerSecondBonusPercent;
    private int energyRegenDisabledBelowHealthPercent;

    @Override
    public void init() {
        super.init();
        this.healthThreshold = getValue("healthThreshold", int.class);
        this.energyPerSecondBonusPercent = getValue("energyPerSecondBonusPercent", int.class);
        this.energyRegenDisabledBelowHealthPercent = getValue("energyRegenDisabledBelowHealthPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "soulforged";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healthThreshold, energyPerSecondBonusPercent, energyRegenDisabledBelowHealthPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Soulforged.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false
            ).addModifier(Modifier.ENERGY_GAIN_PER_TICK, energyGainPerTick -> {
                if (warlordsPlayer.isDead() || warlordsPlayer.getMaxHealth() <= 0) {
                    return;
                }
                float healthPercent = warlordsPlayer.getCurrentHealth() / warlordsPlayer.getMaxHealth() * 100f;
                if (healthPercent < energyRegenDisabledBelowHealthPercent) {
                    energyGainPerTick.addModifier(
                            FloatModifiable.ModifierType.OVERRIDING,
                            getName(),
                            0
                    );
                } else if (healthPercent >= healthThreshold) {
                    energyGainPerTick.addModifier(
                            FloatModifiable.ModifierType.ADDITIVE,
                            getName(),
                            energyPerSecondBonusPercent / 20f
                    );
                }
            }));
        }

    }

}
