package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable2;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;

public class Flawless1 extends AbstractBounty implements TracksPostGame, DailyRewardSpendable2 {

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public String getName() {
        return "Flawless";
    }

    @Override
    public String getDescription() {
        return "Complete Easy Mode solo.";
    }

    @Override
    public Bounty getBounty() {
        return Bounty.FLAWLESS1;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer) {
        if (game.warlordsPlayers().count() != 1) {
            return;
        }
        if (game.getOptions().stream().anyMatch(option -> option instanceof WaveDefenseOption waveDefenseOption && waveDefenseOption.getDifficulty() == DifficultyIndex.EASY)) {
            value++;
        }
    }
}
