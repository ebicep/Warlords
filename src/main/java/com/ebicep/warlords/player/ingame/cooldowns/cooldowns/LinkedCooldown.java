package com.ebicep.warlords.player.ingame.cooldowns.cooldowns;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.java.TriConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This type of cooldown is used for any cooldown that is linked between WarlordsEntities, if removed from caster then it is removed from all linked entities
 * <p>ex. Intervene</p>
 */
public class LinkedCooldown<T> extends RegularCooldown<T> {

    private final List<WarlordsEntity> linkedEntities;

    public LinkedCooldown(
            String name,
            String nameAbbreviation,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
            int ticksLeft
    ) {
        this(name, nameAbbreviation, cooldownClass, cooldownObject, from, cooldownType, onRemove, ticksLeft, new ArrayList<>());
    }

    public LinkedCooldown(
            String name,
            String nameAbbreviation,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
            int ticksLeft,
            List<TriConsumer<RegularCooldown<T>, Integer, Integer>> triConsumers,
            WarlordsEntity... linkedEntities

    ) {
        super(name, nameAbbreviation, cooldownClass, cooldownObject, from, cooldownType, onRemove, ticksLeft, triConsumers);
        this.linkedEntities = new ArrayList<>(List.of(linkedEntities));
        setOnRemove(cooldownManager -> {
            onRemove.accept(cooldownManager);
            this.linkedEntities.forEach(warlordsEntity -> warlordsEntity.getCooldownManager().removeCooldown(this));
        });
    }

    @Override
    public void onTick(WarlordsEntity from) {
        if (this.from == from) {
            super.onTick(from);
        }
    }

    @Override
    public boolean removeCheck() {
        return super.removeCheck() || !from.getCooldownManager().hasCooldown(this);
    }
}
