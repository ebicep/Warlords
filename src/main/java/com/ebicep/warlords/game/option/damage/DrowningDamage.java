package com.ebicep.warlords.game.option.damage;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nonnull;

public class DrowningDamage extends ExternalDamage {

    @Override
    public EntityDamageEvent.DamageCause getDamageCause() {
        return EntityDamageEvent.DamageCause.DROWNING;
    }

    @Override
    public void onDamage(@Nonnull WarlordsEntity warlordsEntity, EntityDamageEvent e) {
        //100 flat
        if (warlordsEntity.getGame().isFrozen()) {
            return;
        }
        warlordsEntity.addDamageInstance(warlordsEntity, "Fall", 100, 100, 0, 100);
        warlordsEntity.resetRegenTimer();
    }
}
