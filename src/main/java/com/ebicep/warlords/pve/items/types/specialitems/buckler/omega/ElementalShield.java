package com.ebicep.warlords.pve.items.types.specialitems.buckler.omega;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;

import java.util.Set;

public class ElementalShield extends SpecialOmegaBuckler implements AppliesToWarlordsPlayer {

    public ElementalShield() {
    }

    public ElementalShield(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getDescription() {
        return "Not too sure how to hold this, good luck!";
    }

    @Override
    public String getBonus() {
        return "+12.5% Max HP.";
    }

    @Override
    public String getName() {
        return "Elemental Shield";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getHealth().addAdditiveModifier(getName(), 0.125f);
    }

}
