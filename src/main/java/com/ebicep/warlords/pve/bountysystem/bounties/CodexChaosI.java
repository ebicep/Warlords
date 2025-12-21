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
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Arrays;
import java.util.List;


public class CodexChaosI extends AbstractBounty implements TracksDuringGame, EventCost, LibraryArchives2 {

    @Field("killing_blows_experienced")
    private int[] killingBlowsExperienced = {0, 0, 0};  // index > 0 = EGA, 1 = EWA, 2 = VPA | element value > # of blows
    @Transient
    private int[] newKillingBlowsExperienced = {0, 0, 0};

    @Override
    public String getName() {
        return "Codex Chaos";
    }

    @Override
    public String getDescription() {
        return "Experience the Killing Blow ability from each Inquisiteur variant 3 times.";
    }

    @Nullable
    @Override
    public List<Component> getProgress() {
        if (value >= getTarget()) {
            return null;
        }
        return List.of(
                ComponentBuilder.create("Progress: ", NamedTextColor.GRAY).build(),
                ComponentBuilder.create("  EGA: ", NamedTextColor.GRAY)
                                .text(NumberFormat.addCommaAndRound(killingBlowsExperienced[0]), NamedTextColor.GOLD)
                                .text("/", NamedTextColor.AQUA)
                                .text("3", NamedTextColor.GOLD)
                                .build(),
                ComponentBuilder.create("  EWA: ", NamedTextColor.GRAY)
                                .text(NumberFormat.addCommaAndRound(killingBlowsExperienced[1]), NamedTextColor.GOLD)
                                .text("/", NamedTextColor.AQUA)
                                .text("3", NamedTextColor.GOLD)
                                .build(),
                ComponentBuilder.create("  VPA: ", NamedTextColor.GRAY)
                                .text(NumberFormat.addCommaAndRound(killingBlowsExperienced[2]), NamedTextColor.GOLD)
                                .text("/", NamedTextColor.AQUA)
                                .text("3", NamedTextColor.GOLD)
                                .build()
        );
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
        for (int i = 0; i < 3; i++) {
            killingBlowsExperienced[i] = 0;
        }
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
            killingBlowsExperienced[i] += newKillingBlowsExperienced[i];
        }
        TracksDuringGame.super.apply(bounty);
    }

    @Override
    public long getNewValue() {
        return Arrays.stream(killingBlowsExperienced).allMatch(integer -> integer >= 3) ? 1 : 0;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKillingBlow(EventInquisiteur.EventInquisteurKillingBlowEvent event) {
        if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC) {
            switch (warlordsNPC.getMob().getMobRegistry()) {
                case EVENT_INQUISITEUR_EGA -> newKillingBlowsExperienced[0]++;
                case EVENT_INQUISITEUR_EWA -> newKillingBlowsExperienced[1]++;
                case EVENT_INQUISITEUR_VPA -> newKillingBlowsExperienced[2]++;
            }
        }
    }
}
