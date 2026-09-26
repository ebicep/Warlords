package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.anomaly.AbstractAnomalyOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.bountysystem.costs.DailyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable4;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;

public class ProspectorI extends AbstractBounty implements TracksPostGame, DailyCost, DailyRewardSpendable4 {

    @Override
    public String getName() {
        return "Prospector";
    }

    @Override
    public String getDescription() {
        return "Earn " + getTarget() + " Anomaly reward caches.";
    }

    @Override
    public int getTarget() {
        return 30;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.PROSPECTOR_I;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        BountyUtils.getOptionFromGame(game, AbstractAnomalyOption.class).ifPresent(anomalyOption -> {
            if (!anomalyOption.isRewardEligible(warlordsPlayer.getUuid())) {
                return;
            }
            value += anomalyOption.getCacheRewardCount();
        });
    }

}
