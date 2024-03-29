package com.ebicep.warlords.player.ingame.cooldowns.cooldowns;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.java.TriConsumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This type of cooldown is used for any regular cooldowns that are removed when its timer reaches 0
 */
public class RegularCooldown<T> extends AbstractCooldown<T> {

    /**
     * <p>cooldown = this
     * <p>ticksLeft = ticksLeft of cooldown
     * <p>counter = counter incrementing every tick, separate from ticksLeft
     */
    protected final List<TriConsumer<RegularCooldown<T>, Integer, Integer>> consumers = new ArrayList<>();
    protected int startingTicks;
    protected int ticksLeft;
    protected int ticksElapsed;
    protected boolean enhanced = false;

    public RegularCooldown(
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

    public RegularCooldown(
            String name,
            String nameAbbreviation,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
            int ticksLeft,
            List<TriConsumer<RegularCooldown<T>, Integer, Integer>> triConsumers
    ) {
        this(name, nameAbbreviation, cooldownClass, cooldownObject, from, cooldownType, onRemove, true, ticksLeft, triConsumers);
    }

    public RegularCooldown(
            String name,
            String nameAbbreviation,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
            boolean removeOnDeath,
            int ticksLeft,
            List<TriConsumer<RegularCooldown<T>, Integer, Integer>> triConsumers
    ) {
        this(
                name,
                nameAbbreviation,
                cooldownClass,
                cooldownObject,
                from,
                cooldownType,
                onRemove,
                cooldownManager -> {
                },
                removeOnDeath,
                ticksLeft,
                triConsumers
        );
    }

    public RegularCooldown(
            String name,
            String nameAbbreviation,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
            Consumer<CooldownManager> onRemoveForce,
            boolean removeOnDeath,
            int ticksLeft,
            List<TriConsumer<RegularCooldown<T>, Integer, Integer>> triConsumers
    ) {
        super(name, nameAbbreviation, cooldownClass, cooldownObject, from, cooldownType, onRemove, onRemoveForce, removeOnDeath);
        this.startingTicks = ticksLeft;
        this.ticksLeft = ticksLeft;
        this.consumers.addAll(triConsumers);
    }

    public RegularCooldown(
            String name,
            String nameAbbreviation,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
            Consumer<CooldownManager> onRemoveForce,
            int ticksLeft
    ) {
        this(name, nameAbbreviation, cooldownClass, cooldownObject, from, cooldownType, onRemove, onRemoveForce, ticksLeft, new ArrayList<>());
    }

    public RegularCooldown(
            String name,
            String nameAbbreviation,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
            Consumer<CooldownManager> onRemoveForce,
            int ticksLeft,
            List<TriConsumer<RegularCooldown<T>, Integer, Integer>> triConsumers
    ) {
        this(name, nameAbbreviation, cooldownClass, cooldownObject, from, cooldownType, onRemove, onRemoveForce, true, ticksLeft, triConsumers);
    }

    @Override
    public Component getNameAbbreviation() {
        if (ticksLeft <= 0) {
            return Component.empty();
        }
        if (nameAbbreviation == null) {
            return null;
        }

        return Component.textOfChildren(
                Component.text(nameAbbreviation, cooldownType == CooldownTypes.DEBUFF || nameAbbreviation.equals("MIAS") ? NamedTextColor.RED : NamedTextColor.GREEN),
                Component.text(":", NamedTextColor.GRAY),
                Component.text(ticksLeft / 20 + 1, NamedTextColor.GOLD)
        );
    }

    @Override
    public void onTick(WarlordsEntity from) {
        consumers.forEach(integerConsumer -> integerConsumer.accept(this, ticksLeft, ticksElapsed));
        ticksElapsed++;
        subtractTime(1);
    }

    @Override
    public boolean removeCheck() {
        return ticksLeft <= 0;
    }

    public void subtractTime(int amount) {
        if (this.ticksLeft - amount <= 0) {
            ticksLeft = 0;
        } else {
            this.ticksLeft -= amount;
        }
    }

    public int getTicksLeft() {
        return ticksLeft;
    }

    public void setTicksLeft(int ticksLeft) {
        this.ticksLeft = ticksLeft;
    }

    public boolean hasTicksLeft() {
        return ticksLeft > 0;
    }

    public int getStartingTicks() {
        return startingTicks;
    }

    public void addTriConsumer(TriConsumer<RegularCooldown<T>, Integer, Integer> triConsumer) {
        this.consumers.add(triConsumer);
    }

    public void removeTriConsumer(TriConsumer<RegularCooldown<T>, Integer, Integer> triConsumer) {
        this.consumers.remove(triConsumer);
    }

    public boolean isEnhanced() {
        return enhanced;
    }

    public void setEnhanced(boolean enhanced) {
        this.enhanced = enhanced;
    }
}
