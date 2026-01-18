package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Multiply extends BaseSet {

    private int maxEnergyRequirement;
    private int critMultiplierIncreasePercent;

    @Override
    public void init() {
        super.init();
        this.maxEnergyRequirement = getValue("maxEnergyRequirement", int.class);
        this.critMultiplierIncreasePercent = getValue("critMultiplierIncreasePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "multiply";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(maxEnergyRequirement, critMultiplierIncreasePercent);
    }

    public class Bonus implements SetBonus.Bonus {
        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Multiply.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false
            ).addModifier(
                    Modifier.MODIFY_OUTGOING_CRIT_MULTIPLIER,
                    (event, currentCritMultiplier) -> {
                        int stacks = (int) (warlordsPlayer.getMaxEnergy() / maxEnergyRequirement);
                        if (stacks <= 0) {
                            return;
                        }

                        currentCritMultiplier.addModifier(
                                FloatModifiable.ModifierType.ADDITIVE,
                                getName(),
                                critMultiplierIncreasePercent
                        );
                    }
            ));

        }

    }

}
