package com.ebicep.warlords.game.option.pvp.siege;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.state.ClosedState;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SiegeOption implements Option {

    private final Map<Team, Location> teamPayloadStart = new HashMap<>();
    private final Location location;
    private int ticksElapsed = 0;
    private Game game;
    private SiegeState state;

    public SiegeOption(Location location) {
        this.location = location;
    }

    public SiegeOption addPayloadStart(Team team, Location location) {
        teamPayloadStart.put(team, location);
        return this;
    }

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        state = new SiegeCapturePointState(this);
        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(10, "state-time") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                return Collections.singletonList(state.getSidebarComponent(ticksElapsed));
            }
        });
        game.registerGameMarker(TimerSkipAbleMarker.class, new TimerSkipAbleMarker() {
            @Override
            public int getDelay() {
                return 0;
            }

            @Override
            public void skipTimer(int delayInTicks) {
                if (state == null || !(state instanceof TimerSkipAbleMarker timerSkipAbleMarker)) {
                    return;
                }
                timerSkipAbleMarker.skipTimer(delayInTicks);
            }
        });
    }

    @Override
    public void start(@Nonnull Game game) {
        state.start(game);
        new GameRunnable(game) {
            @Override
            public void run() {
                if (gameEnded()) {
                    this.cancel();
                    return;
                }
                boolean advanceStateFromTick = state.tick(ticksElapsed);
                ticksElapsed++;
                if (!advanceStateFromTick) {
                    return;
                }
                if (gameEnded()) {
                    this.cancel();
                    return;
                }
                gotoNextState();
            }
        }.runTaskTimer(20, 0);
    }

    private boolean gameEnded() {
        return game.getState(EndState.class).isPresent() || game.getState(ClosedState.class).isPresent();
    }

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
        if (gameEnded()) {
            return;
        }
        state.updateInventory(warlordsPlayer, player);
    }

    private void gotoNextState() {
        state.end();
        state = state.getNextState();
        state.start(game);
        ticksElapsed = 0;
    }

    public Map<Team, Location> getTeamPayloadStart() {
        return teamPayloadStart;
    }

    public Location getLocation() {
        return location;
    }
}
