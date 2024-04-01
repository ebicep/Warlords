package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.Particle;

/**
 * Indicats an ability has a HitBox/Range
 */
public interface HitBox {

    FloatModifiable getHitBoxRadius();

    default void renderHitBox(Location center) {
        EffectUtils.playSphereAnimation(
                center,
                getHitBoxRadius().getCalculatedValue(),
                Particle.VILLAGER_HAPPY,
                1
        );
    }

}
