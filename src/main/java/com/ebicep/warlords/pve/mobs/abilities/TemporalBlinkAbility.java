package com.ebicep.warlords.pve.mobs.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

public class TemporalBlinkAbility extends AbstractPveAbility {

    private final double teleportRange;
    private final double afterimageRadius;
    private final int afterimageDelay;
    private final int afterimageDamage;

    public TemporalBlinkAbility(AbstractAbilityBuilder builder, double teleportRange, double afterimageRadius, int afterimageDelay, int afterimageDamage) {
        super(builder);
        this.teleportRange = teleportRange;
        this.afterimageRadius = afterimageRadius;
        this.afterimageDelay = afterimageDelay;
        this.afterimageDamage = afterimageDamage;
    }


    @Override
    public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
        Location oldLoc = wp.getLocation().clone();

        Location newLoc = oldLoc.clone().add(
                ThreadLocalRandom.current().nextDouble(-teleportRange, teleportRange),
                0,
                ThreadLocalRandom.current().nextDouble(-teleportRange, teleportRange)
        );
        wp.teleport(newLoc);

        EffectUtils.playCylinderAnimation(oldLoc, 1, 120, 0, 255);

        // Afterimage explosion delayed
        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                EffectUtils.playCylinderAnimation(oldLoc, 1, 150, 1, 30);

                PlayerFilter.entitiesAround(oldLoc, afterimageRadius, afterimageRadius, afterimageRadius)
                        .aliveEnemiesOf(wp)
                        .forEach(enemy -> {
                            enemy.addInstance(InstanceBuilder
                                    .damage()
                                    .cause("After Image")
                                    .source(wp)
                                    .min(afterimageDamage)
                                    .max(afterimageDamage)
                            );
                        });
            }
        }.runTaskLater(afterimageDelay);

        return false;
    }
}

