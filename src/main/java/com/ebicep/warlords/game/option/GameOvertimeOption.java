package com.ebicep.warlords.game.option;

import com.ebicep.warlords.events.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
    public void register(Game game) {
        game.registerEvents(this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEvent(WarlordsGameTriggerWinEvent event) {
        if (!wasActivated && event.getCause() instanceof WinAfterTimeoutOption && event.getDeclaredWinner() == null) {
            event.setCancelled(true);
            for (Team team : TeamMarker.getTeams(event.getGame())) {
                event.getGame().setPoints(team, 0);
            }
            for (Option option : event.getGame().getOptions()) {
                if (option instanceof WinByPointsOption) {
                    WinByPointsOption winByPointsOption = (WinByPointsOption) option;
                    winByPointsOption.setPointLimit(overTimePoints);
                }
            }
            WinAfterTimeoutOption drawAfterTimeoutOption = (WinAfterTimeoutOption) event.getCause();
            drawAfterTimeoutOption.setTimeRemaining(overTimeTime);
            event.getGame().forEachOnlinePlayerWithoutSpectators((player, team) -> {
                PacketUtils.sendTitle(player, ChatColor.LIGHT_PURPLE + "OVERTIME!", ChatColor.YELLOW + "First team to reach " + overTimePoints + " points wins!", 0, 60, 0);
                player.sendMessage("§dOvertime is now active!");
                player.playSound(player.getLocation(), Sound.PORTAL_TRAVEL, 1, 1);
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
