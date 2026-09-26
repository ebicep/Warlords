package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.LifetimeCost;
import com.ebicep.warlords.pve.bountysystem.rewards.LifetimeRewardSpendable3;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;
import com.ebicep.warlords.util.java.NumberFormat;

public class SentinelI extends AbstractBounty implements TracksPostGame, LifetimeCost, LifetimeRewardSpendable3 {

    @Override
    public String getName() {
        return "Sentinel";
    }

    @Override
    public String getDescription() {
        return "Get " + NumberFormat.addCommaAndRound(getTarget()) + " assists in PvE.";
    }

    @Override
    public int getTarget() {
        return 25_000;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.SENTINEL_I;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        if (!GameMode.isPvE(game.getGameMode())) {
            return;
        }
        value += warlordsPlayer.getMinuteStats().total().getAssists();
    }

}
