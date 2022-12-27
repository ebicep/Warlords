package com.ebicep.warlords.game.option.wavedefense.events.modes;

import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;

import javax.annotation.Nonnull;

public class BoltarosLairOption implements Option {

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer) {
            AbilityTree abilityTree = ((WarlordsPlayer) player).getAbilityTree();
            abilityTree.setMaxMasterUpgrades(6);
            for (AbstractUpgradeBranch<?> upgradeBranch : abilityTree.getUpgradeBranches()) {
                upgradeBranch.setMaxUpgrades(8);
            }
        }
    }

}
