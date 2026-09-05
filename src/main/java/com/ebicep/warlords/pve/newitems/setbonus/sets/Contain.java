package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractPiercingProjectile;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.abilities.internal.Splash;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Contain extends BaseSet {

    private int primaryAbilityRangeIncreasePercent;

    @Override
    public void init() {
        super.init();
        this.primaryAbilityRangeIncreasePercent = getValue("primaryAbilityRangeIncreasePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "contain";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(primaryAbilityRangeIncreasePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            float rangeMultiplier = 1 + primaryAbilityRangeIncreasePercent / 100f;
            for (AbstractAbility ability : warlordsPlayer.getAbilities()) {
                if (!(ability instanceof WeaponAbilityIcon)) {
                    continue;
                }
                if (ability instanceof AbstractPiercingProjectile<?, ?> projectile) {
                    projectile.getMaxDistance().addModifier(
                            FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                            getName(),
                            rangeMultiplier
                    );
                } else if (ability instanceof HitBox hitBox) {
                    hitBox.getHitBoxRadius().addModifier(
                            FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                            getName(),
                            rangeMultiplier
                    );
                }
                if (ability instanceof Splash splash) {
                    splash.getSplashRadius().addModifier(
                            FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                            getName(),
                            rangeMultiplier
                    );
                }
            }
        }

    }

}
