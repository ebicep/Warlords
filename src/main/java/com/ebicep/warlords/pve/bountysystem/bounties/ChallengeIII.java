package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.onslaught.OnslaughtOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.bountysystem.costs.WeeklyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.WeeklyRewardSpendable3;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;

public class ChallengeIII extends AbstractBounty implements TracksPostGame, WeeklyCost, WeeklyRewardSpendable3 {

    @Override
    public String getName() {
        return "Challenge";
    }

    @Override
    public String getDescription() {
        return "Reach " + getTarget() + " minutes in Onslaught.";
    }

    @Override
    public int getTarget() {
        return 15;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.CHALLENGE_III;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        BountyUtils.getOptionFromGame(game, OnslaughtOption.class).ifPresent(onslaughtOption -> {
            int secondsElapsed = onslaughtOption.getTicksElapsed() / 20;
            if (secondsElapsed / 60 > value) {
                value = secondsElapsed;
            }
        });
    }

}
