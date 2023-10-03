package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;

import java.util.Set;

public class LilithsClaws extends SpecialOmegaGauntlet implements AppliesToWarlordsPlayer {
    public LilithsClaws() {

    }

    public LilithsClaws(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getName() {
        return "Lilith's Claws";
    }

    @Override
    public String getBonus() {
        return "Prevents you from earning coins, but increases all other drops.";
    }

    @Override
    public String getDescription() {
        return "Might as well condemn everyone, right?";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        //TODO
    }
}
