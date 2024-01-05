package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.ForgottenCodexOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.LibraryArchives2;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;

public class ForgottenFlawlessI extends AbstractBounty implements TracksPostGame, EventCost, LibraryArchives2 {

    @Override
    public String getName() {
        return "Forgotten Flawless";
    }

    @Override
    public String getDescription() {
        return "Complete Forgotten Codex " + getTarget() + " times without dying.";
    }

    @Override
    public int getTarget() {
        return 3;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.FORGOTTEN_FLAWLESS_I;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        if (!DatabaseGameEvent.eventIsActive()) {
            return;
        }
        if (BountyUtils.lostGame(gameWinEvent)) {
            return;
        }
        BountyUtils.getOptionFromGame(game, ForgottenCodexOption.class).ifPresent(option -> {
            if (warlordsPlayer.getMinuteStats().total().getDeaths() == 0) {
                value++;
            }
        });
    }

}
