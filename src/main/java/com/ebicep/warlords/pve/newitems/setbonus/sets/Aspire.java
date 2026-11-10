package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Aspire extends BaseSet {

    private int maxEnergyRequirement;
    private float energyPerSecondPerStack;

    @Override
    public void init() {
        super.init();
        this.maxEnergyRequirement = getValue("maxEnergyRequirement", int.class);
        this.energyPerSecondPerStack = getValue("energyPerSecondPerStack", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "aspire";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(maxEnergyRequirement, energyPerSecondPerStack);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Aspire.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false
            ).addModifier(Modifier.ENERGY_GAIN_PER_TICK, energyGainPerTick -> {
                int stacks = (int) (warlordsPlayer.getMaxEnergy() / maxEnergyRequirement);
                if (stacks > 0) {
                    energyGainPerTick.addModifier(
                            FloatModifiable.ModifierType.ADDITIVE,
                            getName(),
                            energyPerSecondPerStack * stacks / 20f
                    );
                }
            }));
        }
    }

}
