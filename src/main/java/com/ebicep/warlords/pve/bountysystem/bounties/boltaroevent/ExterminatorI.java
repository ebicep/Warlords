package com.ebicep.warlords.pve.bountysystem.bounties.boltaroevent;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.BoltaroBonanzaOption;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.FightersGloryReward2;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import com.ebicep.warlords.pve.mobs.events.boltarobonanza.EventBoltaroShadow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.springframework.data.annotation.Transient;

public class ExterminatorI extends AbstractBounty implements TracksDuringGame, EventCost, FightersGloryReward2 {

    @Transient
    private int newKills = 0;

    @Override
    public String getName() {
        return "Exterminator";
    }

    @Override
    public String getDescription() {
        return "Defeat " + getTarget() + " of Boltaro's Shadow in Boltaro Bonanza.";
    }

    @Override
    public int getTarget() {
        return 10_000;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.EXTERMINATOR_I;
    }

    @Override
    public void reset() {
        newKills = 0;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(WarlordsDeathEvent event) {
        if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof EventBoltaroShadow) {
            newKills++;
        }
    }

    @Override
    public boolean trackGame(Game game) {
        return DatabaseGameEvent.eventIsActive() && game.getOptions().stream().anyMatch(option -> option instanceof BoltaroBonanzaOption);
    }


    @Override
    public long getNewValue() {
        return newKills;
    }
}
