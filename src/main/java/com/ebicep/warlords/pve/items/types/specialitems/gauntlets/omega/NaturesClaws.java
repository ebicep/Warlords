package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;

import java.util.Set;

public class NaturesClaws extends SpecialOmegaGauntlet implements AppliesToWarlordsPlayer {
    public NaturesClaws() {

    }

    public NaturesClaws(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getDescription() {
        return "Survival of the fittest, at it's finest.";
    }

    @Override
    public String getBonus() {
        return "+10% cooldown reduction on all abilities.";
    }

    @Override
    public String getName() {
        return "Nature's Claws";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        for (AbstractAbility ability : warlordsPlayer.getAbilities()) {
            ability.getCooldown().addMultiplicativeModifierAdd(getName(), -0.1f);
        }
    }


}
