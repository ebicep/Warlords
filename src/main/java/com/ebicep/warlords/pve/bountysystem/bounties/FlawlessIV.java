package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable3;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;
import com.ebicep.warlords.util.warlords.PlayerFilter;

public class FlawlessIV extends AbstractBounty implements TracksPostGame, DailyRewardSpendable3 {

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public String getName() {
        return "Flawless";
    }

    @Override
    public String getDescription() {
        return "Complete Extreme Mode with less than 10 total team deaths.";
    }

    @Override
    public Bounty getBounty() {
        return Bounty.FLAWLESS_III;
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
        if (totalTeamDeaths < 10) {
            value++;
        }
    }

}
