package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.bountysystem.costs.LifetimeCost;
import com.ebicep.warlords.pve.bountysystem.rewards.LifetimeRewardSpendable2;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;
import com.ebicep.warlords.util.java.NumberFormat;

public class BoundlessI extends AbstractBounty implements TracksPostGame, LifetimeCost, LifetimeRewardSpendable2 {

    @Override
    public String getName() {
        return "Boundless";
    }

    @Override
    public String getDescription() {
        return "Complete " + NumberFormat.addCommaAndRound(getTarget()) + " waves in Endless Mode.";
    }

    @Override
    public int getTarget() {
        return 1000;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.BOUNDLESS_I;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        BountyUtils.getOptionFromGame(game, WaveDefenseOption.class).ifPresent(waveDefenseOption -> {
            if (waveDefenseOption.getDifficulty() == DifficultyIndex.ENDLESS) {
                value += waveDefenseOption.getWavesCleared();
            }
        });
    }

}
