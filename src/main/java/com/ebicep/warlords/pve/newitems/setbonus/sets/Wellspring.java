package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Wellspring extends BaseSet {

    private int blueRuneCooldownReductionPercent;

    @Override
    public void init() {
        super.init();
        this.blueRuneCooldownReductionPercent = getValue("blueRuneCooldownReductionPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "wellspring";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(blueRuneCooldownReductionPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            for (AbstractAbility ability : warlordsPlayer.getAbilities()) {
                if (ability instanceof BlueAbilityIcon) {
                    ability.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getName(),  1 - blueRuneCooldownReductionPercent / 100f);
                }
            }
        }

    }

}