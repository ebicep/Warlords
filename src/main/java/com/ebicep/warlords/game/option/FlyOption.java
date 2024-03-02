package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;

public class FlyOption implements Option {

    private boolean flyEnabled = false;

    public void setFlyEnabled(boolean flyEnabled) {
        this.flyEnabled = flyEnabled;
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (player.getEntity() instanceof Player p) {
            p.setAllowFlight(flyEnabled);
            p.setFlying(flyEnabled);
        }
    }

    @Override
    public void onPlayerReJoinGame(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setAllowFlight(flyEnabled);
                player.setFlying(flyEnabled);
            }
        }.runTaskLater(Warlords.getInstance(), 2); // delay bc ?
    }

}
