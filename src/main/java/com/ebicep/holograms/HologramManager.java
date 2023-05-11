package com.ebicep.holograms;

import com.ebicep.warlords.Warlords;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class HologramManager implements Listener {

    public void init() {
        new BukkitRunnable() {

            @Override
            public void run() {

            }
        }.runTaskTimerAsynchronously(Warlords.getInstance(), 0, 20);
    }

}
