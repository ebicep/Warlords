package com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza;

import com.ebicep.warlords.commands.debugcommands.misc.GamesCommand;
import com.ebicep.warlords.database.repositories.events.pojos.GameEvents;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.events.EventPointsOption;
import com.ebicep.warlords.game.option.wavedefense.events.modes.BoltaroBonanzaOption;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class DatabaseGamePvEEventBoltaroBonanza extends DatabaseGamePvEEvent {

    @Field("highest_split")
    private int highestSplit;
    @Field("total_mobs_killed")
    private int totalMobsKilled;
    private List<DatabaseGamePlayerPvEEventBoltaroBonanza> players = new ArrayList<>();

    public DatabaseGamePvEEventBoltaroBonanza() {
    }

    @Override
    public GameEvents getEvent() {
        return GameEvents.BOLTARO;
    }

    public DatabaseGamePvEEventBoltaroBonanza(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(game, counted);
        AtomicReference<WaveDefenseOption> waveDefenseOption = new AtomicReference<>();
        AtomicReference<EventPointsOption> eventPointsOption = new AtomicReference<>();
        AtomicReference<BoltaroBonanzaOption> boltaroBonanzaOption = new AtomicReference<>();
        for (Option option : game.getOptions()) {
            if (option instanceof WaveDefenseOption) {
                waveDefenseOption.set((WaveDefenseOption) option);
            } else if (option instanceof EventPointsOption) {
                eventPointsOption.set((EventPointsOption) option);
            } else if (option instanceof BoltaroBonanzaOption) {
                boltaroBonanzaOption.set((BoltaroBonanzaOption) option);
            }
        }
        if (waveDefenseOption.get() == null || eventPointsOption.get() == null || boltaroBonanzaOption.get() == null) {
            throw new IllegalStateException("Missing option");
        }
        game.warlordsPlayers()
            .forEach(warlordsPlayer -> players.add(new DatabaseGamePlayerPvEEventBoltaroBonanza(warlordsPlayer,
                    waveDefenseOption.get(),
                    eventPointsOption.get()
            )));
        this.highestSplit = boltaroBonanzaOption.get().getHighestSplitValue();
        this.totalMobsKilled = players.stream().mapToInt(DatabaseGamePlayerBase::getTotalKills).sum();
    }

    @Override
    public void updatePlayerStatsFromGame(DatabaseGameBase databaseGame, int multiplier) {
        players.forEach(databaseGamePlayerPvEEventBoltaroBonanza -> {
            DatabaseGameBase.updatePlayerStatsFromTeam(databaseGame,
                    databaseGamePlayerPvEEventBoltaroBonanza,
                    multiplier
            );
            GamesCommand.PLAYER_NAMES.add(databaseGamePlayerPvEEventBoltaroBonanza.getName());
        });
    }

    @Override
    public Set<DatabaseGamePlayerBase> getBasePlayers() {
        return new HashSet<>(players);
    }

    @Override
    public DatabaseGamePlayerResult getPlayerGameResult(DatabaseGamePlayerBase player) {
        return DatabaseGamePlayerResult.NONE;
    }

    @Override
    public void createHolograms() {

    }

    @Override
    public String getGameLabel() {
        return null;
    }

    @Override
    public List<String> getExtraLore() {
        return null;
    }

    public int getHighestSplit() {
        return highestSplit;
    }

}
