package com.ebicep.warlords.game.option.pvp.ctf;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.game.WarlordsFlagUpdatedEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FlagOption implements Option {

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(new Listener() {
            @EventHandler
            public void onFlagChange(WarlordsFlagUpdatedEvent event) {
                event.getOld().onFlagUpdateEventOld(event);
                event.getNew().onFlagUpdateEventNew(event);
            }

            @EventHandler
            public void onPlayerLogout(PlayerQuitEvent event) {
                dropFlag(event.getPlayer());
            }

            public boolean dropFlag(Player player) {
                return dropFlag(Warlords.getPlayer(player));
            }

            public boolean dropFlag(@Nullable WarlordsEntity player) {
                if (player == null) {
                    return false;
                }
                FlagHolder.dropFlagForPlayer(player);
                return true;
            }

            @EventHandler
            public void onPlayerDeath(WarlordsDeathEvent event) {
                dropFlag(event.getWarlordsEntity());
            }
        });
    }

}
