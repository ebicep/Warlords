package com.ebicep.warlords.pve.bountysystem.bounties.boltaroevent;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.BoltarosLairOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.FightersGloryReward1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;

public class HuntLairI extends AbstractBounty implements TracksPostGame, EventCost, FightersGloryReward1 {

    @Override
    public String getName() {
        return "Hunt-Lair";
    }

    @Override
    public String getDescription() {
        return "Kill " + getTarget() + " enemies in Boltaro’s Lair.";
    }

    @Override
    public int getTarget() {
        return 500;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.HUNT_LAIR_I;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        if (!DatabaseGameEvent.eventIsActive()) {
            return;
        }
        if (game.getOptions().stream().anyMatch(option -> option instanceof BoltarosLairOption)) {
            value += warlordsPlayer.getMinuteStats().total().getKills();
        }
    }
}
