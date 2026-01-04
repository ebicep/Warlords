package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Omamori extends BaseSet {

    private float ultimateDurationIncreaseSeconds;

    @Override
    public void init() {
        super.init();
        this.ultimateDurationIncreaseSeconds = getValue("ultimateDurationIncreaseSeconds", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "omamori";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(ultimateDurationIncreaseSeconds);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            for (var ability : warlordsPlayer.getAbilities()) {
                if (ability instanceof OrangeAbilityIcon && ability instanceof Duration duration) {
                    duration.setTickDuration((int) (duration.getTickDuration() + ultimateDurationIncreaseSeconds * 20));
                }
            }
        }

    }

}