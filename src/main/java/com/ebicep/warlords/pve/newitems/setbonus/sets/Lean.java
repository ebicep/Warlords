package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.abilities.internal.Splash;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Lean extends BaseSet {

    private int blueRuneAbilityRangeIncreasePercent;

    @Override
    public void init() {
        super.init();
        this.blueRuneAbilityRangeIncreasePercent = getValue("blueRuneAbilityRangeIncreasePercent", int.class);
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
        return List.of(blueRuneAbilityRangeIncreasePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            for (AbstractAbility ability : warlordsPlayer.getAbilities()) {
                if (ability instanceof OrangeAbilityIcon) {
                    if (ability instanceof HitBox hitBox) {
                        hitBox.getHitBoxRadius().addModifier(
                                FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                getName(),
                                1 + blueRuneAbilityRangeIncreasePercent / 100f
                        );
                    }
                    if (ability instanceof Splash splash) {
                        splash.getSplashRadius().addModifier(
                                FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                getName(),
                                1 + blueRuneAbilityRangeIncreasePercent / 100f
                        );
                    }
                }
            }
        }
    }
}
