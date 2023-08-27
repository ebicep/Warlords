package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.bountysystem.costs.WeeklyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.WeeklyRewardSpendable4;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;
import com.ebicep.warlords.util.warlords.PlayerFilter;

public class ChallengeVI extends AbstractBounty implements TracksPostGame, WeeklyCost, WeeklyRewardSpendable4 {

    @Override
    public String getName() {
        return "Challenge";
    }

    @Override
    public String getDescription() {
        return "Complete Extreme Mode with less than 5 deaths.";
    }

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.CHALLENGE_VI;
    }


    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer) {
        if (!BountyUtils.waveDefenseMatchesDifficulty(game, DifficultyIndex.EXTREME)) {
            return;
        }
        int totalTeamDeaths = PlayerFilter.playingGame(game)
                                          .teammatesOf(warlordsPlayer)
                                          .stream()
                                          .mapToInt(warlordsEntity -> warlordsEntity.getMinuteStats().total().getDeaths())
                                          .sum();
        if (totalTeamDeaths < 5) {
            value++;
        }
    }

}
