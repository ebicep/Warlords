package com.ebicep.warlords.game.option.pvp;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.win.WinAfterTimeoutOption;
import com.ebicep.warlords.game.option.win.WinByPointsOption;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Causes the game to go into an overtime mode when an
 * {@link WarlordsGameTriggerWinEvent trigger win event} is fired by an
 * {@link WinAfterTimeoutOption draw after timeout option}
 */
public class GameOvertimeOption implements Option, Listener {

    private static final int OVERTIME_POINT = 20;
    private static final int OVERTIME_TIME = 60;
    private boolean wasActivated = false;
    private int overTimePoints;
    private int overTimeTime;

    public GameOvertimeOption() {
        this(OVERTIME_POINT, OVERTIME_TIME);
    }

    public GameOvertimeOption(int overTimePoints, int overTimeTime) {
        this.overTimePoints = overTimePoints;
        this.overTimeTime = overTimeTime;
    }

    public boolean isWasActivated() {
        return wasActivated;
    }

    public void setWasActivated(boolean wasActivated) {
        this.wasActivated = wasActivated;
    }

    public int getOverTimePoints() {
        return overTimePoints;
    }

    public void setOverTimePoints(int overTimePoints) {
        this.overTimePoints = overTimePoints;
    }

    public int getOverTimeTime() {
        return overTimeTime;
    }

    public void setOverTimeTime(int overTimeTime) {
        this.overTimeTime = overTimeTime;
    }

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEvent(WarlordsGameTriggerWinEvent event) {
        if (!wasActivated && event.getCause() instanceof WinAfterTimeoutOption drawAfterTimeoutOption && event.getDeclaredWinner() == null) {
            event.setCancelled(true);
            for (Team team : TeamMarker.getTeams(event.getGame())) {
                event.getGame().setPoints(team, 0);
            }
            for (Option option : event.getGame().getOptions()) {
                if (option instanceof WinByPointsOption winByPointsOption) {
                    winByPointsOption.setPointLimit(overTimePoints);
                }
            }
            drawAfterTimeoutOption.setTimeRemaining(overTimeTime);
            event.getGame().forEachOnlinePlayerWithoutSpectators((player, team) -> {
                player.showTitle(Title.title(
                        Component.text("OVERTIME!", NamedTextColor.LIGHT_PURPLE),
                        Component.text("First team to reach " + overTimePoints + " points wins!", NamedTextColor.YELLOW),
                        Title.Times.times(Ticks.duration(0), Ticks.duration(60), Ticks.duration(0))
                ));
                player.sendMessage(Component.text("Overtime is now active!", NamedTextColor.LIGHT_PURPLE));
                player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1, 1);
            });
            wasActivated = true;
        }
    }

    @Override
    public String toString() {
        return "GameOvertimeOption{" + "wasActivated=" + wasActivated + ", overTimePoints=" + overTimePoints + ", overTimeTime=" + overTimeTime + '}';
    }

    @Override
    public void checkConflicts(List<Option> options) {
        boolean hasDrawAfterTimeoutOption = Utils.collectionHasItem(options, e -> e instanceof WinAfterTimeoutOption);
        boolean hasWinByPointsOption = Utils.collectionHasItem(options, e -> e instanceof WinByPointsOption);
        if (!hasDrawAfterTimeoutOption || !hasWinByPointsOption) {
            throw new IllegalArgumentException("Game requires a WinAfterTimeoutOption and a WinByPointsOption for the GameOvertimeOption to work properly");
        }
    }
}
