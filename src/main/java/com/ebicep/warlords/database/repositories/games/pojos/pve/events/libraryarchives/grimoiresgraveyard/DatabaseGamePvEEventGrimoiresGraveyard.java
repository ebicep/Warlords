package com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.grimoiresgraveyard;

import com.ebicep.warlords.commands.debugcommands.misc.GamesCommand;
import com.ebicep.warlords.database.repositories.events.pojos.GameEvents;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.DatabaseGamePvEEventLibraryArchives;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.EventPointsOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.GrimoiresGraveyardOption;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class DatabaseGamePvEEventGrimoiresGraveyard extends DatabaseGamePvEEventLibraryArchives {

    @Field("total_mobs_killed")
    private int totalMobsKilled;
    @Field("waves_cleared")
    private int wavesCleared;
    private List<DatabaseGamePlayerPvEEventGrimoiresGraveyard> players = new ArrayList<>();

    public DatabaseGamePvEEventGrimoiresGraveyard() {
    }

    public DatabaseGamePvEEventGrimoiresGraveyard(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(game, gameWinEvent, counted);
        AtomicReference<WaveDefenseOption> waveDefenseOption = new AtomicReference<>();
        AtomicReference<EventPointsOption> eventPointsOption = new AtomicReference<>();
        AtomicReference<GrimoiresGraveyardOption> grimoiresGraveyardOption = new AtomicReference<>();
        for (Option option : game.getOptions()) {
            if (option instanceof WaveDefenseOption) {
                waveDefenseOption.set((WaveDefenseOption) option);
            } else if (option instanceof EventPointsOption) {
                eventPointsOption.set((EventPointsOption) option);
            } else if (option instanceof GrimoiresGraveyardOption) {
                grimoiresGraveyardOption.set((GrimoiresGraveyardOption) option);
            }
        }
        if (waveDefenseOption.get() == null || eventPointsOption.get() == null || grimoiresGraveyardOption.get() == null) {
            throw new IllegalStateException("Missing option");
        }
        game.warlordsPlayers()
            .forEach(warlordsPlayer -> players.add(new DatabaseGamePlayerPvEEventGrimoiresGraveyard(warlordsPlayer,
                    gameWinEvent, waveDefenseOption.get(),
                    eventPointsOption.get(),
                    grimoiresGraveyardOption.get(),
                    counted
            )));
        this.totalMobsKilled = players.stream().mapToInt(DatabaseGamePlayerBase::getTotalKills).sum();
        this.wavesCleared = waveDefenseOption.get().getWavesCleared();
    }

    @Override
    public GameEvents getEvent() {
        return GameEvents.LIBRARY_ARCHIVES;
    }

    @Override
    public int getPointLimit() {
        return 75_000;
    }

    @Override
    public void updatePlayerStatsFromGame(DatabaseGameBase databaseGame, int multiplier) {
        players.forEach(databaseGamePlayerPvEEventGrimoiresGraveyard -> {
            DatabaseGameBase.updatePlayerStatsFromTeam(databaseGame,
                    databaseGamePlayerPvEEventGrimoiresGraveyard,
                    multiplier
            );
            GamesCommand.PLAYER_NAMES.add(databaseGamePlayerPvEEventGrimoiresGraveyard.getName());
        });
    }

    @Override
    public void appendLastGameStats(Hologram hologram) {
        super.appendLastGameStats(hologram);
        hologram.getLines().appendText(ChatColor.YELLOW + "Waves Cleared: " + wavesCleared);
    }

    @Override
    public void addCustomHolograms(List<Hologram> holograms) {
        super.addCustomHolograms(holograms);

    }

    @Override
    public String getGameLabel() {
        return super.getGameLabel() + " - " + ChatColor.YELLOW + wavesCleared;
    }

    @Override
    public List<Component> getExtraLore() {
        List<Component> extraLore = super.getExtraLore();
        extraLore.add(Component.text("Waves Cleared: ", NamedTextColor.GRAY)
                               .append(Component.text(wavesCleared, NamedTextColor.YELLOW))
        );
        return extraLore;
    }


    @Override
    public List<DatabaseGamePlayerPvEEvent> getPlayers() {
        return new ArrayList<>(players);
    }

    @Override
    public Set<? extends DatabaseGamePlayerBase> getBasePlayers() {
        return new HashSet<>(players);
    }

    public int getWavesCleared() {
        return wavesCleared;
    }
}
