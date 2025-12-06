package com.ebicep.warlords.pve.bountysystem.bounties.boltaroevent;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.BoltaroBonanzaOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.FightersGloryReward2;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;

public class IntoTheShadowI extends AbstractBounty implements TracksPostGame, EventCost, FightersGloryReward2 {

    @Override
    public String getName() {
        return "Into The Shadow";
    }

    @Override
    public String getDescription() {
        return "Reach a highest split of " + getTarget() + " in Boltaro Bonanza.";
    }

    @Override
    public int getTarget() {
        return 75;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.INTO_THE_SHADOW_I;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        BountyUtils.getOptionFromGame(game, BoltaroBonanzaOption.class).ifPresent(option -> {
            if (option.getHighestSplitValue() > value) {
                value = option.getHighestSplitValue();
            }
        });
    }

    public long getNewValue() {
        return value;
    }
}
