package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Detonator extends BaseSet {

    private int redRuneAbilityDamageIncreasePercent;

    @Override
    public void init() {
        super.init();
        this.redRuneAbilityDamageIncreasePercent = getValue("redRuneAbilityDamageIncreasePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "detonator";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(redRuneAbilityDamageIncreasePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            for (AbstractAbility ability : warlordsPlayer.getAbilities()) {
                if (ability instanceof RedAbilityIcon && ability instanceof Damages<?>) {
                    ((Damages<?>) ability).getDamageValues().getValues().forEach(damageValue -> {
                        damageValue.forEachAllValues(
                                value -> value.addModifier(
                                        FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                        getName(),
                                        1 + redRuneAbilityDamageIncreasePercent / 100f));
                    });
                }
            }
        }
    }
}