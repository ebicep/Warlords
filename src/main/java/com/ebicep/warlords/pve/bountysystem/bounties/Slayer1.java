package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;

public class Slayer1 extends AbstractBounty implements TracksPostGame, DailyRewardSpendable1 {

    @Override
    public int getTarget() {
        return 500;
    }

    @Override
    public String getName() {
        return "Slayer";
    }

    @Override
    public String getDescription() {
        return "Kill " + getTarget() + " enemies in any gamemode.";
    }

    @Override
    public Bounty getBounty() {
        return Bounty.SLAYER1;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer) {
        value += warlordsPlayer.getMinuteStats().total().getKills();
    }

}
