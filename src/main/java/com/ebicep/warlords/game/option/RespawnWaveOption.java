package com.ebicep.warlords.game.option;

import com.ebicep.warlords.events.player.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.WarlordsRespawnEvent;
import com.ebicep.warlords.events.player.pve.WarlordsPlayerGiveRespawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.concurrent.atomic.AtomicInteger;

public class RespawnWaveOption implements Option, Listener {
    public static final int DEFAULT_INITIAL_DELAY = 0;
    public static final int DEFAULT_TASK_PERIOD = 12;
    public static final int DEFAULT_MIN_RESPAWN_TIMER = 5;
    
    private int initialDelay;
    private int taskPeriod;
    private int minRespawnTimer;
    private int currentTimer = 0;

    public RespawnWaveOption() {
        this(DEFAULT_INITIAL_DELAY, DEFAULT_TASK_PERIOD, DEFAULT_MIN_RESPAWN_TIMER);
    }
            
    public RespawnWaveOption(int initialTaskDelay, int taskPeriod, int minRespawnTimer) {
        this.initialDelay = initialTaskDelay;
        this.taskPeriod = taskPeriod;
        this.minRespawnTimer = minRespawnTimer;
    }

    public int getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(int initialDelay) {
        this.initialDelay = initialDelay;
    }

    public int getTaskPeriod() {
        return taskPeriod;
    }

    public void setTaskPeriod(int taskPeriod) {
        this.taskPeriod = taskPeriod;
    }

    public int getMinRespawnTimer() {
        return minRespawnTimer;
    }

    public void setMinRespawnTimer(int minRespawnTimer) {
        this.minRespawnTimer = minRespawnTimer;
    }

    public int getCurrentTimer() {
        return currentTimer;
    }

    public void setCurrentTimer(int currentTimer) {
        this.currentTimer = currentTimer;
    }

    @Override
    public void register(Game game) {
        game.registerEvents(this);
        game.registerGameMarker(TimerSkipAbleMarker.class, (delayInTicks) -> {
            currentTimer += delayInTicks / 20;
            for (WarlordsEntity player : PlayerFilter.playingGame(game)) {
                if (player.getRespawnTimer() >= 0) {
                    player.setRespawnTimer(Math.max(player.getRespawnTimer() - delayInTicks * 20, 0));
                }
            }
        });
        this.currentTimer = -initialDelay;
    }

    @Override
    public void start(Game game) {
        new GameRunnable(game) {
            @Override
            public void run() {
                currentTimer++;
                for (WarlordsEntity player : PlayerFilter.playingGame(game)) {
                    if (player.isDead() && player.isOnline() && player.getRespawnTimer() == -1) {
                        giveRespawnTimer(player);
                    }
                }
            }
        }.runTaskTimer(GameRunnable.SECOND, GameRunnable.SECOND);
    }
    
    @EventHandler
    public void onEvent(WarlordsDeathEvent event) {
        giveRespawnTimer(event.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(WarlordsRespawnEvent event) {
        if (event.isCancelled()) {
            if (event.getPlayer().getRespawnTimer() == 0) {
                int respawn = -currentTimer % this.taskPeriod;
                while (respawn < 1) {
                    respawn += this.taskPeriod;
                }
                event.getPlayer().setRespawnTimer(respawn);
            }
        }
    }

    public void giveRespawnTimer(WarlordsEntity player) {
        int respawn = -currentTimer % this.taskPeriod;
        while (respawn < minRespawnTimer) {
            respawn += this.taskPeriod;
        }
        AtomicInteger respawnTime = new AtomicInteger(respawn);
        Bukkit.getPluginManager().callEvent(new WarlordsPlayerGiveRespawnEvent(player, respawnTime));
        player.setRespawnTimer(Math.max(2, respawnTime.get()));
    }
    
}
