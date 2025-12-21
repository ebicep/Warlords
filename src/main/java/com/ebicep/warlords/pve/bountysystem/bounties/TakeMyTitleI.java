package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.RecordTimeElapsedOption;
import com.ebicep.warlords.game.option.pve.wavedefense.WinByMaxWaveClearOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.TartarusOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.GardenOfHesperides2;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles.GardenOfHesperidesTitle;

public class TakeMyTitleI extends AbstractBounty implements TracksPostGame, EventCost, GardenOfHesperides2 {

    @Override
    public String getName() {
        return "Take My Title";
    }

    @Override
    public String getDescription() {
        return "Complete Tartarus with a Legendary weapon equipped with a Garden of Hesperides title.";
    }

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.TAKE_MY_TITLE_I;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        if (!DatabaseGameEvent.eventIsActive()) {
            return;
        }
        if (BountyUtils.getOptionFromGame(game, TartarusOption.class).isEmpty()) {
            return;
        }
        AbstractWeapon weapon = warlordsPlayer.getWeapon();
        if (weapon == null) {
            return;
        }
        if (!(weapon instanceof GardenOfHesperidesTitle)) {
            return;
        }
        BountyUtils.getOptionFromGame(game, RecordTimeElapsedOption.class)
                   .ifPresent(recordTimeElapsedOption -> {
                       if (gameWinEvent.getCause() instanceof WinByMaxWaveClearOption && recordTimeElapsedOption.getTicksElapsed() < 10 * 60 * 20) {
                           value++;
                       }
                   });
    }

}
