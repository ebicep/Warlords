package com.ebicep.warlords.game.option.damage;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
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
        warlordsEntity.addInstance(InstanceBuilder
                .melee()
                .source(warlordsEntity)
                .value(100)
        );
        warlordsEntity.resetRegenTimer();
    }
}
