package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;
import com.ebicep.warlords.util.java.NumberFormat;

public class ExploreI extends AbstractBounty implements TracksPostGame, DailyRewardSpendable1 {

    @Override
    public String getName() {
        return "Explorer";
    }

    @Override
    public String getDescription() {
        return "Travel " + NumberFormat.addCommaAndRound(getTarget()) + " blocks in any gamemode.";
    }

    @Override
    public int getTarget() {
        return 10_000;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.EXPLORE_I;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer) {
        value += warlordsPlayer.getBlocksTravelled();
    }

}
