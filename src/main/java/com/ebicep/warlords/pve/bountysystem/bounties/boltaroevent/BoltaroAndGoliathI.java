package com.ebicep.warlords.pve.bountysystem.bounties.boltaroevent;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.BoltaroBonanzaOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.BoltarosLairOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.FightersGloryReward2;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;

public class BoltaroAndGoliathI extends AbstractBounty implements TracksPostGame, EventCost, FightersGloryReward2 {

    @Override
    public String getName() {
        return "Boltaro And Goliath";
    }

    @Override
    public String getDescription() {
        return "Complete the Fighters Glory Event " + getTarget() + " times using all three warriors.";
    }

    @Override
    public int getTarget() {
        return 5;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.BOLTARO_AND_GOLIATH_I;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        if (!DatabaseGameEvent.eventIsActive()) {
            return;
        }
        if (!game.warlordsPlayers().filter(p -> p.getTeam().equals(warlordsPlayer.getTeam())).anyMatch(p -> !p.isDead())) {
            return;
        }
        if (!game.getOptions().stream().anyMatch(option -> option instanceof BoltaroBonanzaOption || option instanceof BoltarosLairOption)) {
            return;
        }

        boolean hasBerserker = false;
        boolean hasDefender = false;
        boolean hasRevenant = false;

        for (WarlordsPlayer player : game.warlordsPlayers().toList()) {
            if (!player.getTeam().equals(warlordsPlayer.getTeam())) {
                continue;
            }
            if (player.getSpec() == null) {
                continue;
            }
            String specName = player.getSpec().getName();
            if (specName.equalsIgnoreCase("BERSERKER")) {
                hasBerserker = true;
            } else if (specName.equalsIgnoreCase("DEFENDER")) {
                hasDefender = true;
            } else if (specName.equalsIgnoreCase("REVENANT")) {
                hasRevenant = true;
            }
        }
        if (hasBerserker && hasDefender && hasRevenant) {
            value++;
        }
    }

}
