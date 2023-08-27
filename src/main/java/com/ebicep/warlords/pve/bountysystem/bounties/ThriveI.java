package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.PlayerStatisticsMinute;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.WeeklyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.WeeklyRewardSpendable2;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;

public class ThriveI extends AbstractBounty implements TracksPostGame, WeeklyCost, WeeklyRewardSpendable2 {

    @Override
    public String getName() {
        return "Thrive";
    }

    @Override
    public String getDescription() {
        return "Achieve " + getTarget() + " DHP.";
    }

    @Override
    public int getTarget() {
        return 100_000_000;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.THRIVE_I;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer) {
        PlayerStatisticsMinute.Entry total = warlordsPlayer.getMinuteStats().total();
        value += total.getDamage() + total.getHealing() + total.getAbsorbed();
    }

}
