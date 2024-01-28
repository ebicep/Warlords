package com.ebicep.warlords.game.option.damage;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nonnull;

public abstract class ExternalDamage implements Option {

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(new Listener() {

            @EventHandler
            public void onEntityDamage(EntityDamageEvent e) {
                if (e.getCause() != getDamageCause()) {
                    return;
                }
                WarlordsEntity warlordsEntity = Warlords.getPlayer(e.getEntity());
                if (warlordsEntity == null) {
                    return;
                }
                if (warlordsEntity.getGame() != game) {
                    return;
                }
                onDamage(warlordsEntity, e);
            }

        });
    }

    public abstract EntityDamageEvent.DamageCause getDamageCause();

    public abstract void onDamage(@Nonnull WarlordsEntity warlordsEntity, EntityDamageEvent e);
}
