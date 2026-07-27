package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.abilities.internal.Splash;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Lean extends BaseSet {

    private int orangeRuneAbilityRangeIncreasePercent;

    @Override
    public void init() {
        super.init();
        this.orangeRuneAbilityRangeIncreasePercent = getValue("orangeRuneAbilityRangeIncreasePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "lean";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(orangeRuneAbilityRangeIncreasePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            for (AbstractAbility ability : warlordsPlayer.getAbilities()) {
                if (!(ability instanceof OrangeAbilityIcon)) {
                    continue;
                }
                if (ability instanceof HitBox hitBox) {
                    hitBox.getHitBoxRadius().addModifier(
                            FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                            getName(),
                            1 + orangeRuneAbilityRangeIncreasePercent / 100f
                    );
                }
                if (ability instanceof Splash splash) {
                    splash.getSplashRadius().addModifier(
                            FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                            getName(),
                            1 + orangeRuneAbilityRangeIncreasePercent / 100f
                    );
                }
            }
        }

    }

}
