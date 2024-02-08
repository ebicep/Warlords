package com.ebicep.warlords.pve.items.types.specialitems.buckler.omega;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;

import java.util.Set;

public class LovelyOmamori extends SpecialOmegaBuckler implements AppliesToWarlordsPlayer {

    public LovelyOmamori() {
    }

    public LovelyOmamori(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getDescription() {
        return "How many items? 10? Ugh.";
    }

    @Override
    public String getBonus() {
        return "Increase the duration of your ultimate ability by 3 seconds if applicable.";
    }

    @Override
    public String getName() {
        return "Lovely Omamori";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        for (AbstractAbility ability : warlordsPlayer.getAbilities()) {
            if (ability instanceof OrangeAbilityIcon && ability instanceof Duration duration) {
                duration.setTickDuration(duration.getTickDuration() + 60);
            }
        }
    }

}
