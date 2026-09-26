package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.LifetimeCost;
import com.ebicep.warlords.pve.bountysystem.rewards.LifeTimeRewardSpendable4;
import com.ebicep.warlords.pve.bountysystem.rewards.LifetimeRewardSpendable3;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;

public class RestorationI extends AbstractBounty implements TracksPostGame, LifetimeCost, LifeTimeRewardSpendable4 {

    @Override
    public String getName() {
        return "Restoration";
    }

    @Override
    public String getDescription() {
        return "Heal for 400 million in any gamemode.";
    }

    @Override
    public int getTarget() {
        return 400_000_000;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.RESTORATION_I;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        if (!GameMode.isPvE(game.getGameMode())) {
            return;
        }
        value += warlordsPlayer.getMinuteStats().total().getHealing();
    }

}
