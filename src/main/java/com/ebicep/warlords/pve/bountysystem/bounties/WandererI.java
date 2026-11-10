package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.LifetimeCost;
import com.ebicep.warlords.pve.bountysystem.rewards.LifetimeRewardSpendable2;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;
import com.ebicep.warlords.util.java.NumberFormat;

public class WandererI extends AbstractBounty implements TracksPostGame, LifetimeCost, LifetimeRewardSpendable2 {

    @Override
    public String getName() {
        return "Wanderer";
    }

    @Override
    public String getDescription() {
        return "Travel " + NumberFormat.addCommaAndRound(getTarget()) + " blocks in PvE.";
    }

    @Override
    public int getTarget() {
        return 1_000_000;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.WANDERER_I;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        if (!GameMode.isPvE(game.getGameMode())) {
            return;
        }
        value += warlordsPlayer.getBlocksTravelled();
    }

}
