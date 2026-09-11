package com.ebicep.warlords.player.ingame.cooldowns.cooldowns;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.java.TriConsumer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Cooldown shared between a caster ({@code from}) and other WarlordsEntities.
 * <p>
 * The caster must not be included in the linked entity set ({@link #getLinkedEntities()}). Force-remove / {@code expire} behavior:
 * <ul>
 *     <li>Caster removed: strip this cooldown from every linked entity via {@code removeCooldownNoForce}, then clear the set</li>
 *     <li>Linked entity removed: drop that entity from the set; if the set is empty and the caster still has this cooldown, end the caster copy</li>
 * </ul>
 * Paths that use {@code removeCooldownNoForce} skip that cleanup and must call {@link #unlink(WarlordsEntity)} (or the overload) instead.
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
        Consumer<CooldownManager> oldRemoveForce = getOnRemoveForce();
        setOnRemoveForce(cooldownManager -> {
            oldRemoveForce.accept(cooldownManager);
            cleanupLinkedEntities(cooldownManager);
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
        Consumer<CooldownManager> oldRemove = getOnRemove();
        setOnRemove(cooldownManager -> {
            oldRemove.accept(cooldownManager);
            onRemove.accept(cooldownManager, this);
        });
        Consumer<CooldownManager> oldRemoveForce = getOnRemoveForce();
        setOnRemoveForce(cooldownManager -> {
            oldRemoveForce.accept(cooldownManager);
            onRemoveForce.accept(cooldownManager, this);
            cleanupLinkedEntities(cooldownManager);
        });
    }

    private void cleanupLinkedEntities(CooldownManager cooldownManager) {
        WarlordsEntity entity = cooldownManager.getWarlordsEntity();
        if (Objects.equals(entity, from)) {
            for (WarlordsEntity linked : new ArrayList<>(this.linkedEntities)) {
                linked.getCooldownManager().removeCooldownNoForce(this);
            }
            this.linkedEntities.clear();
            return;
        }
        this.linkedEntities.remove(entity);
        expireCasterIfNoLinks();
    }

    /**
     * NoForce unlink of a single linked entity. Matches the linked-entity branch of force cleanup.
     * Use when removing a link without running {@code onRemoveForce} (e.g. range break).
     */
    public void unlink(WarlordsEntity entity) {
        unlink(entity, true);
    }

    /**
     * @param expireIfEmpty if true and no links remain, end the caster's copy of this cooldown
     */
    public void unlink(WarlordsEntity entity, boolean expireIfEmpty) {
        entity.getCooldownManager().removeCooldownNoForce(this);
        this.linkedEntities.remove(entity);
        if (expireIfEmpty) {
            expireCasterIfNoLinks();
        }
    }

    private void expireCasterIfNoLinks() {
        CooldownManager fromCm = from.getCooldownManager();
        if (this.linkedEntities.isEmpty()
                && fromCm.hasCooldown(this)
                && !fromCm.markedForRemoval(this)) {
            setTicksLeft(0);
        }
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
