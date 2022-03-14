
package com.ebicep.warlords.util.warlords;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Copy of a BukkitRunable designed for quick registration of tasks related to a game
 */
public abstract class GameRunnable implements Runnable {
    public static final boolean DEFAULT_RUN_IN_PAUSE_MODE = false;
    /**
     * The amount of ticks in a second. Multiply this constant with the amount of seconds you want an runnable to repeat for
     */
    public static final int SECOND = 20;
    
    private int taskId = -1;
    @Nonnull
    private final Game game;
    private final boolean runInPauseMode;
    
    public GameRunnable(@Nonnull Game game) {
        this(game, DEFAULT_RUN_IN_PAUSE_MODE);
    }

    public GameRunnable(@Nonnull Game game, boolean runInPauseMode) {
        this.game = Objects.requireNonNull(game, "game");
        this.runInPauseMode = runInPauseMode;
    }

    @Nonnull
    public Game getGame() {
        return game;
    }

    public boolean runInPauseMode() {
        return runInPauseMode;
    }

    /**
     * Attempts to cancel this task.
     *
     * @throws IllegalStateException if task was not scheduled yet
     */
    public synchronized void cancel() throws IllegalStateException {
        Bukkit.getScheduler().cancelTask(getTaskId());
    }

    /**
     * Schedules this in the Bukkit scheduler to run on next tick.
     *
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalStateException if this was already scheduled
     * @see BukkitScheduler#runTask(Plugin, Runnable)
     */
    public synchronized BukkitTask runTask() throws IllegalArgumentException, IllegalStateException {
        checkState();
        return setupId(Bukkit.getScheduler().runTask(Warlords.getInstance(), getRunnable()));
    }

    /**
     * Schedules this to run after the specified number of server ticks.
     *
     * @param delay the ticks to wait before running the task
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalStateException if this was already scheduled
     * @see BukkitScheduler#runTaskLater(Plugin, Runnable, long)
     */
    public synchronized BukkitTask runTaskLater(long delay) throws IllegalArgumentException, IllegalStateException  {
        checkState();
        return setupId(Bukkit.getScheduler().runTaskLater(Warlords.getInstance(), getRunnable(), delay));
    }

    /**
     * Schedules this to repeatedly run until cancelled, starting after the
     * specified number of server ticks.
     *
     * @param delay the ticks to wait before running the task
     * @param period the ticks to wait between runs
     * @return a BukkitTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalStateException if this was already scheduled
     * @see BukkitScheduler#runTaskTimer(Plugin, Runnable, long, long)
     */
    public synchronized BukkitTask runTaskTimer(long delay, long period) throws IllegalArgumentException, IllegalStateException  {
        checkState();
        return setupId(Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), getRunnable(), delay, period));
    }

    /**
     * Gets the task id for this runnable.
     *
     * @return the task id that this runnable was scheduled as
     * @throws IllegalStateException if task was not scheduled yet
     */
    public synchronized int getTaskId() throws IllegalStateException {
        final int id = taskId;
        if (id == -1) {
            throw new IllegalStateException("Not scheduled yet");
        }
        return id;
    }

    private void checkState() {
        if (taskId != -1) {
            throw new IllegalStateException("Already scheduled as " + taskId);
        }
    }
    
    private Runnable getRunnable() {
        if (this.runInPauseMode) {
            return this;
        } else {
            return () -> {
                if(!game.isFrozen()) {
                    run();
                }
            };
        }
    }

    private BukkitTask setupId(final BukkitTask task) {
        this.taskId = task.getTaskId();
        this.game.registerGameTask(task);
        return task;
    }

    public static GameRunnable create(Game game, Runnable runner) {
        return create(game, runner, DEFAULT_RUN_IN_PAUSE_MODE);
    }
    
    public static GameRunnable create(Game game, Runnable runner, boolean runInPauseMode) {
        return new GameRunnable(game, runInPauseMode) {
            @Override
            public void run() {
                runner.run();
            }

            @Override
            public String toString() {
                return "GameRunnable{" + runner.toString() + '}';
            }
        };
    }
}