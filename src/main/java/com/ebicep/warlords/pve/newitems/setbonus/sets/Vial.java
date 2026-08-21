package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Heals;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Vial extends BaseSet {

    private int redRuneAbilityHealingIncreasePercent;

    @Override
    public void init() {
        super.init();
        this.redRuneAbilityHealingIncreasePercent = getValue("redRuneAbilityHealingIncreasePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "vial";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(redRuneAbilityHealingIncreasePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            for (AbstractAbility ability : warlordsPlayer.getAbilities()) {
                if (ability instanceof RedAbilityIcon && ability instanceof Heals<?> heals) {
                    heals.getHealValues().getValues().forEach(healValue -> {
                        healValue.forEachAllValues(
                                value -> value.addModifier(
                                        FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                        getName(),
                                        1 + redRuneAbilityHealingIncreasePercent / 100f)
                        );
                    });
                }
            }
        }

    }

}