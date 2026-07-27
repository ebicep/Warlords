package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Axe extends BaseSet {

    private int purpleRuneCooldownReductionPercent;

    @Override
    public void init() {
        super.init();
        this.purpleRuneCooldownReductionPercent = getValue("purpleRuneCooldownReductionPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "axe";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(purpleRuneCooldownReductionPercent);
    }

    public class Bonus implements SetBonus.Bonus {
        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            for (AbstractAbility ability : warlordsPlayer.getAbilities()) {
                if (ability instanceof PurpleAbilityIcon) {
                    ability.getCooldown().addModifier(
                            FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                            getName(),
                            1 - purpleRuneCooldownReductionPercent / 100f
                    );
                }
            }
        }
    }
}
