package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.onslaught.OnslaughtOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable4;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;

public class Challenge1 extends AbstractBounty implements TracksPostGame, DailyRewardSpendable4 {

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public String getName() {
        return "Challenge";
    }

    @Override
    public String getDescription() {
        return "Reach 5 minutes in Onslaught.";
    }

    @Override
    public Bounty getBounty() {
        return Bounty.CHALLENGE1;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer) {
        BountyUtils.getPvEOptionFromGame(game, OnslaughtOption.class).ifPresent(onslaughtOption -> {
            int secondsElapsed = onslaughtOption.getTicksElapsed() / 20;
            if (secondsElapsed >= 5 * 60) {
                value++;
            }
        });
    }

}
