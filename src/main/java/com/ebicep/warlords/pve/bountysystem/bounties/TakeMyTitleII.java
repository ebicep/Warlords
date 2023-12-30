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
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles.LibraryArchivesTitle;

public class TakeMyTitleII extends AbstractBounty implements TracksPostGame, EventCost, LibraryArchives2 {

    @Override
    public String getName() {
        return "Take My Title";
    }

    @Override
    public String getDescription() {
        return "Complete Forgotten Codex with a legendary weapon equipped with a Library Archives title " + getTarget() + " times.";
    }

    @Override
    public int getTarget() {
        return 3;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.TAKE_MY_TITLE_II;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        if (!DatabaseGameEvent.eventIsActive()) {
            return;
        }
        if (BountyUtils.lostGame(gameWinEvent)) {
            return;
        }
        if (BountyUtils.getOptionFromGame(game, ForgottenCodexOption.class).isEmpty()) {
            return;
        }
        AbstractWeapon weapon = warlordsPlayer.getWeapon();
        if (weapon == null) {
            return;
        }
        if (!(weapon instanceof LibraryArchivesTitle)) {
            return;
        }
        value++;
    }

}
