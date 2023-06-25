package com.ebicep.warlords.game.state;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.marker.CanStartGameMarker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.jetbrains.annotations.Nullable;

public class SyncTimerState implements State {

    private final Game game;

    public SyncTimerState(Game game) {
        this.game = game;
    }

    @Override
    public void begin() {
        game.forEachOnlinePlayerWithoutSpectators((player, team) -> {
            player.showTitle(Title.title(
                    Component.empty(),
                    Component.text("Syncing Timers...", NamedTextColor.GREEN),
                    Title.Times.times(Ticks.duration(0), Ticks.duration(20), Ticks.duration(0))
            ));
        });
    }

    @Nullable
    @Override
    public State run() {
        return Warlords.LOOP_TICK_COUNTER.get() % 20 == 0 &&
                game.getMarkers(CanStartGameMarker.class).stream().allMatch(CanStartGameMarker::canStartGame) ? new PlayingState(game) : null;
    }

    @Override
    public void end() {

    }

    @Override
    public int getTicksElapsed() {
        return 0;
    }
}
