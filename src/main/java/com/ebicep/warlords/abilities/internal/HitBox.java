package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

/**
 * Indicats an ability has a HitBox/Range
 */
public interface HitBox {

    FloatModifiable getHitBoxRadius();

    default void renderHitBox(Location center, Player... players) {
        for (Player player : players) {
            EffectUtils.playCircularEffectAround(
                    player,
                    Particle.VILLAGER_HAPPY,
                    center.clone(),
                    getHitBoxRadius().getCalculatedValue(),
                    200
            );
        }
//        EffectUtils.playSphereAnimation(
//                center,
//                getHitBoxRadius().getCalculatedValue(),
//                Particle.VILLAGER_HAPPY,
//                1,
//                4
//        );
    }

}
