package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.entity.Player;

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
        WarlordsEntity warlordsEntity = Warlords.getPlayer(player);
        if (warlordsEntity == null) {
            return;
        }
        new GameRunnable(warlordsEntity.getGame()) {
            @Override
            public void run() {
                player.setAllowFlight(flyEnabled);
                player.setFlying(flyEnabled);
            }
        }.runTaskLater(2); // delay bc ?
    }

}
