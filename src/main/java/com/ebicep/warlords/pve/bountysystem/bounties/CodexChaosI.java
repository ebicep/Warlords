package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.ForgottenCodexOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.GrimoiresGraveyardOption;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.LibraryArchives2;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import com.ebicep.warlords.pve.mobs.events.libraryarchives.EventInquisiteur;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;


public class CodexChaosI extends AbstractBounty implements TracksDuringGame, EventCost, LibraryArchives2 {

    @Field("killing_blows_experienced")
    private List<Integer> killingBlowsExperienced = new ArrayList<>(); // index > 0 = EGA, 1 = EWA, 2 = VPA | element value > times killed
    @Transient
    private List<Integer> newKillingBlowsExperienced = new ArrayList<>();

    @Override
    public String getName() {
        return "Codex Chaos";
    }

    @Override
    public String getDescription() {
        return "Experience the Killing Blow ability from each Inquisiteur variant 3 times.";
    }

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.CODEX_CHAOS_I;
    }

    @Override
    public void reset() {
        newKillingBlowsExperienced.clear();
    }

    @Override
    public boolean trackGame(Game game) {
        return DatabaseGameEvent.eventIsActive() && game.getOptions()
                                                        .stream()
                                                        .anyMatch(option -> option instanceof GrimoiresGraveyardOption || option instanceof ForgottenCodexOption);
    }

    @Override
    public void apply(AbstractBounty bounty) {
        for (int i = 0; i < 3; i++) {
            killingBlowsExperienced.set(i, killingBlowsExperienced.get(i) + newKillingBlowsExperienced.get(i));
        }
        TracksDuringGame.super.apply(bounty);
    }

    @Override
    public long getNewValue() {
        return killingBlowsExperienced.stream().allMatch(integer -> integer >= 3) ? 1 : 0;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKillingBlow(EventInquisiteur.EventInquisteurKillingBlowEvent event) {
        if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC) {
            switch (warlordsNPC.getMob().getMobRegistry()) {
                case EVENT_INQUISITEUR_EGA -> newKillingBlowsExperienced.set(0, newKillingBlowsExperienced.get(0) + 1);
                case EVENT_INQUISITEUR_EWA -> newKillingBlowsExperienced.set(1, newKillingBlowsExperienced.get(1) + 1);
                case EVENT_INQUISITEUR_VPA -> newKillingBlowsExperienced.set(2, newKillingBlowsExperienced.get(2) + 1);
            }
        }
    }
}
