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

public class EngageI extends AbstractBounty implements TracksPostGame, DailyCost, DailyRewardSpendable2 {

    @Override
    public String getName() {
        return "Engage";
    }

    @Override
    public String getDescription() {
        return "Spend " + (getTarget() / 60) + " minutes in combat in PvE.";
    }

    @Override
    public int getTarget() {
        return 15 * 60;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.ENGAGE_I;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        if (!GameMode.isPvE(game.getGameMode())) {
            return;
        }
        value += warlordsPlayer.getMinuteStats().total().getTimeInCombat();
    }

}
