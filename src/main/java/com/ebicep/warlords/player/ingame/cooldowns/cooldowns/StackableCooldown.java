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
 * This type of cooldown is used for any stacking cooldowns that can either tick down independently or as a group
 */
public class StackableCooldown<T> extends AbstractCooldown<T> {

    protected final List<TriConsumer<StackableCooldown<T>, Integer, Integer>> consumers = new ArrayList<>();
    protected final int maxStacks;
    protected final boolean independentCooldown;
    protected final List<Integer> stackDurations = new ArrayList<>();
    protected int startingTicks;
    protected int ticksLeft;
    protected int ticksElapsed;
    protected boolean enhanced = false;

    public StackableCooldown(
            String name,
            String nameAbbreviation,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
            int ticksLeft,
            int maxStacks,
            boolean independentCooldown
    ) {
        this(name, nameAbbreviation, cooldownClass, cooldownObject, from, cooldownType, onRemove, ticksLeft, maxStacks, independentCooldown, new ArrayList<>());
    }

    public StackableCooldown(
            String name,
            String nameAbbreviation,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
            int ticksLeft,
            int maxStacks,
            boolean independentCooldown,
            List<TriConsumer<StackableCooldown<T>, Integer, Integer>> triConsumers
    ) {
        this(name, nameAbbreviation, cooldownClass, cooldownObject, from, cooldownType, onRemove, true, ticksLeft, maxStacks, independentCooldown, triConsumers);
    }

    public StackableCooldown(
            String name,
            String nameAbbreviation,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
            boolean removeOnDeath,
            int ticksLeft,
            int maxStacks,
            boolean independentCooldown,
            List<TriConsumer<StackableCooldown<T>, Integer, Integer>> triConsumers
    ) {
        this(name, nameAbbreviation, cooldownClass, cooldownObject, from, cooldownType, onRemove, cooldownManager -> {}, removeOnDeath, ticksLeft, maxStacks, independentCooldown, triConsumers);
    }

    public StackableCooldown(
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
            int maxStacks,
            boolean independentCooldown,
            List<TriConsumer<StackableCooldown<T>, Integer, Integer>> triConsumers
    ) {
        super(name, nameAbbreviation, cooldownClass, cooldownObject, from, cooldownType, onRemove, onRemoveForce, removeOnDeath);
        this.startingTicks = ticksLeft;
        this.ticksLeft = ticksLeft;
        this.maxStacks = maxStacks;
        this.independentCooldown = independentCooldown;
        this.consumers.addAll(triConsumers);
        this.stackDurations.add(ticksLeft);
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
                Component.text(nameAbbreviation, customActionBarColor() != null ? customActionBarColor() : cooldownType.getTextColor()),
                Component.text("(" + getCurrentStacks() + ")", NamedTextColor.YELLOW),
                Component.text(":", NamedTextColor.GRAY),
                Component.text(ticksLeft / 20 + 1, NamedTextColor.GOLD)
        );
    }

    @Override
    public void onTick(WarlordsEntity from) {
        consumers.forEach(consumer -> consumer.accept(this, ticksLeft, ticksElapsed));
        ticksElapsed++;

        if (independentCooldown) {
            // Tick down each stack independently
            for (int i = 0; i < stackDurations.size(); i++) {
                stackDurations.set(i, stackDurations.get(i) - 1);
            }
            // Remove expired stacks
            stackDurations.removeIf(duration -> duration <= 0);
            // Update ticksLeft to the longest remaining duration
            ticksLeft = stackDurations.isEmpty() ? 0 : stackDurations.stream().max(Integer::compareTo).orElse(0);
        } else {
            // All stacks share the same cooldown
            subtractTime(1);
        }
    }

    @Override
    public boolean removeCheck() {
        return ticksLeft <= 0 || stackDurations.isEmpty();
    }

    public void addStack() {
        // Add a new stack
        stackDurations.add(getStartingTicks());
        if (stackDurations.size() > maxStacks) {
            // Remove the oldest stack if at max stacks
            stackDurations.removeFirst();
        }
        if (!independentCooldown) {
            // Refresh all stack durations
            stackDurations.replaceAll(d -> getStartingTicks());
        }
        ticksLeft = getStartingTicks();
    }

    public void removeStack() {
        if (!stackDurations.isEmpty()) {
            // Remove the oldest stack
            stackDurations.removeFirst();
        }
        if(stackDurations.isEmpty()) {
            return;
        }
        if (independentCooldown) {
            // Update ticksLeft to the longest remaining duration
            ticksLeft = stackDurations.stream().max(Integer::compareTo).orElse(0);
        } else {
            // Refresh all stack durations
            stackDurations.replaceAll(d -> startingTicks);
            ticksLeft = startingTicks;
        }
    }

    public void subtractTime(int amount) {
        if (this.ticksLeft - amount <= 0) {
            ticksLeft = 0;
        } else {
            this.ticksLeft -= amount;
        }
    }

    public int getCurrentStacks() {
        return stackDurations.size();
    }

    public int getMaxStacks() {
        return maxStacks;
    }

    public boolean isAtMaxStacks() {
        return getCurrentStacks() >= maxStacks;
    }

    public boolean isIndependentCooldown() {
        return independentCooldown;
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

    public int getTicksElapsed() {
        return ticksElapsed;
    }

    public void addTriConsumer(TriConsumer<StackableCooldown<T>, Integer, Integer> triConsumer) {
        this.consumers.add(triConsumer);
    }

    public void removeTriConsumer(TriConsumer<StackableCooldown<T>, Integer, Integer> triConsumer) {
        this.consumers.remove(triConsumer);
    }

    public List<TriConsumer<StackableCooldown<T>, Integer, Integer>> getConsumers() {
        return consumers;
    }

    public boolean isEnhanced() {
        return enhanced;
    }

    public void setEnhanced(boolean enhanced) {
        this.enhanced = enhanced;
    }

    public List<Integer> getStackDurations() {
        return stackDurations;
    }
}