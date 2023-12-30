package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.bountysystem.costs.DailyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable4;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;

public class AdvanceI extends AbstractBounty implements TracksPostGame, DailyCost, DailyRewardSpendable4 {

    @Override
    public String getName() {
        return "Advance";
    }

    @Override
    public String getDescription() {
        return "Complete 25 waves in any gamemode.";
    }

    @Override
    public int getTarget() {
        return 25;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.ADVANCE_I;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        BountyUtils.getOptionFromGame(game, WaveDefenseOption.class).ifPresent(waveDefenseOption -> {
            value += waveDefenseOption.getWavesCleared();
        });
    }

}
