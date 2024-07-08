package com.ebicep.warlords.game.option.damage;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nonnull;

public class VoidDamage extends ExternalDamage {

    @Override
    public EntityDamageEvent.DamageCause getDamageCause() {
        return EntityDamageEvent.DamageCause.VOID;
    }

    @Override
    public void onDamage(@Nonnull WarlordsEntity warlordsEntity, EntityDamageEvent e) {
        if (warlordsEntity.isDead()) {
            warlordsEntity.getEntity().teleport(warlordsEntity.getLocation().clone().add(0, 100, 0));
        } else {
            warlordsEntity.addInstance(InstanceBuilder
                    .fall()
                    .source(warlordsEntity)
                    .value(1000000)
            );
        }
    }
}
