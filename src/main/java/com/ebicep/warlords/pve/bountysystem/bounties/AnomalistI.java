package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.bountysystem.costs.LifetimeCost;
import com.ebicep.warlords.pve.bountysystem.rewards.LifetimeRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;

public class AnomalistI extends AbstractBounty implements TracksPostGame, LifetimeCost, LifetimeRewardSpendable1 {

    @Override
    public String getName() {
        return "Anomalist";
    }

    @Override
    public String getDescription() {
        return "Win " + getTarget() + " Anomalies.";
    }

    @Override
    public int getTarget() {
        return 200;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.ANOMALIST_I;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        if (BountyUtils.completedAnomaly(game, gameWinEvent)) {
            value++;
        }
    }

}
