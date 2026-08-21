package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Cell extends BaseSet {

    private int energyCostReduction;

    @Override
    public void init() {
        super.init();
        this.energyCostReduction = getValue("energyCostReduction", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "cell";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(energyCostReduction);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            for (AbstractAbility ability : warlordsPlayer.getAbilities()) {
                if (ability instanceof WeaponAbilityIcon) {
                    ability.getEnergyCost().addModifier(
                            FloatModifiable.ModifierType.ADDITIVE,
                            getName(),
                            -energyCostReduction
                    );
                }
            }
        }
    }
}
