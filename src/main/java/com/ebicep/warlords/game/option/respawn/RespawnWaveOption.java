package com.ebicep.warlords.game.option.respawn;

import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsRespawnEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsGiveRespawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicInteger;

public class RespawnWaveOption implements Option, Listener {

    public static final int DEFAULT_INITIAL_DELAY = 20;
    public static final int DEFAULT_TASK_PERIOD = 12;
    public static final int DEFAULT_MIN_RESPAWN_TIMER = 4;

    private final int initialDelay;
    private final int taskPeriod;
    private final int minRespawnTimer;
    private int currentTimer = 0;

    public RespawnWaveOption() {
        this(DEFAULT_INITIAL_DELAY, DEFAULT_TASK_PERIOD, DEFAULT_MIN_RESPAWN_TIMER);
    }

    public RespawnWaveOption(int initialTaskDelay, int taskPeriod, int minRespawnTimer) {
        this.initialDelay = initialTaskDelay;
        this.taskPeriod = taskPeriod;
        this.minRespawnTimer = minRespawnTimer;
    }

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(this);
        game.registerGameMarker(TimerSkipAbleMarker.class, (delayInTicks) -> {
                    currentTimer += delayInTicks;
                    for (WarlordsEntity player : PlayerFilter.playingGame(game)) {
                        if (player.getRespawnTickTimer() >= 0) {
                            giveRespawnTimer(player);
                        }
                    }
                }
        );
    }

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {
            @Override
            public void run() {
                currentTimer++;
                for (WarlordsEntity player : PlayerFilter.playingGame(game)) {
                    if (player.isDead() && player.isOnline() && player.getRespawnTickTimer() == -1) {
                        giveRespawnTimer(player);
                    }
                }
            }
        }.runTaskTimer(initialDelay, 0);
    }

    public void giveRespawnTimer(WarlordsEntity player) {
        AtomicInteger respawnTime = new AtomicInteger(getRespawnTimer());
        Bukkit.getPluginManager().callEvent(new WarlordsGiveRespawnEvent(player, respawnTime));
        player.setRespawnTimerTicks(Math.max(2, respawnTime.get()));
    }

    private int getRespawnTimer() {
        int respawn = -currentTimer % (taskPeriod * 20);
        while (respawn < minRespawnTimer * 20) {
            respawn += taskPeriod * 20;
        }
        return respawn;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEvent(WarlordsDeathEvent event) {
        giveRespawnTimer(event.getWarlordsEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(WarlordsRespawnEvent event) {
        if (event.isCancelled()) {
            if (event.getWarlordsEntity().getRespawnTickTimer() == 0) {
                giveRespawnTimer(event.getWarlordsEntity());
            }
        }
    }

}
