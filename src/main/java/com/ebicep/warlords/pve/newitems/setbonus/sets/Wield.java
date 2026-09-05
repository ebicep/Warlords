package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Wield extends BaseSet {

    private int primaryAbilityDamageIncreasePercent;

    @Override
    public void init() {
        super.init();
        this.primaryAbilityDamageIncreasePercent = getValue("primaryAbilityDamageIncreasePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "wield";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(primaryAbilityDamageIncreasePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            for (AbstractAbility ability : warlordsPlayer.getAbilities()) {
                if (ability instanceof WeaponAbilityIcon && ability instanceof Damages<?> damages) {
                    damages.getDamageValues().getValues().forEach(damageValue -> {
                        damageValue.forEachAllValues(
                                value -> value.addModifier(
                                        FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                        getName(),
                                        1 + primaryAbilityDamageIncreasePercent / 100f
                                )
                        );
                    });
                }
            }
        }

    }

}
