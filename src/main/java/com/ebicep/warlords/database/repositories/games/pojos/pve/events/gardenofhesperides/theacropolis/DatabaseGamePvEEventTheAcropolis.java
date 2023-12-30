package com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.theacropolis;

import com.ebicep.warlords.commands.debugcommands.misc.GamesCommand;
import com.ebicep.warlords.database.repositories.events.pojos.GameEvents;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.EventPointsOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.TheAcropolisOption;
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

public class DatabaseGamePvEEventTheAcropolis extends DatabaseGamePvEEvent {

    @Field("total_mobs_killed")
    private int totalMobsKilled;
    @Field("waves_cleared")
    private int wavesCleared;
    private List<DatabaseGamePlayerPvEEventTheAcropolis> players = new ArrayList<>();

    public DatabaseGamePvEEventTheAcropolis() {
    }

    public DatabaseGamePvEEventTheAcropolis(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(game, counted);
        AtomicReference<WaveDefenseOption> waveDefenseOption = new AtomicReference<>();
        AtomicReference<EventPointsOption> eventPointsOption = new AtomicReference<>();
        AtomicReference<TheAcropolisOption> theAcropolisOption = new AtomicReference<>();
        for (Option option : game.getOptions()) {
            if (option instanceof WaveDefenseOption) {
                waveDefenseOption.set((WaveDefenseOption) option);
            } else if (option instanceof EventPointsOption) {
                eventPointsOption.set((EventPointsOption) option);
            } else if (option instanceof TheAcropolisOption) {
                theAcropolisOption.set((TheAcropolisOption) option);
            }
        }
        if (waveDefenseOption.get() == null || eventPointsOption.get() == null || theAcropolisOption.get() == null) {
            throw new IllegalStateException("Missing option");
        }
        game.warlordsPlayers()
            .forEach(warlordsPlayer -> players.add(new DatabaseGamePlayerPvEEventTheAcropolis(warlordsPlayer,
                    gameWinEvent, waveDefenseOption.get(),
                    eventPointsOption.get(),
                    counted
            )));
        this.totalMobsKilled = players.stream().mapToInt(DatabaseGamePlayerBase::getTotalKills).sum();
        this.wavesCleared = waveDefenseOption.get().getWavesCleared();
    }

    @Override
    public GameEvents getEvent() {
        return GameEvents.GARDEN_OF_HESPERIDES;
    }

    @Override
    public int getPointLimit() {
        return 75_000;
    }

    @Override
    public void updatePlayerStatsFromGame(DatabaseGameBase databaseGame, int multiplier) {
        players.forEach(databaseGamePlayerPvEEventTheAcropolis -> {
            DatabaseGameBase.updatePlayerStatsFromTeam(databaseGame,
                    databaseGamePlayerPvEEventTheAcropolis,
                    multiplier
            );
            GamesCommand.PLAYER_NAMES.add(databaseGamePlayerPvEEventTheAcropolis.getName());
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

}
