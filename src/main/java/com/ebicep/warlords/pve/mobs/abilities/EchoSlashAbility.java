package com.ebicep.warlords.pve.mobs.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import jakarta.annotation.Nonnull;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class EchoSlashAbility extends AbstractPveAbility {

    private final int lookbackTicks;
    private final double attackRadius;
    private final int damage;
    private final Map<UUID, LinkedList<Location>> playerLocationHistory = new HashMap<>();

    public EchoSlashAbility(AbstractAbilityBuilder builder, int lookbackTicks, double attackRadius, int damage) {
        super(builder);
        this.lookbackTicks = lookbackTicks;
        this.attackRadius = attackRadius;
        this.damage = damage;
    }

    public void trackPlayerPosition(WarlordsEntity player) {
        playerLocationHistory.computeIfAbsent(player.getUuid(), k -> new LinkedList<>()).add(player.getLocation().clone());
        LinkedList<Location> history = playerLocationHistory.get(player.getUuid());
        if (history.size() > lookbackTicks) {
            history.removeFirst();
        }
    }

    @Override
    public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
        WarlordsEntity target = PlayerFilter
                .entitiesAround(wp, 20, 10, 20)
                .aliveEnemiesOf(wp)
                .closestFirst(wp)
                .findFirst()
                .orElse(null);

        if (target == null) return false;

        LinkedList<Location> history = playerLocationHistory.get(target.getUuid());
        if (history == null || history.size() < lookbackTicks) return false;

        Location attackLoc = history.getFirst(); // location from X ticks ago

        // Visual windup
        EffectUtils.playCylinderAnimation(attackLoc, 10, Particle.SWEEP_ATTACK, 1);

        // Deal damage instantly (can add delay if desired)
        PlayerFilter.entitiesAround(attackLoc, attackRadius, attackRadius, attackRadius)
                .aliveEnemiesOf(wp)
                .forEach(enemy -> {
                    enemy.addInstance(InstanceBuilder
                            .damage()
                            .cause("Echo Slash")
                            .source(wp)
                            .min(damage)
                            .max(damage)
                    );
                });
        return false;
    }
}
