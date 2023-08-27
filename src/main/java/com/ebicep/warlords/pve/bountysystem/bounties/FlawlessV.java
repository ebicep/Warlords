package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.bountysystem.rewards.WeeklyRewardSpendable3;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;
import com.ebicep.warlords.util.warlords.PlayerFilter;

public class FlawlessV extends AbstractBounty implements TracksPostGame, WeeklyRewardSpendable3 {

    @Override
    public String getName() {
        return "Flawless";
    }

    @Override
    public String getDescription() {
        return "Complete Normal Mode without dying.";
    }

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.FLAWLESS_III;
    }


    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer) {
        if (!BountyUtils.waveDefenseMatchesDifficulty(game, DifficultyIndex.NORMAL)) {
            return;
        }
        int totalTeamDeaths = PlayerFilter.playingGame(game)
                                          .teammatesOf(warlordsPlayer)
                                          .stream()
                                          .mapToInt(warlordsEntity -> warlordsEntity.getMinuteStats().total().getDeaths())
                                          .sum();
        if (totalTeamDeaths == 0) {
            value++;
        }
    }

}
