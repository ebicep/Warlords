package com.ebicep.warlords.pve.bountysystem.bounties.boltaroevent;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.BoltarosLairOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.FightersGloryReward1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;

public class BoltarosAdvanceI extends AbstractBounty implements TracksPostGame, EventCost, FightersGloryReward1 {

    @Override
    public String getName() {
        return "Boltaro's Advance";
    }

    @Override
    public String getDescription() {
        return "Reach Wave " + getTarget() + " Boltaro's Lair.";
    }

    @Override
    public int getTarget() {
        return 40;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.BOLTAROS_ADVANCE_I;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        if (!DatabaseGameEvent.eventIsActive()) {
            return;
        }
        if (BountyUtils.getOptionFromGame(game, BoltarosLairOption.class).isEmpty()) {
            return;
        }
        BountyUtils.getOptionFromGame(game, WaveDefenseOption.class).ifPresent(option -> {
            int wavesCleared = option.getWavesCleared();
            if (wavesCleared > value) {
                value = wavesCleared;
            }
        });
    }

}
