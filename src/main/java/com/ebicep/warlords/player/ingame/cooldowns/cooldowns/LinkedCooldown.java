package com.ebicep.warlords.player.ingame.cooldowns.cooldowns;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.java.TriConsumer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * This type of cooldown is used for any cooldown that is linked between WarlordsEntities, if removed from caster then it is removed from all linked entities
 * <p>ex. Intervene</p>
 */
public class LinkedCooldown<T> extends RegularCooldown<T> {

    protected final List<TriConsumer<LinkedCooldown<T>, Integer, Integer>> consumers = new ArrayList<>();
    private final Set<WarlordsEntity> linkedEntities;

    public LinkedCooldown(
            String name,
            String nameAbbreviation,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
            Consumer<CooldownManager> onRemoveForce,
            int ticksLeft,
            List<TriConsumer<LinkedCooldown<T>, Integer, Integer>> triConsumers,
            Set<WarlordsEntity> linkedEntities

    ) {
        super(
                name,
                nameAbbreviation,
                cooldownClass,
                cooldownObject,
                from,
                cooldownType,
                onRemove,
                onRemoveForce,
                ticksLeft
        );
        this.consumers.addAll(triConsumers);
        this.linkedEntities = new HashSet<>(linkedEntities);
        setOnRemoveForce(cooldownManager -> {
            onRemoveForce.accept(cooldownManager);
            this.linkedEntities.forEach(warlordsEntity -> warlordsEntity.getCooldownManager().removeCooldownNoForce(this));
            this.linkedEntities.removeIf(WarlordsEntity::isDead);
        });
    }

    public LinkedCooldown(
            String name,
            String nameAbbreviation,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
            Consumer<CooldownManager> onRemoveForce,
            int ticksLeft,
            List<TriConsumer<LinkedCooldown<T>, Integer, Integer>> triConsumers,
            WarlordsEntity... linkedEntities
    ) {
        this(name, nameAbbreviation, cooldownClass, cooldownObject, from, cooldownType, onRemove, onRemoveForce, ticksLeft, triConsumers,
                Set.of(linkedEntities)
        );
    }

    public LinkedCooldown(
            String name,
            String nameAbbreviation,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            BiConsumer<CooldownManager, LinkedCooldown<T>> onRemove,
            BiConsumer<CooldownManager, LinkedCooldown<T>> onRemoveForce,
            int ticksLeft,
            List<TriConsumer<LinkedCooldown<T>, Integer, Integer>> triConsumers,
            Set<WarlordsEntity> linkedEntities

    ) {
        super(name, nameAbbreviation, cooldownClass, cooldownObject, from, cooldownType, cooldownManager -> {
        }, ticksLeft);
        this.consumers.addAll(triConsumers);
        this.linkedEntities = new HashSet<>(linkedEntities);
        setOnRemove(cooldownManager -> {
            onRemove.accept(cooldownManager, this);
        });
        setOnRemoveForce(cooldownManager -> {
            onRemoveForce.accept(cooldownManager, this);
            this.linkedEntities.forEach(warlordsEntity -> warlordsEntity.getCooldownManager().removeCooldownNoForce(this));
            this.linkedEntities.removeIf(WarlordsEntity::isDead);
        });
    }

    @Override
    public void onTick(WarlordsEntity from) {
        if (this.from == from) {
            consumers.forEach(integerConsumer -> integerConsumer.accept(this, ticksLeft, ticksElapsed));
            ticksElapsed++;
            subtractTime(1);
        }
    }

    @Override
    public boolean removeCheck() {
        return super.removeCheck() || !from.getCooldownManager().hasCooldown(this);
    }

    public Set<WarlordsEntity> getLinkedEntities() {
        return linkedEntities;
    }
}
