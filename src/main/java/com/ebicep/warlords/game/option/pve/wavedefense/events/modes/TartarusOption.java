package com.ebicep.warlords.game.option.pve.wavedefense.events.modes;

import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;

import javax.annotation.Nonnull;

public class TartarusOption implements Option {

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer) {
            AbilityTree abilityTree = ((WarlordsPlayer) player).getAbilityTree();
            abilityTree.setMaxMasterUpgrades(6);
        }
    }

}
