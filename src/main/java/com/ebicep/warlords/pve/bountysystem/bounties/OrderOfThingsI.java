package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.TartarusOption;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.GardenOfHesperides2;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import com.ebicep.warlords.pve.mobs.events.gardenofhesperides.EventHades;
import com.ebicep.warlords.pve.mobs.events.gardenofhesperides.EventPoseidon;
import com.ebicep.warlords.pve.mobs.events.gardenofhesperides.EventZeus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.springframework.data.annotation.Transient;

import java.util.List;

public class OrderOfThingsI extends AbstractBounty implements TracksDuringGame, EventCost, GardenOfHesperides2 {

    private static final List<Class<?>> ORDER = List.of(EventHades.class, EventPoseidon.class, EventZeus.class);
    @Transient
    private int newKills = 0;

    @Override
    public String getName() {
        return "Order of Things";
    }

    @Override
    public String getDescription() {
        return "Complete Tartarus in the effective order.";
    }

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.ORDER_OF_THINGS_I;
    }

    @Override
    public void reset() {
        newKills = 0;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(WarlordsDeathEvent event) {
        if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC) {
            if (ORDER.indexOf(warlordsNPC.getMob().getClass()) == newKills) {
                newKills++;
            }
        }
    }

    @Override
    public boolean trackGame(Game game) {
        return DatabaseGameEvent.eventIsActive() && game.getOptions().stream().anyMatch(option -> option instanceof TartarusOption);
    }

    @Override
    public long getNewValue() {
        return newKills == 3 ? 0 : 1;
    }

}
