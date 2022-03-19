package com.ebicep.warlords.game.state;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import org.jetbrains.annotations.Nullable;

public class SyncTimerState implements State {

    private final Game game;

    public SyncTimerState(Game game) {
        this.game = game;
    }

    @Override
    public void begin() {
        game.forEachOnlinePlayerWithoutSpectators((player, team) -> {
            PacketUtils.sendTitle(player, "", "§aSyncing Timers...", 0, 20, 0);
        });
    }

    @Nullable
    @Override
    public State run() {
        return Warlords.getInstance().getCounter() % 20 == 0 ? new PlayingState(game) : null;
    }

    @Override
    public void end() {

    }
}
