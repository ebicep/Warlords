package com.ebicep.warlords.game.option.respawn;

import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsRespawnEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsGiveRespawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedRespawnTimeOption implements Option, Listener {
    private final int respawnTime; // seconds

    public FixedRespawnTimeOption(int respawnTime) {
        this.respawnTime = respawnTime;
    }

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(this);
    }

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {
            @Override
            public void run() {
                for (WarlordsEntity player : PlayerFilter.playingGame(game)) {
                    if (player.isDead() && player.isOnline() && player.getRespawnTickTimer() == -1) {
                        giveRespawnTimer(player);
                    }
                }
            }
        }.runTaskTimer(GameRunnable.SECOND, GameRunnable.SECOND);
    }

    @EventHandler
    public void onEvent(WarlordsDeathEvent event) {
        giveRespawnTimer(event.getWarlordsEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(WarlordsRespawnEvent event) {
        if (event.isCancelled()) {
            if (event.getWarlordsEntity().getRespawnTickTimer() == 0) {
                event.getWarlordsEntity().setRespawnTimerSeconds(respawnTime);
            }
        }
    }

    public void giveRespawnTimer(WarlordsEntity player) {
        AtomicInteger respawnTime = new AtomicInteger(this.respawnTime);
        Bukkit.getPluginManager().callEvent(new WarlordsGiveRespawnEvent(player, respawnTime));
        player.setRespawnTimerSeconds(Math.max(2, respawnTime.get()));
    }

}
