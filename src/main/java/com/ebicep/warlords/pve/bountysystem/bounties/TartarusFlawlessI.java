package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.RecordTimeElapsedOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.TartarusOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.GardenOfHesperides2;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;

public class TartarusFlawlessI extends AbstractBounty implements TracksPostGame, EventCost, GardenOfHesperides2 {

    @Override
    public String getName() {
        return "Tartarus Flawless";
    }

    @Override
    public String getDescription() {
        return "Complete Tartarus without dying.";
    }

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.TARTARUS_FLAWLESS_I;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        if (!DatabaseGameEvent.eventIsActive()) {
            return;
        }
        if (BountyUtils.getPvEOptionFromGame(game, TartarusOption.class).isEmpty()) {
            return;
        }
        BountyUtils.getPvEOptionFromGame(game, RecordTimeElapsedOption.class)
                   .ifPresent(recordTimeElapsedOption -> {
                       if (gameWinEvent.getCause() instanceof WarlordsGameTriggerWinEvent && recordTimeElapsedOption.getTicksElapsed() < 10 * 60 * 20) {
                           value++;
                       }
                   });
    }

}
