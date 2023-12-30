package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.bountysystem.costs.WeeklyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.WeeklyRewardSpendable4;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;
import com.ebicep.warlords.util.warlords.PlayerFilter;

public class AdvanceIII extends AbstractBounty implements TracksPostGame, WeeklyCost, WeeklyRewardSpendable4 {

    @Override
    public String getName() {
        return "Advance";
    }

    @Override
    public String getDescription() {
        return "Reach Wave " + getTarget() + " in Endless Mode with less than 5 deaths.";
    }

    @Override
    public int getTarget() {
        return 50;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.ADVANCE_III;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        BountyUtils.getOptionFromGame(game, WaveDefenseOption.class).ifPresent(waveDefenseOption -> {
            if (waveDefenseOption.getDifficulty() == DifficultyIndex.ENDLESS) {
                int wavesCleared = waveDefenseOption.getWavesCleared();
                int totalTeamDeaths = PlayerFilter.playingGame(game)
                                                  .teammatesOf(warlordsPlayer)
                                                  .stream()
                                                  .mapToInt(warlordsEntity -> warlordsEntity.getMinuteStats().total().getDeaths())
                                                  .sum();
                if (wavesCleared > value && totalTeamDeaths < 5) {
                    value = wavesCleared;
                }
            }
        });
    }

}
