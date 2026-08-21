package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.abilities.internal.Splash;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Stretch extends BaseSet {

    private int redRuneAbilityRangeIncreasePercent;

    @Override
    public void init() {
        super.init();
        this.redRuneAbilityRangeIncreasePercent = getValue("redRuneAbilityRangeIncreasePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "stretch";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(redRuneAbilityRangeIncreasePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            for (AbstractAbility ability : warlordsPlayer.getAbilities()) {
                if (ability instanceof RedAbilityIcon) {
                    if (ability instanceof HitBox hitBox) {
                        hitBox.getHitBoxRadius().addModifier(
                                FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                getName(),
                                1 + redRuneAbilityRangeIncreasePercent / 100f
                        );
                    }
                    if (ability instanceof Splash splash) {
                        splash.getSplashRadius().addModifier(
                                FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                getName(),
                                1 + redRuneAbilityRangeIncreasePercent / 100f
                        );
                    }
                }
            }
        }

    }

}