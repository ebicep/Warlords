package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.LifetimeCost;
import com.ebicep.warlords.pve.bountysystem.rewards.LifetimeRewardSpendable3;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;

import java.util.LinkedHashMap;

public class AmassI extends AbstractBounty implements TracksPostGame, LifetimeCost, LifetimeRewardSpendable3 {

    @Override
    public String getName() {
        return "Amass";
    }

    @Override
    public String getDescription() {
        return "Accumulate 1 million experience.";
    }

    @Override
    public int getTarget() {
        return 1_000_000;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.AMASS_I;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        LinkedHashMap<String, Long> expSummary = ExperienceManager.getExpFromGameStats(warlordsPlayer, false).getUniversalExpGainSummary();
        value += expSummary.values().stream().mapToLong(Long::longValue).sum();
    }

}
