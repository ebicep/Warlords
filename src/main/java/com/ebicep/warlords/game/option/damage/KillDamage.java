package com.ebicep.warlords.game.option.damage;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
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
            warlordsEntity.addInstance(InstanceBuilder
                    .melee()
                    .source(warlordsEntity)
                    .value(1000000)
            );
        }
    }
}
