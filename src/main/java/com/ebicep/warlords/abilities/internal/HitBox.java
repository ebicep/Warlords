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

    default void renderHitBox(Location center, Player... players) {
        for (Player player : players) {
            EffectUtils.playCircularEffectAround(
                    player,
                    Particle.HAPPY_VILLAGER,
                    center.clone(),
                    getHitBoxRadius().getCalculatedValue(),
                    200
            );
        }
//        EffectUtils.playSphereAnimation(
//                center,
//                getHitBoxRadius().getCalculatedValue(),
//                Particle.HAPPY_VILLAGER,
//                1,
//                4
//        );
    }

    FloatModifiable getHitBoxRadius();

}
