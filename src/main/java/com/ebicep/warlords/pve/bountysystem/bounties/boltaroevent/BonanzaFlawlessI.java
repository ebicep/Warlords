package com.ebicep.warlords.pve.bountysystem.bounties.boltaroevent;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.BoltaroBonanzaOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.FightersGloryReward1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;

public class BonanzaFlawlessI extends AbstractBounty implements TracksPostGame, EventCost, FightersGloryReward1 {

    @Override
    public String getName() {
        return "Boltaro Bonanza Flawless";
    }

    @Override
    public String getDescription() {
        return "Complete Boltaro Bonanza without dying " + getTarget() + " times.";
    }

    @Override
    public int getTarget() {
        return 5;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.BONANZA_FLAWLESS_I;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        if (!DatabaseGameEvent.eventIsActive()) {
            return;
        }
        BountyUtils.getOptionFromGame(game, BoltaroBonanzaOption.class).ifPresent(bonanzaOption -> {
            int deaths = warlordsPlayer.getMinuteStats().total().getDeaths();
            if (deaths == 0) {
                value++;
            }
        });
    }
}
