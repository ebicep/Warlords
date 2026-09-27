package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.DailyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable2;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;
import com.ebicep.warlords.util.java.NumberFormat;

public class EndureI extends AbstractBounty implements TracksPostGame, DailyCost, DailyRewardSpendable2 {

    @Override
    public String getName() {
        return "Endure";
    }

    @Override
    public String getDescription() {
        return "Take " + NumberFormat.addCommaAndRound(getTarget()) + " damage in any gamemode.";
    }

    @Override
    public int getTarget() {
        return 250_000;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.ENDURE_I;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        if (!GameMode.isPvE(game.getGameMode())) {
            return;
        }
        value += warlordsPlayer.getMinuteStats().total().getDamageTaken();
    }

}
