package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
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

import java.util.ArrayList;
import java.util.List;

public class ForgottenSlayerI extends AbstractBounty implements TracksDuringGame, EventCost, LibraryArchives2 {

    @Field("inquisiteurs_defeated")
    private List<Integer> inquisiteursDefeated = new ArrayList<>() {{ // index > 0 = EGA, 1 = EWA, 2 = VPA | element value > times killed
        add(0);
        add(0);
        add(0);
    }};
    @Transient
    private List<Integer> newinquisiteursDefeated = new ArrayList<>();

    @Nullable
    @Override
    public List<Component> getProgress() {
        if (value >= getTarget()) {
            return null;
        }
        return List.of(
                ComponentBuilder.create("Progress: ", NamedTextColor.GRAY).build(),
                ComponentBuilder.create("  EGA: ", NamedTextColor.GRAY)
                                .text(NumberFormat.addCommaAndRound(inquisiteursDefeated.get(0)), NamedTextColor.GOLD)
                                .text("/", NamedTextColor.AQUA)
                                .text("5", NamedTextColor.GOLD)
                                .build(),
                ComponentBuilder.create("  EWA: ", NamedTextColor.GRAY)
                                .text(NumberFormat.addCommaAndRound(inquisiteursDefeated.get(1)), NamedTextColor.GOLD)
                                .text("/", NamedTextColor.AQUA)
                                .text("5", NamedTextColor.GOLD)
                                .build(),
                ComponentBuilder.create("  VPA: ", NamedTextColor.GRAY)
                                .text(NumberFormat.addCommaAndRound(inquisiteursDefeated.get(2)), NamedTextColor.GOLD)
                                .text("/", NamedTextColor.AQUA)
                                .text("5", NamedTextColor.GOLD)
                                .build()
        );
    }

    @Override
    public String getName() {
        return "Forgotten Slayer";
    }

    @Override
    public String getDescription() {
        return "Defeat each of the Inquisiteurs 5 times.";
    }

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.FORGOTTEN_SLAYER_I;
    }

    @Override
    public void reset() {
        newinquisiteursDefeated.clear();
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
            inquisiteursDefeated.set(i, inquisiteursDefeated.get(i) + newinquisiteursDefeated.get(i));
        }
        TracksDuringGame.super.apply(bounty);
    }

    @Override
    public long getNewValue() {
        return inquisiteursDefeated.stream().allMatch(integer -> integer >= 5) ? 1 : 0;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(WarlordsDeathEvent event) {
        if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof EventInquisiteur) {
            switch (warlordsNPC.getMob().getMobRegistry()) {
                case EVENT_INQUISITEUR_EGA -> newinquisiteursDefeated.set(0, newinquisiteursDefeated.get(0) + 1);
                case EVENT_INQUISITEUR_EWA -> newinquisiteursDefeated.set(1, newinquisiteursDefeated.get(1) + 1);
                case EVENT_INQUISITEUR_VPA -> newinquisiteursDefeated.set(2, newinquisiteursDefeated.get(2) + 1);
            }
        }
    }
}
