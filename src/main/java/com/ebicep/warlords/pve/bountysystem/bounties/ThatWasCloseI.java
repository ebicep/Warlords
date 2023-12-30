package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.ForgottenCodexOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.GrimoiresGraveyardOption;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.WeeklyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.LibraryArchives1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import com.ebicep.warlords.pve.mobs.events.libraryarchives.EventNecronomiconGrimoire;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.springframework.data.annotation.Transient;

import java.util.Objects;

public class ThatWasCloseI extends AbstractBounty implements TracksDuringGame, WeeklyCost, LibraryArchives1 {

    @Transient
    private int newValue = 0;

    @Override
    public String getName() {
        return "That Was Close";
    }

    @Override
    public String getDescription() {
        return "Survive the smite of a Necronomicon " + getTarget() + " times.";
    }

    @Override
    public int getTarget() {
        return 15;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.THAT_WAS_CLOSE_I;
    }

    @Override
    public void reset() {
        newValue = 0;
    }

    @Override
    public boolean trackGame(Game game) {
        return DatabaseGameEvent.eventIsActive() && game.getOptions()
                                                        .stream()
                                                        .anyMatch(option -> option instanceof GrimoiresGraveyardOption || option instanceof ForgottenCodexOption);
    }

    @Override
    public long getNewValue() {
        return newValue;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFinalDamageHeal(WarlordsDamageHealingFinalEvent event) {
        if (!Objects.equals(event.getWarlordsEntity().getUuid(), uuid)) {
            return;
        }
        if (!(event.getAttacker() instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof EventNecronomiconGrimoire)) {
            return;
        }
        if (!event.getAbility().equals("Smite")) {
            return;
        }
        if (event.isDead()) {
            return;
        }
        newValue++;
    }
}
