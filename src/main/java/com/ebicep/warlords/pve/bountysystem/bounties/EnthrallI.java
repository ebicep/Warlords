package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.LifetimeCost;
import com.ebicep.warlords.pve.bountysystem.rewards.LifetimeRewardSpendable3;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;

public class EnthrallI extends AbstractBounty implements TracksPostGame, LifetimeCost, LifetimeRewardSpendable3 {

    @Override
    public String getName() {
        return "Enthrall";
    }

    @Override
    public String getDescription() {
        return "Absorb 125 million damage.";
    }

    @Override
    public int getTarget() {
        return 125_000_000;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.ENTHRALL_I;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        value += warlordsPlayer.getMinuteStats().total().getAbsorbed();
    }

}
