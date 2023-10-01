package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega;

import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;

import java.util.Set;

public class HandsOfTheHolyCorpse extends SpecialOmegaGauntlet implements AppliesToWarlordsPlayer {

    public HandsOfTheHolyCorpse() {

    }

    public HandsOfTheHolyCorpse(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getDescription() {
        return "Let there be light.";
    }

    @Override
    public String getBonus() {
        return "+15 Energy per hit.";
    }

    @Override
    public String getName() {
        return "Hands of the Holy Corpse";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        AbstractPlayerClass playerClass = warlordsPlayer.getSpec();
        playerClass.setEnergyPerHit(playerClass.getEnergyPerHit() + 15);
    }
}
