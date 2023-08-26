package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;

public class Skirmish extends AbstractBounty implements TracksPostGame, DailyRewardSpendable1 {

    @Override
    public int getTarget() {
        return 1000;
    }

    @Override
    public String getName() {
        return "Skirmish";
    }

    @Override
    public String getDescription() {
        return "Deal " + getTarget() + " melee hits in any gamemode.";
    }

    @Override
    public Bounty getBounty() {
        return Bounty.SKIRMISH;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer) {
        value += warlordsPlayer.getMinuteStats().total().getMeleeHits();
    }

}
