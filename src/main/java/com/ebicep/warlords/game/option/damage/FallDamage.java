package com.ebicep.warlords.game.option.damage;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nonnull;

public class FallDamage extends ExternalDamage {

    @Override
    public EntityDamageEvent.DamageCause getDamageCause() {
        return EntityDamageEvent.DamageCause.FALL;
    }

    @Override
    public void onDamage(@Nonnull WarlordsEntity warlordsEntity, EntityDamageEvent e) {
        //HEIGHT - DAMAGE
        //PLAYER
        //9 - 160 - 6
        //15 - 400 - 12
        //30ish - 1040

        //HORSE
        //HEIGHT - DAMAGE
        //18 - 160
        //HEIGHT x 40 - 200
        int damage = (int) e.getDamage();
        if (damage > 5) {
            warlordsEntity.addInstance(InstanceBuilder
                    .fall()
                    .source(warlordsEntity)
                    .value(((damage + 3) * 40 - 200))
            );
            warlordsEntity.resetRegenTimer();
        }
    }
}
