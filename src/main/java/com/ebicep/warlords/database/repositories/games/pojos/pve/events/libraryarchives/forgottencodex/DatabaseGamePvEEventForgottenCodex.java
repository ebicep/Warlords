package com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.forgottencodex;

import com.ebicep.warlords.commands.debugcommands.misc.GamesCommand;
import com.ebicep.warlords.database.repositories.events.pojos.GameEvents;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.WavesCleared;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.DatabaseGamePvEEventLibraryArchives;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.EventPointsOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.ForgottenCodexOption;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import net.kyori.adventure.text.Component;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class DatabaseGamePvEEventForgottenCodex extends DatabaseGamePvEEventLibraryArchives implements WavesCleared {

    @Field("total_mobs_killed")
    private int totalMobsKilled;
    @Field("waves_cleared")
    private int wavesCleared; //TODO
    private List<DatabaseGamePlayerPvEEventForgottenCodex> players = new ArrayList<>();

    public DatabaseGamePvEEventForgottenCodex() {
    }

    public DatabaseGamePvEEventForgottenCodex(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(game, gameWinEvent, counted);
        AtomicReference<WaveDefenseOption> waveDefenseOption = new AtomicReference<>();
        AtomicReference<EventPointsOption> eventPointsOption = new AtomicReference<>();
        AtomicReference<ForgottenCodexOption> forgottenCodexOption = new AtomicReference<>();
        for (Option option : game.getOptions()) {
            if (option instanceof WaveDefenseOption) {
                waveDefenseOption.set((WaveDefenseOption) option);
            } else if (option instanceof EventPointsOption) {
                eventPointsOption.set((EventPointsOption) option);
            } else if (option instanceof ForgottenCodexOption) {
                forgottenCodexOption.set((ForgottenCodexOption) option);
            }
        }
        if (waveDefenseOption.get() == null || eventPointsOption.get() == null || forgottenCodexOption.get() == null) {
            throw new IllegalStateException("Missing option");
        }
        game.warlordsPlayers()
            .forEach(warlordsPlayer -> players.add(new DatabaseGamePlayerPvEEventForgottenCodex(warlordsPlayer,
                    gameWinEvent, waveDefenseOption.get(),
                    eventPointsOption.get(),
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
        return 20_000;
    }

    @Override
    public void updatePlayerStatsFromGame(DatabaseGameBase databaseGame, int multiplier) {
        players.forEach(databaseGamePlayerPvEEventForgottenCodex -> {
            DatabaseGameBase.updatePlayerStatsFromTeam(databaseGame,
                    databaseGamePlayerPvEEventForgottenCodex,
                    multiplier
            );
            GamesCommand.PLAYER_NAMES.add(databaseGamePlayerPvEEventForgottenCodex.getName());
        });
    }

    @Override
    public void appendLastGameStats(Hologram hologram) {
        super.appendLastGameStats(hologram);
    }

    @Override
    public void addCustomHolograms(List<Hologram> holograms) {
        super.addCustomHolograms(holograms);

    }

    @Override
    public String getGameLabel() {
        return super.getGameLabel();
    }

    @Override
    public List<Component> getExtraLore() {
        List<Component> extraLore = super.getExtraLore();
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

    @Override
    public int getWavesCleared() {
        return wavesCleared;
    }
}
