package com.ebicep.holograms;

import com.ebicep.warlords.Warlords;
import org.bukkit.entity.Interaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class HologramManager implements Listener {

    public void init() {
        new BukkitRunnable() {

            @Override
            public void run() {

            }
        }.runTaskTimerAsynchronously(Warlords.getInstance(), 0, 20);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractAtEntityEvent e) {
        if (!(e.getRightClicked() instanceof Interaction interaction)) {
            return;
        }

    }

}
