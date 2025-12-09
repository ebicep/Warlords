package com.ebicep.warlords.pve.bountysystem.bounties.boltaroevent;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.BoltaroBonanzaOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.BoltarosLairOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.FightersGloryReward2;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles.FightersGloryTitle;

public class TakeMyTitleIII extends AbstractBounty implements TracksPostGame, EventCost, FightersGloryReward2 {

    @Override
    public String getName() {
        return "Take My Title";
    }

    @Override
    public String getDescription() {
        return "Complete the event with a legendary weapon equipped with a Fighters Glory title " + getTarget() + " times.";
    }

    @Override
    public int getTarget() {
        return 3;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.TAKE_MY_TITLE_III;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        if (!DatabaseGameEvent.eventIsActive()) {
            return;
        }
        if (BountyUtils.getOptionFromGame(game, BoltaroBonanzaOption.class).isEmpty() && BountyUtils.getOptionFromGame(game, BoltarosLairOption.class).isEmpty()) {
            return;
        }
        if (!game.warlordsPlayers().filter(p -> p.getTeam().equals(warlordsPlayer.getTeam())).anyMatch(p -> !p.isDead())) {
            return;
        }
        AbstractWeapon weapon = warlordsPlayer.getWeapon();
        if (weapon == null) {
            return;
        }
        if (!(weapon instanceof FightersGloryTitle)) {
            return;
        }
        value++;
    }

}
