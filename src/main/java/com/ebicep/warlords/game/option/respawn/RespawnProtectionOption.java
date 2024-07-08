
package com.ebicep.warlords.game.option.respawn;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsRespawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 */
public class RespawnProtectionOption implements Option, Listener {

    private static final int DEFAULT_PROTECTION_TIME = 4;
    private static final int DEFAULT_RADIUS = 4;
    private final Map<WarlordsEntity, Pair<Location, Integer>> spawnProtection = new HashMap<>();
    private int protectionTime;
    private int radius;
    private int radiusSquared;
    private boolean removeHorse;

    public RespawnProtectionOption() {
        this(DEFAULT_PROTECTION_TIME, DEFAULT_RADIUS, true);
    }

    public RespawnProtectionOption(boolean removeHorse) {
        this(DEFAULT_PROTECTION_TIME, DEFAULT_RADIUS, removeHorse);
    }

    public RespawnProtectionOption(int protectionTime) {
        this(protectionTime, DEFAULT_RADIUS, true);
    }

    public RespawnProtectionOption(int protectionTime, int radius, boolean removeHorse) {
        this.protectionTime = protectionTime;
        this.radius = radius;
        this.radiusSquared = radius * radius;
        this.removeHorse = removeHorse;
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
    public void register(@Nonnull Game game) {
        game.registerEvents(this);
    }

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {
            private final Location location = new Location(null, 0, 0, 0);

            @Override
            public void run() {
                Iterator<Map.Entry<WarlordsEntity, Pair<Location, Integer>>> itr = spawnProtection.entrySet().iterator();
                while (itr.hasNext()) {
                    Map.Entry<WarlordsEntity, Pair<Location, Integer>> next = itr.next();
                    int newVal = next.getValue().getB() - 1;
                    if (newVal <= 0 || (next.getKey().getLocation().getWorld() == next.getValue().getA().getWorld() && next.getKey()
                                                                                                                           .getLocation(location)
                                                                                                                           .distanceSquared(next.getValue()
                                                                                                                                                .getA()) > radiusSquared)) {
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
        WarlordsEntity player = event.getWarlordsEntity();
        Location respawnPoint = event.getRespawnLocation();
        spawnProtection.put(player, new Pair<>(respawnPoint, protectionTime * 4));
    }
    
    @EventHandler()
    public void onEvent(WarlordsDamageHealingEvent event) {
        if (!spawnProtection.containsKey(event.getWarlordsEntity())) {
            return;
        }
        if (removeHorse && event.getSource().getTeam() != event.getWarlordsEntity().getTeam()) {
            event.getWarlordsEntity().removeHorse();
        }
        event.setCancelled(true);
    }
}
