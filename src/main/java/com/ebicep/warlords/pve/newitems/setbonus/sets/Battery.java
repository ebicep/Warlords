package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Battery extends BaseSet {

    private int redRuneAbilityEnergyCostReduction;

    @Override
    public void init() {
        super.init();
        this.redRuneAbilityEnergyCostReduction = getValue("redRuneAbilityEnergyCostReduction", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "battery";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(redRuneAbilityEnergyCostReduction);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            for (AbstractAbility ability : warlordsPlayer.getAbilities()) {
                if (ability instanceof RedAbilityIcon) {
                    ability.getEnergyCost().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Battery Item",  -redRuneAbilityEnergyCostReduction);
                }
            }
        }

    }

}