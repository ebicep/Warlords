package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.TheAcropolisOption;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.GardenOfHesperides1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import com.ebicep.warlords.pve.mobs.events.gardenofhesperides.EventCronus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.springframework.data.annotation.Transient;

public class StateOfMindI extends AbstractBounty implements TracksDuringGame, EventCost, GardenOfHesperides1 {

    @Transient
    private int newKills = 0;

    @Override
    public String getName() {
        return "State of Mind";
    }

    @Override
    public String getDescription() {
        return "Defeat Cronus while he is rejuvenating.";
    }

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.STATE_OF_MIND_I;
    }

    @Override
    public void reset() {
        newKills = 0;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(WarlordsDeathEvent event) {
        if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC &&
                warlordsNPC.getMob() instanceof EventCronus cronus &&
                cronus.diedDuringRejuvenate()
        ) {
            newKills++;
        }
    }

    @Override
    public boolean trackGame(Game game) {
        return DatabaseGameEvent.eventIsActive() && game.getOptions().stream().anyMatch(option -> option instanceof TheAcropolisOption);
    }

    @Override
    public long getNewValue() {
        return newKills;
    }
}
