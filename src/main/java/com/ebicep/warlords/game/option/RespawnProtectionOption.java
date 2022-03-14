
package com.ebicep.warlords.game.option;

import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.WarlordsRespawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 */
public class RespawnProtectionOption implements Option, Listener {

    private static final int DEFAULT_PROTECTION_TIME = 4;
    private static final int DEFAULT_RADIUS = 5;
    private final Map<WarlordsPlayer, Pair<Location, Integer>> spawnProtection = new HashMap<>();
    private int protectionTime;
    private int radius;
    private int radiusSquared;

    public RespawnProtectionOption() {
        this(DEFAULT_PROTECTION_TIME, DEFAULT_RADIUS);
    }

    public RespawnProtectionOption(int protectionTime) {
        this(protectionTime, DEFAULT_RADIUS);
    }
    public RespawnProtectionOption(int protectionTime, int radius) {
        this.protectionTime = protectionTime;
        this.radius = radius;
        this.radiusSquared = radius * radius;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        this.radiusSquared = radius * radius;
    }

    public int getRadiusSquared() {
        return radiusSquared;
    }

    @Override
    public void register(Game game) {
        game.registerEvents(this);
    }

    @Override
    public void start(Game game) {
        new GameRunnable(game) {
            private final Location location = new Location(null, 0, 0, 0);
            
            @Override
            public void run() {
                Iterator<Map.Entry<WarlordsPlayer, Pair<Location, Integer>>> itr = spawnProtection.entrySet().iterator();
                while (itr.hasNext()) {
                    Map.Entry<WarlordsPlayer, Pair<Location, Integer>> next = itr.next();
                    int newVal = next.getValue().getB() - 1;
                    if (newVal <= 0 || next.getKey().getLocation(location).distanceSquared(next.getValue().getA()) > radiusSquared) {
                        itr.remove();
                    } else {
                        next.getValue().setB(newVal);
                    }
                }
            }
        }.runTaskTimer(0, 5);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEvent(WarlordsRespawnEvent event) {
        WarlordsPlayer player = event.getPlayer();
        Location respawnPoint = event.getRespawnLocation();
        spawnProtection.put(player, new Pair<>(respawnPoint, protectionTime * 4));
    }
    
    @EventHandler()
    public void onEvent(WarlordsDamageHealingEvent event) {
        if (spawnProtection.containsKey(event.getPlayer())) {
            event.getPlayer().removeHorse();
            event.setCancelled(true);
        }
    }
}
