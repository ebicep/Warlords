package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.LibraryArchives1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;

public class ArchivistHunterI extends AbstractBounty implements TracksPostGame, EventCost, LibraryArchives1 {

    @Override
    public String getName() {
        return "Archivist Hunter";
    }

    @Override
    public String getDescription() {
        return "Defeat The Archivist " + getTarget() + " times.";
    }

    @Override
    public int getTarget() {
        return 20;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.ACROPOLIS_FLAWLESS_I;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        if (!DatabaseGameEvent.eventIsActive()) {
            return;
        }
//        BountyUtils.getPvEOptionFromGame(game, GrimoiresGraveyardOption.class).ifPresent(acropolisOption -> {
//            int deaths = warlordsPlayer.getMinuteStats().total().getDeaths();
//            if (deaths == 0) {
//                value++;
//            }
//        });
    }
}
