package com.ebicep.warlords.game.option.damage;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nonnull;

public class KillDamage extends ExternalDamage {

    @Override
    public EntityDamageEvent.DamageCause getDamageCause() {
        return EntityDamageEvent.DamageCause.KILL;
    }

    @Override
    public void onDamage(@Nonnull WarlordsEntity warlordsEntity, EntityDamageEvent e) {
        if (warlordsEntity.isAlive()) {
            warlordsEntity.addDamageInstance(warlordsEntity, "Fall", 1000000, 1000000, 0, 100);
        }
    }
}
